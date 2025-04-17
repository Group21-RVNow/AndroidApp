package com.example.rvnow.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName



data class Favorite(
    @get:PropertyName("rvId")
    val rvId: String = "",

    @get:PropertyName("name")
    val name: String = "",

    @get:PropertyName("imageUrl")
    val imageUrl: String = "",

    @get:PropertyName("isForRental")
    var isForRental: Boolean = false,

    @get:PropertyName("isForSale")
    var isForSale: Boolean = false,

    @get:PropertyName("createdat")
    val createdat: Timestamp = Timestamp.now()
)
