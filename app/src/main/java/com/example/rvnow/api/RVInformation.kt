
package com.example.rvnow.api
import android.util.Log
import com.example.rvnow.model.CartItem
import com.example.rvnow.model.Comment
import com.example.rvnow.model.Favorite
import com.example.rvnow.model.RV
import com.example.rvnow.model.Rating
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
//import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


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

        val commentWithId = comment.copy(id = commentRef.document().id)  // Set the Firestore generated ID to the comment

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
    fun addRating(rvId: String, rating: Rating) {
        val db = FirebaseFirestore.getInstance()
        val ratingRef = db.collection("rvs")           // The "rvs" collection
            .document(rvId)                            // The specific RV document ID
            .collection("ratings")                     // The "ratings" subcollection
            .document(rating.userId)                   // Use userId as the document ID

        ratingRef.set(rating)                          // Set the rating document
            .addOnSuccessListener {
                Log.d("Firestore", "Rating added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding rating", e)
            }
    }

    // Add this to your RVApiService class
    suspend fun addToFavorites(userId: String, rvId: String,name:String,imageUrl:String,isForRental:Boolean, isForSale:Boolean): Boolean {
        return try {
            val favoritesRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(rvId)

            // Create a document with just the RV ID (you can add more fields if needed)
            favoritesRef.set(mapOf(

                "rvId" to rvId,
                "isForRental" to isForRental,
                "imageUrl" to imageUrl,
                "name" to name,
                "isForSale" to isForSale,
                "createdat" to FieldValue.serverTimestamp()
            )).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(userId: String, rvId: String,name:String, imageUrl:String,isForRental:Boolean,isForSale:Boolean): Boolean {
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

            cartRef.set(rvData + mapOf(
                "addedAt" to FieldValue.serverTimestamp(),
//                "quantity" to FieldValue.increment(1) // For quantity management
            )).await()
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

                val cartItems = snapshot?.documents?.mapNotNull { it.toObject(CartItem::class.java) }
                onCartItemsFetched(cartItems ?: emptyList())
            }
    }




    suspend fun getAllFavorites(userId: String): List<Favorite> {
            if (userId.isBlank()) {
                Log.e("Firestore", "Invalid userId: '$userId'")
                return emptyList()
            }
            return try {
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .get()
                    .await()

                val favourites = snapshot.documents.mapNotNull {
                    val fav = it.toObject(Favorite::class.java)
                    Log.d("Firestore", "Fetched favorite: $fav")
                    fav
                }

                Log.d("Firestore", "Fetched ${favourites.size} favorites from Firestore")
                favourites
            } catch (e: Exception) {
                Log.e("Firestore", "Error fetching favorites", e)
                emptyList()
            }
        }


    suspend fun addAllRV(rv:RV) {
        try {

            rvCollection.document(rv.id).set(rv).await()
            Log.d("Firestore", "Document added/updated with ID: ${rv.id}")
            // Use await() to properly suspend execution until Firestore operation completes
//                rvCollection.document(rvObject.id).set(rvObject).await()

//                Log.d("Firestore", "Document added/updated with ID: ${rvObject.id}")

        } catch (e: Exception) {
            Log.e("Firestore", "Error adding RVs: ${e.message}", e)
            throw e
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










