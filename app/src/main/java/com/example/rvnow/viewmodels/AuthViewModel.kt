
package com.example.rvnow.viewmodels
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import com.example.rvnow.api.RVInformation
import com.example.rvnow.model.Favourite
import com.example.rvnow.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoggedIn = MutableLiveData(auth.currentUser != null)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // Store complete User data (from Firebase Authentication + Firestore)
    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> get() = _userInfo

    private val _fullName = MutableLiveData<String?>()
    val fullName: LiveData<String?> get() = _fullName

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
            firebaseAuth.currentUser?.let { user ->
                // Now fetch complete user data from Firestore
                fetchUserData(user.uid)
            } ?: run {
                // If no user is signed in, set userInfo to null
                _userInfo.value = null
            }
        }
    }
    private val db = FirebaseFirestore.getInstance()
    private val _onUserFetched = MutableLiveData<String>()  // will store UID
    val onUserFetched: LiveData<String> = _onUserFetched


//    private val rvApiService = RVInformation()
    fun fetchUserData(uid: String) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                // Update _userInfo LiveData with the complete user data
                _userInfo.value = user
                _fullName.value = user?.fullName // Update full name separately if needed
                user?.let {
                    _onUserFetched.value = uid // 🔁 Trigger event
                }
            //                user?.let {
//                    RVInformation() .loadFavourites(uid) // 👈 Your function to fetch favourite RVs/images
//                }
            }
            .addOnFailureListener {
                _userInfo.value = null // If fetching fails, set it to null
                _fullName.value = null // Handle failure in fetching full name
            }
    }

    fun updateUserInfo(userId: String, newFullName: String, newProfileUrl: String?) {
        val updates = mapOf(
            "fullName" to newFullName,
            "profilePictureUrl" to newProfileUrl,
            "updatedAt" to Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "User info updated")
                fetchUserData(userId) // Refresh local LiveData after update
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Failed to update user info", it)
            }
    }





    // Sign-out function
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _isLoggedIn.value = false
        _userInfo.value = null
    }
}
//
//class AuthViewModel : ViewModel() {
//    private val auth = FirebaseAuth.getInstance()
//    private val firestore = FirebaseFirestore.getInstance()
//
//    private val _isLoggedIn = MutableLiveData(auth.currentUser != null)
//    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn
//
//    private val _userInfo = MutableLiveData<FirebaseUser?>()
//    val userInfo: LiveData<FirebaseUser?> get() = _userInfo
//
//    private val _fullName = MutableLiveData<String?>()
//    val fullName: LiveData<String?> get() = _fullName
//
//    init {
//        auth.addAuthStateListener { firebaseAuth ->
//            _isLoggedIn.value = firebaseAuth.currentUser != null
//            _userInfo.value = firebaseAuth.currentUser
//            firebaseAuth.currentUser?.let { user ->
//                // Fetch full name from Firestore or Realtime Database
//                fetchFullName(user.uid)
//            }
//        }
//    }
//
//    private fun fetchFullName(uid: String) {
//
//        firestore.collection("users")
//            .document(uid)
//            .get()
//            .addOnSuccessListener { document ->
//                val fullName = document.getString("fullName")
//                _fullName.value = fullName
//            }
//            .addOnFailureListener {
//                _fullName.value = null
//            }
//    }
//
//    // Sign-out function
//    fun logout() {
//        FirebaseAuth.getInstance().signOut()
//        _isLoggedIn.value = false // Update the login status
//    }
//}
//
//
