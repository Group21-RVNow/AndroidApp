package com.example.rvnow

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
//import androidx.compose.material.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.rvnow.viewmodels.RVViewModel

import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.rvnow.model.CartItem
import com.example.rvnow.viewmodels.AuthViewModel


@Composable
fun CartScreen(
    navController: NavController,
    rvViewModel: RVViewModel,
    authViewModel: AuthViewModel
) {
    val cartItems by rvViewModel.cartItems.collectAsState()
    val currentUser by authViewModel.userInfo.observeAsState()
    val context = LocalContext.current
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let {
            rvViewModel.fetchCartItems(it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Your Cart", style = MaterialTheme.typography.h4)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ){
            Text("Shopping Cart",style = TextStyle(fontSize = 24.sp) )

        }

//        Spacer(modifier = Modifier.height(46.dp))

        if (!isLoggedIn) {
            Text("You are not logged in", modifier = Modifier.padding(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        navController.navigate("Signin|up") {
                            popUpTo("Signin|up") { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Sign In|Sign Up")
                }
            }
        } else if (cartItems.isEmpty()) {
            Text("Your cart is empty", modifier = Modifier.padding(16.dp))
        } else {
            // Wrap the LazyColumn in a Box
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // LazyColumn to display cart items
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cartItems) { item ->
                        CartItemCard(item, rvViewModel)
                    }
                }

                // Box to hold the button at the bottom-right corner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Add padding to move it up slightly from the bottom
                ) {
                    Button(
                        onClick = { /* ... */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Position it at the bottom end of the screen
                            .padding(16.dp) // Optional padding to make the button text more readable
                    ) {
                        Text(
                            text = "Proceed to Checkout",
                            style = TextStyle(
                                fontSize = 18.sp, // Adjust font size if necessary
                                color = Color.Green
                            ),
                            maxLines = 1, // Ensure text doesn't overflow to multiple lines
                            overflow = TextOverflow.Ellipsis // Ensure text is ellipsized if it's too long
                        )
                    }
                }
            }


            // Checkout Button
//            Button(
//                onClick = {
//////                    currentUser?.uid?.let { userId ->
//////                        rvViewModel.checkout(userId) { success ->
//////                            if (success) {
//////                                Toast.makeText(context, "Checkout successful!", Toast.LENGTH_SHORT).show()
//////                                navController.navigate("home")
//////                            } else {
//////                                Toast.makeText(context, "Checkout failed", Toast.LENGTH_SHORT).show()
////                            }
//////                        }
////                    } ?: run {
////                        Toast.makeText(context, "Please login to checkout", Toast.LENGTH_SHORT).show()
////                    }
//                },
//                modifier = Modifier.background(Color.Red)
//            ) {
//                Text(
//                    "Proceed to Checkout",
//                    style = TextStyle(
//                        fontSize = 24.sp,
//                        color = Color.Green
//                    )
//                )
//
//            }
        }
    }

}






@Composable
fun CartItemCard(
    item: CartItem,
    rvViewModel: RVViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
//        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // RV Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Item Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
//                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "$${item.pricePerDay}/day",
//                    style = MaterialTheme.typography.body1
                )

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quantity: ${item.quantity}")

                    Row {
                        IconButton(
                            onClick = {
//                                rvViewModel.updateCartItemQuantity(item.rvId, item.quantity - 1)
                            },
                            enabled = item.quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, "Decrease quantity")
                        }

                        IconButton(
                            onClick = {
//                                rvViewModel.updateCartItemQuantity(item.rvId, item.quantity + 1)
                            }
                        ) {
                            Icon(Icons.Default.Add, "Increase quantity")
                        }
                    }
                }
            }

            // Remove Button
            IconButton(
                onClick = {
//                    rvViewModel.removeFromCart(item.rvId)
                }
            ) {
                Icon(Icons.Default.Delete, "Remove from cart", tint = Color.Red)
            }
        }
    }
}
