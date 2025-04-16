package com.example.rvnow.model

import com.google.firebase.Timestamp

data class CartItem(
    val id: String = "",
    val rvId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val pricePerDay: Double = 0.0,
    val price:Double = 0.0,
    val quantity: Int = 1,
    val createdAt: Timestamp = Timestamp.now()
)