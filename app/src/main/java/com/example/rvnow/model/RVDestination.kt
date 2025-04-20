package com.example.rvnow.model

data class RVDestination(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val facilities: List<String> = listOf(),
    val bestTimeToVisit: String = "",
    val bestTime: String = "",
    val priceRange: String = "",
    val rating: Double = 0.0,
    val isFeatured: Boolean = false,
    val parkingSpots: List<Map<String, Any>>? = null
)
