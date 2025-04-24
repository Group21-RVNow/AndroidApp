
package com.example.rvnow.api
import android.util.Log
import com.example.rvnow.model.CartItem
import com.example.rvnow.model.Comment
import com.example.rvnow.model.Favourite
import com.example.rvnow.model.RV
import com.example.rvnow.model.RVType
import com.example.rvnow.model.Rating
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay


class RVInformation {

    private val db = FirebaseFirestore.getInstance()
    private val rvCollection = db.collection("rvs")


    // Fetch all RVs from the collection

    suspend fun fetchAllRVs(): List<RV> {
        return try {
            val snapshot = rvCollection.get().await()
            println("DEBUG: Firestore snapshot size = ${snapshot.documents.size}")

            val rvList = snapshot.documents.mapNotNull { it.toObject(RV::class.java) }

            println("DEBUG: Parsed RV list size = ${rvList.size}")
            rvList
        } catch (e: Exception) {
            println("DEBUG: Firestore error - ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchLastRVId(): String? {
        return try {
            val snapshot = rvCollection
                .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val lastRV = snapshot.documents.firstOrNull()
            lastRV?.id // or lastRV?.getString("id") if ID is stored as a field
        } catch (e: Exception) {
            println("DEBUG: Error fetching last RV ID - ${e.message}")
            null
        }
    }


    fun addComment(rvId: String, comment: Comment) {
        val commentRef = FirebaseFirestore.getInstance()
            .collection("rvs")           // The "rvs" collection
            .document(rvId)             // The specific RV document ID
            .collection("comments")     // The "comments" subcollection

        val commentWithId =
            comment.copy(id = commentRef.document().id)  // Set the Firestore generated ID to the comment

        // Now add the comment to Firestore
        commentRef.add(commentWithId)
            .addOnSuccessListener {
                Log.d("Firestore", "Comment added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding comment", e)
            }
    }


    //    fun addRating(rvId: String, comment: Rating) {
//        val ratingRef = FirebaseFirestore.getInstance()
//            .collection("rvs")           // The "rvs" collection
//            .document(rvId)             // The specific RV document ID
//            .collection("ratings")     // The "comments" subcollection
//
//        val ratingWithId = comment.copy(id = ratingRef.document().id)  // Set the Firestore generated ID to the comment
//
//        // Now add the comment to Firestore
//        ratingRef.add(ratingWithId)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Comment added successfully!")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error adding comment", e)
//            }
//    }
//    fun addRating(rvId: String, rating: Rating) {
//        val db = FirebaseFirestore.getInstance()
//        val ratingRef = db.collection("rvs")           // The "rvs" collection
//            .document(rvId)                            // The specific RV document ID
//            .collection("ratings")                     // The "ratings" subcollection
//            .document(rating.userId)                   // Use userId as the document ID
//
//        ratingRef.set(rating)                          // Set the rating document
//            .addOnSuccessListener {
//                Log.d("Firestore", "Rating added successfully!")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error adding rating", e)
//            }
//    }

    fun addRating(rvId: String, rating: Rating, onUpdated: (Double) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val ratingRef = db.collection("rvs")
            .document(rvId)
            .collection("ratings")
            .document(rating.userId)

        ratingRef.set(rating)
            .addOnSuccessListener {
                Log.d("Firestore", "Rating added successfully!")

                // 🟡 Fetch all ratings to recalculate average
                db.collection("rvs")
                    .document(rvId)
                    .collection("ratings")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val ratings = snapshot.documents.mapNotNull { doc ->
                            doc.getDouble("score")  // assuming your Rating has a 'score' field
                        }
                        val avg = if (ratings.isNotEmpty()) ratings.average() else 0.0
                        onUpdated(avg)  // 🔁 Callback to update UI
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding rating", e)
            }
    }


    // Add this to your RVApiService class
    suspend fun addToFavorites(
        userId: String,
        rvId: String,
        name: String,
        imageUrl: String,
        isForRental: Boolean,
        isForSale: Boolean
    ): Boolean {
        return try {
            val favoritesRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(rvId)

            // Create a document with just the RV ID (you can add more fields if needed)
            favoritesRef.set(
                mapOf(

                    "rvId" to rvId,
                    "isForRental" to isForRental,
                    "imageUrl" to imageUrl,
                    "name" to name,
                    "isForSale" to isForSale,
                    "createdat" to FieldValue.serverTimestamp()
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(
        userId: String,
        rvId: String,
        name: String,
        imageUrl: String,
        isForRental: Boolean,
        isForSale: Boolean
    ): Boolean {
        return try {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(rvId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkIfFavorite(userId: String, rvId: String): Boolean {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(rvId)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }


    fun getAverageRating(rvId: String, onResult: (Float) -> Unit) {
        val ratingsRef = db.collection("rvs")
            .document(rvId)
            .collection("ratings")

        val query = ratingsRef.aggregate(
            AggregateField.average("rating")
        )

        query.get(AggregateSource.SERVER)
            .addOnSuccessListener { result ->

                val average = result.get(AggregateField.average("rating")) ?: 0.0
                Log.d("AVERAGE_CHECK", "Average rating: $average")
                onResult(average.toFloat())
            }
            .addOnFailureListener { exception ->
                Log.e("FIREBASE", "Error getting avg: ${exception.message}")
                onResult(0f) // fallback
            }
    }


    fun fetchComments(rvId: String, onCommentsFetched: (List<Comment>) -> Unit) {
        db.collection("rvs")
            .document(rvId)
            .collection("comments")
            .orderBy("createdat", Query.Direction.DESCENDING)
            .get() // Use 'get()' to fetch data once
            .addOnSuccessListener { snapshot ->
                val comments = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                onCommentsFetched(comments)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching comments", e)
            }
    }


    // In your RVApiService or similar
    suspend fun addToCart(userId: String, rvId: String, rvData: Map<String, Any>): Boolean {
        return try {
            val cartRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("cart")
                .document(rvId) // Using RV ID as document ID for easy updates

            cartRef.set(
                rvData + mapOf(
                    "addedAt" to FieldValue.serverTimestamp(),
//                "quantity" to FieldValue.increment(1) // For quantity management
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromCart(userId: String, rvId: String): Boolean {
        return try {
            val cartRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("cart")
                .document(rvId)

            // Actually delete the document from the cart
            cartRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun fetchedCartItems(userId: String, onCartItemsFetched: (List<CartItem>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("cart")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Error fetching cart items", e)
                    return@addSnapshotListener
                }

                val cartItems =
                    snapshot?.documents?.mapNotNull { it.toObject(CartItem::class.java) }
                onCartItemsFetched(cartItems ?: emptyList())
            }
    }

    fun fetchedFavorites(userId: String, onFavoritesFetched: (List<Favourite>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Error fetching favorites", e)
                    return@addSnapshotListener
                }

                val favorites = snapshot?.documents?.mapNotNull { it.toObject(Favourite::class.java) }
                onFavoritesFetched(favorites ?: emptyList())
            }
    }


    fun updateRV(rv: RV): Task<Void> {
        return db.collection("rvs")
            .document(rv.id)
            .set(rv)
    }


    suspend fun removeFromRV(userId: String, rvId: String): Boolean {
        return try {
            val cartRef = FirebaseFirestore.getInstance()
                .collection("rvs")
                .document(rvId)

            // Delete the entire document
            cartRef.delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace() // Optional: log error for debugging
            false
        }
    }




//    suspend fun getAllFavorites(userId: String): List<Favorite> {
//            if (userId.isBlank()) {
//                Log.e("Firestore", "Invalid userId: '$userId'")
//                return emptyList()
//            }
//            return try {
//                val snapshot = db.collection("users")
//                    .document(userId)
//                    .collection("favorites")
//                    .get()
//                    .await()
//
//                val favourites = snapshot.documents.mapNotNull {
//                    val fav = it.toObject(Favorite::class.java)
//                    Log.d("Firestore", "Fetched favorite: $fav")
//                    fav
//                }
//
//                Log.d("Firestore", "Fetched ${favourites.size} favorites from Firestore")
//                favourites
//            } catch (e: Exception) {
//                Log.e("Firestore", "Error fetching favorites", e)
//                emptyList()
//            }
//        }


    // In RVInformation.kt
//    suspend fun getAllFavorites(userId: String): List<Favourite> {
//        return try {
//            val querySnapshot = db.collection("users")
//                .document(userId)
//                .collection("favorites")
//                .limit(50) // Add reasonable limits
//                .get()
//                .await()
//
//            Log.d("Firestore", "Raw favorites docs: ${querySnapshot.documents}")
//
//            querySnapshot.toObjects(Favourite::class.java).also {
//                Log.d("Firestore", "Parsed ${it.size} favorites")
//            }
//        } catch (e: Exception) {
//            Log.e("Firestore", "Error getting favorites", e)
////            emptyList()
//            throw e
//        }
//
//    }


    suspend fun addAllRV(rv: RV) {
        try {

            rvCollection.document(rv.id).set(rv).await()
            Log.d("Firestore", "Document added/updated with ID: ${rv.id}")

        } catch (e: Exception) {
            Log.e("Firestore", "Error adding RVs: ${e.message}", e)
            throw e
        }
    }


    // Helper function to retrieve download URL for an image
//    fun getDownloadUrlForImage(imagePath: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
//        val storage = FirebaseStorage.getInstance()
//        val storageRef = storage.reference
//
//        val imagePaths = listOf("RVNow_Image/1.png", "RVNow_Image/2.png", "RVNow_Image/3.png")
//
//        val downloadUrls = mutableListOf<String>()
//
//        imagePaths.forEach { path ->
//            val imageRef = storageRef.child(path)
//            imageRef.downloadUrl.addOnSuccessListener { uri ->
//                downloadUrls.add(uri.toString())
//                if (downloadUrls.size == imagePaths.size) {
//                    // All URLs fetched, proceed to update RV data
//                    updateRvData(downloadUrls)
//                }
//            }.addOnFailureListener { exception ->
//                // Handle any errors
//                Log.e("Firebase", "Error fetching image URL: ${exception.message}")
//            }
//        }
//    }

//    fun updateRvData(imageUrls: List<String>) {
//        val rvNewList = (1..4).map { index ->
//            mapOf(
//                "id" to "rv${index.toString().padStart(3, '0')}",
//                "ownerId" to "owner${index.toString().padStart(3, '0')}",
//                "name" to when (index) {
//                    1 -> "Explorer 2025"
//                    2 -> "Cozy Cruiser"
//                    3 -> "Mountain Trekker"
//                    4 -> "Urban Nomad"
//                    else -> "Pacific Wanderer"
//                },
//                "type" to if (index % 2 == 0) "Sales" else "Rental",
//                "description" to "This is RV number $index with high-end features and adventure-ready design.",
//                "pricePerDay" to 80.0 + (index * 10),
//                "imageUrl" to imageUrls.getOrNull(index - 1) ?: "", // Assign fetched URL
//                "place" to when (index) {
//                    1 -> "New York, USA"
//                    2 -> "Austin, TX"
//                    3 -> "Denver, CO"
//                    4 -> "Los Angeles, CA"
//                    else -> "Miami, FL"
//                },
//                "additionalImages" to (1..10).map { i ->
//                    "https://storage.cloud.google.com/rvnow-72045.firebasestorage.app/RVNow_Image/${index}.$i.png"
//                },
//                "insurance" to mapOf("type" to "Comprehensive", "company" to "InsureCo $index"),
//                "driverLicenceRequired" to if (index % 2 == 0) "C" else "B",
//                "kilometerLimitation" to 200 + (index * 10),
//                "isForSale" to (index % 2 == 0),
//                "isForRental" to (index % 2 != 0),
//                "isPopular" to (index % 3 == 0),
//                "status" to "Available",
//                "createdAt" to Timestamp.now(),
//                "bookedDates" to listOf(
//                    mapOf(
//                        "start" to Timestamp.now(),
//                        "end" to Timestamp.now()
//                    )
//                ),
//                "price" to if (index % 2 == 0) 18000.0 + (index * 500) else 80.0 + (index * 10)
//            )
//        }
//
//        addRVsToFirestore(rvNewList)
//    }


    fun addRVsToFirestore(rvList: List<Map<String, Any>>) {
        val db = FirebaseFirestore.getInstance()
        val rvCollection = db.collection("rvs")

        rvList.forEach { rv ->
            rvCollection.add(rv)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Document added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding document", e)
                }
        }
    }


    // Example usage to populate Firestore with updated image URLs
//    suspend fun populateFirestoreWithUpdatedImages() {
//        val updatedRvList = updateRvNewListWithImageUrls()
//        addUpdatedRVs(updatedRvList)
//    }

    fun verifyRVsInFirestore() {
        rvCollection.get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("Firestore", "No RV documents found.")
                } else {
                    for (document in result) {
                        Log.d("Firestore", "RV ID: ${document.id}, Data: ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error retrieving RV documents: ", exception)
            }
    }





}



//    fun addComment(rvId: String, comment: Comment) {
//        val commentRef = FirebaseFirestore.getInstance()
//            .collection("rvs")           // The "rv" collection
//            .document(rvId)             // The specific RV document ID
//            .collection("comments")     // The "comments" subcollection
//            .document()                 // Firestore automatically generates an ID for the comment
//
//        val commentWithId = comment.copy(id = commentRef.id)  // Set the Firestore generated ID to the comment
//
//        // Now add the comment to Firestore
//        commentRef.set(commentWithId)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Comment added successfully!")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error adding comment", e)
//            }
//    }



//
//    fun getAllFavorites(userId: String, onFavouriteFetched: (List<Favourite>) -> Unit) {
//        db.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("Firestore", "Error fetching favourites", e)
//                    return@addSnapshotListener
//                }
//
//                Log.d("Firestore", "Fetched snapshot: ${snapshot?.documents?.size ?: 0} documents")
//
//                val favorites = snapshot?.documents?.mapNotNull {
//                    Log.d("Firestore", "Raw document: ${it.data}")
//                    val fav = it.toObject(Favourite::class.java)
//                    Log.d("Firestore", "Mapped to Favourite: $fav")
//                    fav
//                }
//
//                Log.d("Firestore", "Final favourites list: $favorites")
//                onFavouriteFetched(favorites ?: emptyList())
//            }
//    }

//    fun getAllFavorites(userId: String, onFavouriteFetched: (List<Favorite>) -> Unit) {
//        db.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("Firestore", "Error fetching favourites", e)
//                    return@addSnapshotListener
//                }
//
//                val fetchedFavourites = snapshot?.documents?.mapNotNull { it.toObject(Favorite::class.java) }
//                onFavouriteFetched(fetchedFavourites ?: emptyList())
//            }
//
//    fun getAllFavorites(userId: String, onFavouriteFetched: (List<Favorite>) -> Unit) {
//        db.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("Firestore", "Error fetching favourites", e)
//                    return@addSnapshotListener
//                }
//
//                val fetchedFavourites = snapshot?.documents?.mapNotNull {
//                    val fav = it.toObject(Favorite::class.java)
//                    Log.d("Firestore", "Fetchedfavorite from api: $fav")
//                    fav
//                }
//
//                Log.d("Firestore", "Fetchedfavorite ${fetchedFavourites?.size} favourites from api")
//                onFavouriteFetched(fetchedFavourites ?: emptyList())
//            }


//suspend fun getAllFavorites(userId: String): List<Favorite> {
//    if (userId.isBlank()) {
//        Log.e("Firestore", "Invalid userId: '$userId'")
//        return emptyList()
//    }
//
//    return try {
//        val snapshot = db.collection("users")
//            .document(userId)
//            .collection("favorites")
//            .get()
//            .await()
//
//        snapshot.documents.mapNotNull {
//            val fav = it.toObject(Favorite::class.java).also {
//                Log.d("Firestore", "Fetched favorite: $it")
//            }
//        }.also {
//            Log.d("Firestore", "Fetched ${it.size} favorites from Firestore")
//        }
//    } catch (e: Exception) {
//        Log.e("Firestore", "Error fetching favorites", e)
//        emptyList()
//    }
//}








//                snapshot?.let {
//                    val favorites = it.documents.mapNotNull { doc ->
//                        try {
//                            val data = doc.data ?: return@mapNotNull null
//                            Favourite(
//                                rvId = doc.id,
//                                name = data["name"] as? String ?: "",
//                                imageUrl = data["imageUrl"] as? String ?: "",
//                                isForRental = data["isForRental"] as? Boolean ?: false,
//                                isForSale = data["isForSale"] as? Boolean ?: false,
//                                createdat = data["createdat"] as? Timestamp ?: Timestamp.now()
//                            )
//                        } catch (e: Exception) {
//                            Log.e("Firestore", "Error mapping document ${doc.id}", e)
//                            null
//                        }
//                    }
//                    onFavouriteFetched(favorites)
//                }
//            }


// Add an RV to the collection










//        hashMapOf(
//            "id" to "rv002",
//            "ownerId" to "owner002",
//            "name" to "Luxury RV 2024",
//            "type" to "Sales",
//            "description" to "A high-end RV with all the comforts of home.",
//            "pricePerDay" to 250.0,
//            "imageUrl" to "https://example.com/images/rv002.jpg",
//            "place" to "Los Angeles, California",
//            "additionalImages" to listOf("https://example.com/images/rv002_1.jpg", "https://example.com/images/rv002_2.jpg"),
//            "insurance" to mapOf("type" to "Full Coverage", "company" to "XYZ Insurance"),
//            "driverLicenceRequired" to "No",
//            "kilometerLimitation" to 400,
//            "isForSale" to true,
//            "status" to "For Sale",
//            "createdAt" to Timestamp.now(),
//            "bookedDates" to listOf(
//                hashMapOf("startDate" to Timestamp.now(), "endDate" to Timestamp.now())
//            )
//        ),
//        hashMapOf(
//            "id" to "rv003",
//            "ownerId" to "owner003",
//            "name" to "Mountain Adventure",
//            "type" to "Rental",
//            "description" to "Designed for mountain trips, with extra space for equipment.",
//            "pricePerDay" to 150.0,
//            "imageUrl" to "https://example.com/images/rv003.jpg",
//            "place" to "Denver, Colorado",
//            "additionalImages" to listOf("https://example.com/images/rv003_1.jpg", "https://example.com/images/rv003_2.jpg"),
//            "insurance" to mapOf("type" to "Third Party", "company" to "MNO Insurance"),
//            "driverLicenceRequired" to "Yes",
//            "kilometerLimitation" to 500,
//            "isForSale" to false,
//            "status" to "Available",
//            "createdAt" to Timestamp.now(),
//            "bookedDates" to listOf(
//                hashMapOf("startDate" to Timestamp.now(), "endDate" to Timestamp.now())
//            )
//        ),
//        hashMapOf(
//            "id" to "rv004",
//            "ownerId" to "owner004",
//            "name" to "Coastal Explorer",
//            "type" to "Rental",
//            "description" to "Ideal for long road trips along the coast, with a spacious interior.",
//            "pricePerDay" to 180.0,
//            "imageUrl" to "https://example.com/images/rv004.jpg",
//            "place" to "Miami, Florida",
//            "additionalImages" to listOf("https://example.com/images/rv004_1.jpg", "https://example.com/images/rv004_2.jpg"),
//            "insurance" to mapOf("type" to "Comprehensive", "company" to "DEF Insurance"),
//            "driverLicenceRequired" to "Yes",
//            "kilometerLimitation" to 600,
//            "isForSale" to true,
//            "status" to "For Sale",
//            "createdAt" to Timestamp.now(),
//            "bookedDates" to listOf(
//                hashMapOf("startDate" to Timestamp.now(), "endDate" to Timestamp.now())
//            )
//        ),
//        hashMapOf(
//            "id" to "rv005",
//            "ownerId" to "owner005",
//            "name" to "Family RV",
//            "type" to "Rental",
//            "description" to "Perfect for family road trips with enough space for everyone.",
//            "pricePerDay" to 160.0,
//            "imageUrl" to "https://example.com/images/rv005.jpg",
//            "place" to "Austin, Texas",
//            "additionalImages" to listOf("https://example.com/images/rv005_1.jpg", "https://example.com/images/rv005_2.jpg"),
//            "insurance" to mapOf("type" to "Third Party", "company" to "XYZ Insurance"),
//            "driverLicenceRequired" to "Yes",
//            "kilometerLimitation" to 450,
//            "isForSale" to false,
//            "status" to "Available",
//            "createdAt" to Timestamp.now(),
//            "bookedDates" to listOf(
//                hashMapOf("startDate" to Timestamp.now(), "endDate" to Timestamp.now())
//            )
//        )
//    )









