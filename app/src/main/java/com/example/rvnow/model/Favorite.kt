package com.example.rvnow.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName



data class Favourite(
    @get:PropertyName("rvId") val rvId: String = "",

    @get:PropertyName("name") val name: String = "",

//    @get:PropertyName("imageUrl") val imageUrl: String = "",
    val imageUrl: String = "",

    @get:PropertyName("isForSale") val isForSale: Boolean = false,
    @get:PropertyName("isForRental") var isForRental: Boolean = false,
//    @get:PropertyName("isForRental")
//    var isForRental: Boolean = false,

//    @get:PropertyName("isForSale")
//    var isForSale: Boolean = false,
    val createdat: Timestamp = Timestamp.now(),

//    @get:PropertyName("createdat")
//    val createdat: Timestamp = Timestamp.now()
)
