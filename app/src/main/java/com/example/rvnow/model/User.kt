package com.example.rvnow.model
import com.google.firebase.firestore.PropertyName
import com.google.firebase.Timestamp



data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val passwordHash: String = "",
    val role: UserRole = UserRole.Customer,
    @get:PropertyName("profilePictureUrl") // This is the Firestore field name
    val profilePictureUrl: String? = "https://play-lh.googleusercontent.com/3Fsib84emmZzNBKV0baMOEtK3lIwqTvxaw_0m6dMWJbtf0d6yirIx6vgvbi6KiqI7qk=w526-h296-rw",
//    val profilePictureUrl: String? = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTgOOZxJ0NLa746ipg_PJu0UKC3nDhhpoNyhtjcEp_Fesa1So1Lvbgdfdc&s",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val status: String = "Active"
)

enum class UserRole {
    Admin,
    Owner,
    Customer
}