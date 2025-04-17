package com.example.rvnow.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class CartItem(
    val id: String = "",
    val rvId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val pricePerDay: Double = 0.0,
    val price:Double = 0.0,
    val quantity: Int = 1,

    @get:PropertyName("createdAt")
    val createdAt: Timestamp = Timestamp.now()
)