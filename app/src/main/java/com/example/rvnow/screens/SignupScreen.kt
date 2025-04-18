
package com.example.rvnow

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextButton
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.rvnow.model.User
import com.example.rvnow.model.UserRole
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import java.security.MessageDigest
import java.nio.charset.StandardCharsets

@Composable
fun SignupScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current // Access the current context
    val firestore = FirebaseFirestore.getInstance() // Firestore instance

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(top = 50.dp)
                .align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (password != confirmPassword) {
                            Toast.makeText(
                                context,
                                "Passwords do not match!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            signUpUser(email, password, fullName, auth, navController, firestore, context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sign Up")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate("Signin|up") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Already have an account? Log in")
                }
            }
        }
    }
}

private fun signUpUser(
    email: String,
    password: String,
    fullName: String,
    auth: FirebaseAuth,
    navController: NavController,
    firestore: FirebaseFirestore,
    context: Context
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val userRole = UserRole.Customer // Default role
                    val passwordHash = hashPassword(password)
                    val userProfilePicture = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQSdz0utA21KB1xCbObh4vipxTQ8Bw9XMRoyyU8HTWeukL-Cic4PUjf3gE&usqp=CAE&s"

                    // Create User object
                    val newUser = User(
                        id = userId,
                        email = email,
                        fullName = fullName,
                        passwordHash = passwordHash,
                        role = userRole,
                        profilePictureUrl = userProfilePicture,
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now(),
                        status = "Active"
                    )

                    // Store user in Firestore
                    firestore.collection("users")
                        .document(userId)
                        .set(newUser)
                        .addOnSuccessListener {
                            // Navigate to the home screen
                            navController.navigate("home")
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                context,
                                "Failed to store user in Firestore: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                // If sign-up fails, display a message to the user
                Toast.makeText(
                    context,
                    "Sign-up failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}

private fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashedBytes = md.digest(password.toByteArray(StandardCharsets.UTF_8))
    return hashedBytes.joinToString("") { String.format("%02x", it) }
}
