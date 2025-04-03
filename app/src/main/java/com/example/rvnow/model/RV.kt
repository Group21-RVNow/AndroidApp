package com.example.rvnow.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class RVResponse(
    val results: List<RV>
)
//data class RV(
//    val id: Int,
//    val name: String,
//    val type: RVType,  // Rental or Sales type
//    val description: String,
//    val price: Double,
//    val imageUrl: String,
//    val place:String,
//    val driverLicence: String,
//    val kilometerLimitation: Int,
//)


//data class RV(
//    val id: Int,
//    val name: String,
//    val type: RVType,
//    val description: String,
//    val price: Double,
//    val imageUrl: String,
//    val place: String,
//    val additionalImages: List<String> = listOf(),
//    val insuranceInfo: String,
//    val driverLicence: String,
//    val kilometerLimitation: Int
//)

//
//data class RV(
////    val id: String = "",
//    val id: Int,  // Firebase document ID
//    val ownerId: String,  // Firebase Auth UID of the user who published it
//    val name: String,
//    val type: RVType,  // Enum (Sale or Rental)
//    val description: String,
//    val price: Double,
//    val imageUrl: String,
//    val place: String,
//    val additionalImages: List<String> = listOf(),
//    val insuranceInfo: String?,
//    val driverLicence: String?,
//    val kilometerLimitation: Int?,
//    val isForSale: Boolean,
//    val createdAt: Long = System.currentTimeMillis()
//)




data class RV(
    val id: String = "",
//    val isForSale: Boolean = false,
    @get:PropertyName("isForSale") val isForSale: Boolean = true,
//    @get:PropertyName("isForRental") val isForSRental: Boolean = true,
    val ownerId: String = "",
    val name: String = "",
    val type: RVType = RVType.Sales,
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    val place: String = "",
    val additionalImages: List<String> = listOf(),
    val insurance: Map<String, String> = emptyMap(),
    val driverLicenceRequired: String? = null,
    val kilometerLimitation: Int? = null,
    val isForRental: Boolean = false,
    @get:PropertyName("isPopular") val isPopular: Boolean = true,
    val status:String="",
    val createdAt: Timestamp = Timestamp.now(),
    val bookedDates: List<Map<String, Timestamp>> = listOf(),
//    val comments:List<String> = listOf(comment),
)




