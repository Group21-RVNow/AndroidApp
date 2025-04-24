package com.example.rvnow.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class RV(
    val id: String = "",
    @get:PropertyName("isForSale") val isForSale: Boolean = true,
    val ownerId: String = "",
    val name: String = "",
    val type: RVType = RVType.Sales,
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    @get:PropertyName("place") val place: String = "",
    val additionalImages: List<String> = listOf(),
    val insurance: Map<String, String> = emptyMap(),
    val driverLicenceRequired: String? = null,
    val kilometerLimitation: Int? = null,
    @get:PropertyName("isForRental") var isForRental: Boolean = false,
    @get:PropertyName("isPopular") val isPopular: Boolean = true,
    val status: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val bookedDates: List<Map<String, Timestamp>> = listOf(),
    var isFavorite: Boolean = false,
    @get:PropertyName("price")
    var price: Double = 0.0
)



