package com.example.rvnow.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rvnow.api.RVInformation
import com.example.rvnow.model.RV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateMapOf
import com.example.rvnow.model.Comment
import kotlinx.coroutines.Job
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import com.example.rvnow.model.CartItem
import com.example.rvnow.model.Favourite
import com.example.rvnow.model.Rating

import com.example.rvnow.model.RVType
import com.google.firebase.Timestamp
import kotlinx.coroutines.CancellationException
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Empty<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
class RVViewModel : ViewModel() {
    private val rvApiService = RVInformation()

    //    private val rvApiService = RVInformation()
    private val _rvs = MutableStateFlow<List<RV>>(emptyList())
    val rvs: StateFlow<List<RV>> = _rvs

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _commentStatus = MutableStateFlow<String?>(null)
    val commentStatus: StateFlow<String?> = _commentStatus

    private var commentListenerJob: Job? = null
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private var ratingListenerJob: Job? = null
    private val _ratings = MutableStateFlow<List<Comment>>(emptyList())
    val ratings: StateFlow<List<Comment>> = _ratings

    private val _fetchedFavourites = MutableStateFlow<List<Favourite>>(emptyList())
    val fetchedFavourites: StateFlow<List<Favourite>> = _fetchedFavourites

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

//    private val _fetchedFavourites= MutableStateFlow<List<Favorite>>(emptyList())
//    val fetchedFavourites: StateFlow<List<Favorite>> = _fetchedFavourites

    private val _averageRating = MutableStateFlow(0f) // Default to 0f
    val averageRating: StateFlow<Float> = _averageRating

    // 本地收藏状态管理
    private val _favorites = mutableStateMapOf<String, Boolean>()
//    val favoriteRVs = rvList.filter { it.id in favoriteRVIds }



    init {
        fetchRVs()

//        viewModelScope.launch {
//            authViewModel.currentUser.collect { user ->
//                user?.uid?.let { uid ->
//                    loadFavorites(uid)
//                }
//            }
//        }
    }

    fun fetchRVs() {
        viewModelScope.launch {
            _loading.value = true
            try {
                var fetchedRVs = rvApiService.fetchAllRVs()
                _rvs.value = fetchedRVs
            } catch (e: Exception) {
                _error.value = "Failed to fetch RVs: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    fun addComment(rvId: String, comment: Comment, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                rvApiService.addComment(rvId, comment)
                _commentStatus.value = "Comment submitted successfully"
                loadComments(rvId)
                onComplete()
            } catch (e: Exception) {
                _commentStatus.value = "Failed to submit comment: ${e.message}"
            }
        }
    }


//    fun addRating(rvId: String, rating: Rating, onComplete: () -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                rvApiService.addRating(rvId, rating)
//                _commentStatus.value = "Rating submitted successfully"
//
//                updateAverageRating(rvId, rating.rating)
//                loadAverageRating(rvId)
//
//                // This is now just a regular callback
//                onComplete()
//            } catch (e: Exception) {
//                _commentStatus.value = "Failed to submit rating: ${e.message}"
//            }
//        }
//    }


    //  fxi the detail page problem: average rating is not immedatly after the submission
    fun addRating(rvId: String, rating: Rating, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                rvApiService.addRating(rvId, rating, onUpdated = {
                    // This will run AFTER rating is saved
                    loadAverageRating(rvId)
                })

                _commentStatus.value = "Rating submitted successfully"
                onComplete()
            } catch (e: Exception) {
                _commentStatus.value = "Failed to submit rating: ${e.message}"
            }
        }
    }






    // A function to update the average rating in the ViewModel after a new rating is added
    private fun updateAverageRating(rvId: String, averageRating: Float) {
        _averageRatings.value = _averageRatings.value.toMutableMap().apply {
            this[rvId] = averageRating
        }
    }

    fun loadAverageRating(rvId: String) {
        viewModelScope.launch {
            try {
                rvApiService.getAverageRating(rvId) { averageRating ->
                    // Update the state using the controlled method
                    updateAverageRating(rvId, averageRating)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading average rating: ${e.message}")
            }
        }
    }

    private val _averageRatings = MutableStateFlow<Map<String, Float>>(emptyMap())
    val averageRatings: StateFlow<Map<String, Float>> = _averageRatings


    fun loadComments(rvId: String, onComplete: () -> Unit = {}) {
        commentListenerJob?.cancel() // Cancel previous listener if any

        commentListenerJob = viewModelScope.launch {
            Log.d("RVViewModel", "Loading comments for RV ID: $rvId")

            // Call the fetchComments API and handle the result via the callback
            rvApiService.fetchComments(rvId) { commentList ->
                Log.d("RVViewModel", "Comments loaded: $commentList")
                _comments.value = commentList
                onComplete() // Invoke the callback after loading comments
            }
        }
    }



    fun checkFavoriteStatus(userId: String, rvId: String, onComplete: (Boolean) -> Unit = { _ -> }) {
        viewModelScope.launch {
            try {
                val isFavorite = rvApiService.checkIfFavorite(userId, rvId)
                _favorites[rvId] = isFavorite
                updateLocalFavoriteStatus(rvId)
                onComplete(isFavorite)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
    // In RVViewModel
    fun toggleFavorite(userId: String, rvId: String,name:String, imageUrl:String,isForRental: Boolean,
                       isForSale: Boolean, onComplete: (Boolean) -> Unit = { _ -> }) {
        val currentlyFavorite = _favorites[rvId] ?: false
        // Optimistic update - change UI immediately
        _favorites[rvId] = !currentlyFavorite
        updateLocalFavoriteStatus(rvId)

        viewModelScope.launch {
            try {
                val success = if (currentlyFavorite) {
                    rvApiService.removeFromFavorites(userId, rvId,name,imageUrl,isForRental,isForSale)
                } else {
                    rvApiService.addToFavorites(userId, rvId, name,imageUrl,isForRental,isForSale)
                }

                if (success) {
                    _favorites[rvId] = !currentlyFavorite
                    updateLocalFavoriteStatus(rvId)
                }
                onComplete(success)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    private val rvInformation = RVInformation()  // Assuming RVInformation is the class with getAllFavorites


//    private val _fetchedFavourites = MutableStateFlow<List<Favorite>>(emptyList())
//    val fetchedFavourites: StateFlow<List<Favorite>> = _fetchedFavourites



    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            try {
                rvInformation.fetchedFavorites(userId) { favorites ->
                    _fetchedFavourites.value = favorites
                    Log.d("ViewModel", "Fetched favorites: $favorites")
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching favorites", e)
                _fetchedFavourites.value = emptyList()
            }
        }
    }

    // Properly initialized MutableStateFlow
    private val _removeFromCartState = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val removeFromCartState: StateFlow<Resource<Boolean>> = _removeFromCartState

    fun removeFromCart(userId: String, rvId: String) {
        viewModelScope.launch {
            _removeFromCartState.value = Resource.Loading()
            try {
                val success = rvApiService.removeFromCart(userId, rvId)
                _removeFromCartState.value = Resource.Success(success)
            } catch (e: Exception) {
                _removeFromCartState.value = Resource.Error(
                    message = "Failed to remove item: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun resetRemoveFromCartState() {
        _removeFromCartState.value = Resource.Empty()
    }



//    fun loadFavorites(userId: String) {
//        viewModelScope.launch {
//            try {
//                val result = rvInformation.getAllFavorites(userId)
//                if (isActive) { // Check if coroutine is still active
//                    _fetchedFavourites.value = result
//                }
//            } catch (e: Exception) {
//                if (e !is CancellationException) { // Don't reset on normal cancellation
//                    Log.e("ViewModel", "Permanent error", e)
//                    _fetchedFavourites.value = emptyList()
//                }
//            }
//        }
//    }



    fun addToCart(
        userId: String,
        rv: RV,
        sourcePage: String,
        isForSale: Boolean,
        quantity: Int,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val isFromSales = sourcePage == "sales" || (sourcePage == "home" && isForSale)
                val isFromRental = sourcePage == "rental" || (sourcePage == "home" && !isForSale)

                val cartItemData = mapOf(
                    "rvId" to rv.id,
                    "name" to rv.name,
                    "imageUrl" to rv.imageUrl,
                    "price" to if (isFromSales) rv.price else 0.0,
                    "pricePerDay" to if (isFromRental) rv.pricePerDay else 0.0,
                    "quantity" to quantity
                )

                val success = rvApiService.addToCart(userId, rv.id, cartItemData)

                if (success) {
                    _cartItems.value += CartItem(
                        rvId = rv.id,
                        name = rv.name,
                        imageUrl = rv.imageUrl,
                        price = if (isFromSales) rv.price else 0.0,
                        pricePerDay = if (isFromRental) rv.pricePerDay else 0.0,
                        quantity = quantity
                    )
                }

                callback(success)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }


    // Function to fetch cart items from Firestore
    fun fetchCartItems(userId: String) {
        viewModelScope.launch {
            try {
                rvApiService.fetchedCartItems(userId) { cartItems ->
                    // Clear the list and add new items
                    _cartItems.value = cartItems // Directly assigning the new list
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }




    private fun updateLocalFavoriteStatus(rvId: String) {
        _rvs.value = _rvs.value.map { rv ->
            if (rv.id == rvId) rv.copy(isFavorite = _favorites[rv.id] ?: false) else rv
        }
    }

    // 保持原有方法不变
    fun fetchLastRVId() {
        viewModelScope.launch {
            try {
                val lastId = rvApiService.fetchLastRVId()
                println("DEBUG: Last RV ID = $lastId")
            } catch (e: Exception) {
                println("DEBUG: Error fetching last RV ID - ${e.message}")
            }
        }
    }

//    fun checkout(userId: String, callback: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            try {
////                1. Process payment (implement your payment logic)
//                val paymentSuccess = rvApiService.processPayment(userId, _cartItems)
//
////                2. If successful, clear cart
//                if (paymentSuccess) {
//                    _cartItems.clear()
//                    rvApiService.clearCart(userId)
//                    callback(true)
//                } else {
//                    callback(false)
//                }
//            } catch (e: Exception) {
//                callback(false)
//            }
//        }
//    }

    fun addRV(rv: RV) {
        viewModelScope.launch {
            _loading.value = true
            try {
                rvApiService.addAllRV(rv)  // Make sure this function only accepts a single RV
                fetchRVs()  // Refresh the list after adding
//                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                _error.value = "Failed to add RV: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }




    //    private val rvNewList = (1..10).map { index ->
//        RV(
//            id = "rv${index.toString().padStart(3, '0')}",
//            ownerId = "owner${index.toString().padStart(3, '0')}",
//            name = when (index) {
//                1 -> "Explorer 2025"
//                2 -> "Cozy Cruiser"
//                3 -> "Mountain Trekker"
//                4 -> "Urban Nomad"
//                5 -> "Desert Drifter"
//                6 -> "Forest Voyager"
//                7 -> "Sunset Seeker"
//                8 -> "River Runner"
//                9 -> "Skyline Roamer"
//                else -> "Pacific Wanderer"
//            },
//            type = if (index % 2 == 0) RVType.Sales else RVType.Rental,
//            description = "This is RV number $index with high-end features and adventure-ready design.",
//            pricePerDay = 80.0 + (index * 10),
//            imageUrl = "https://storage.cloud.google.com/rvnow-72045.firebasestorage.app/RVNow_Image/$index.png",
//            place = when (index) {
//                1 -> "New York, USA"
//                2 -> "Austin, TX"
//                3 -> "Denver, CO"
//                4 -> "Los Angeles, CA"
//                5 -> "Phoenix, AZ"
//                6 -> "Portland, OR"
//                7 -> "Salt Lake City, UT"
//                8 -> "San Francisco, CA"
//                9 -> "Chicago, IL"
//                else -> "Miami, FL"
//            },
//            additionalImages = (1..10).map { i ->
//                "https://storage.cloud.google.com/rvnow-72045.firebasestorage.app/RVNow_Image/${index}.$i.png"
//            },
//            insurance = mapOf("type" to "Comprehensive", "company" to "InsureCo $index"),
//            driverLicenceRequired = if (index % 2 == 0) "C" else "B",
//            kilometerLimitation = 200 + (index * 10),
//            isForSale = (index % 2 == 0),
//            isForRental = (index % 2 != 0),
//            isPopular = (index % 3 == 0),
//            status = "Available",
//            createdAt = Timestamp.now(),
//            bookedDates = listOf(
//                hashMapOf(
//                    "start" to Timestamp.now(),
//                    "end" to Timestamp.now()
//                )
//            ),
//            price = if (index % 2 == 0) 18000.0 + (index * 500) else 80.0 + (index * 10)
//        )
//    }
    private val rvNewList = (1..4).map { index ->  // Changed range from 1..10 to 1..4
        RV(
            id = "rv${index.toString().padStart(3, '0')}",
            ownerId = "owner${index.toString().padStart(3, '0')}",
            name = when (index) {
                1 -> "Explorer 2025"
                2 -> "Cozy Cruiser"
                3 -> "Mountain Trekker"
                4 -> "Urban Nomad"
                else -> "Pacific Wanderer"  // This is no longer needed
            },
            type = if (index % 2 == 0) RVType.Sales else RVType.Rental,
            description = "This is RV number $index with high-end features and adventure-ready design.",
            pricePerDay = 80.0 + (index * 10),
            imageUrl = "https://storage.cloud.google.com/rvnow-72045.firebasestorage.app/RVNow_Image/$index.png",
            place = when (index) {
                1 -> "New York, USA"
                2 -> "Austin, TX"
                3 -> "Denver, CO"
                4 -> "Los Angeles, CA"
                else -> "Miami, FL"  // This is also no longer needed
            },
            additionalImages = (1..10).map { i ->
                "https://storage.cloud.google.com/rvnow-72045.firebasestorage.app/RVNow_Image/${index}.$i.png"
            },
            insurance = mapOf("type" to "Comprehensive", "company" to "InsureCo $index"),
            driverLicenceRequired = if (index % 2 == 0) "C" else "B",
            kilometerLimitation = 200 + (index * 10),
            isForSale = (index % 2 == 0),
            isForRental = (index % 2 != 0),
            isPopular = (index % 3 == 0),
            status = "Available",
            createdAt = Timestamp.now(),
            bookedDates = listOf(
                hashMapOf(
                    "start" to Timestamp.now(),
                    "end" to Timestamp.now()
                )
            ),
            price = if (index % 2 == 0) 18000.0 + (index * 500) else 80.0 + (index * 10)
        )
    }


//    fun addRVDataToFirestore() {
//        viewModelScope.launch {
//            try {
//                rvApiService.addRVs(rvList=rvNewList) // Pass the list of RV objects to the API
//            } catch (e: Exception) {
//                Log.e("Firestore", "Failed to add RVs", e)
//            }
//        }
//    }


}



//    fun loadFavoriteRVIds(userId: String, onResult: (List<String>) -> Unit) {
//        viewModelScope.launch {
//            val favorites = rvApiService.getAllFavorites(userId)
//            onResult(favorites)
//        }
//
//    fun fetchCartItems(userId: String) {
//        viewModelScope.launch {
//            try {
//                rvApiService.fetchedCartItems(userId) { cartItems ->
//                    // Clear the list and add new items
//                    _cartItems.value = cartItems // Directly assigning the new list
//                }
//            } catch (e: Exception) {
//                // Handle error
//            }
//        }
//    }

//    fun loadFavorites(userId: String) {
//        viewModelScope.launch {
//            try {
//                rvApiService.getAllFavorites(userId) { fetchedFavourites ->
//                    // Clear the list and add new items
//                    _fetchedFavourites.value = fetchedFavourites // Directly assigning the new list
//                }
//            } catch (e: Exception) {
//                // Handle error
//            }
//        }
//    }











// 收藏/取消收藏
//    fun toggleFavorite(rvId: String) {
//        _favorites[rvId] = !(_favorites[rvId] ?: false)
//        updateLocalFavoriteStatus(rvId)
//    }