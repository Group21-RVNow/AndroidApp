package com.example.rvnow




import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rvnow.model.CartItem
import com.example.rvnow.viewmodels.AuthViewModel
import com.example.rvnow.viewmodels.RVViewModel


@Composable
fun CartScreen(
    navController: NavController,
    rvViewModel: RVViewModel,
    authViewModel: AuthViewModel
) {
    val cartItems by rvViewModel.cartItems.collectAsState()
    val currentUser by authViewModel.userInfo.observeAsState()
//    val userInfo by authViewModel.userInfo.observeAsState()
    val userId = currentUser?.id
    var showCheckout by remember { mutableStateOf(false) }
//    val context = LocalContext.current
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
    LaunchedEffect(currentUser) {
        currentUser?.id?.let {
            rvViewModel.fetchCartItems(it)
        }
    }

    Column(modifier = Modifier.padding(10.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Space between the text and button
            verticalAlignment = Alignment.CenterVertically // Align text and button vertically centered
        ) {
            // Shopping Cart Title Text
            Text(
                text = "Shopping Cart",
                style = TextStyle(fontSize = 20.sp)
            )

            // Proceed to Checkout Button
            Button(
                onClick = { showCheckout = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA3DC6F),
                    contentColor = Color.Black
                ),
                modifier = Modifier.padding(16.dp) // Optional padding for the button
            ) {
                Text(
                    text = "Proceed to Checkout",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }


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

                val totalPrice = cartItems.sumOf { item ->
                    val rentalPrice = item.pricePerDay?.takeIf { it > 0 } ?: 0.0
                    val salePrice = item.price?.takeIf { it > 0 } ?: 0.0
                    val unitPrice = if (rentalPrice > 0) rentalPrice else salePrice
                    unitPrice * item.quantity
                }

                // LazyColumn to display cart items
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cartItems) { item ->
                        if (userId != null) {
                            CartItemCard(
                                item = item,
                                userId = userId,
                                rvViewModel= rvViewModel,)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Total Price: $${"%.2f".format(totalPrice)}",
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }



                }


            }

        }
    }

}






@Composable
fun CartItemCard(
    item: CartItem,
    userId: String,
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

                if (item.price == 0.0) {
                    Text(
                        text = "$${item.pricePerDay}/day"
                    )
                } else {
                    Text(
                        text = "$${item.price}/item"
                    )
                }


//                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quantity: ${item.quantity}")
//
//                    Row {
//                        IconButton(
//                            onClick = {
////                                rvViewModel.updateCartItemQuantity(item.rvId, item.quantity - 1)
//                            },
//                            enabled = item.quantity > 1
//                        ) {
//                            Icon(Icons.Default.Remove, "Decrease quantity")
//                        }
//
//                        IconButton(
//                            onClick = {
////                                rvViewModel.updateCartItemQuantity(item.rvId, item.quantity + 1)
//                            }
//                        ) {
//                            Icon(Icons.Default.Add, "Increase quantity")
//                        }
//                    }
                }
            }

            // Remove Button
            IconButton(
                onClick = {
                    rvViewModel.removeFromCart(
                        userId = userId,
                        rvId = item.rvId,
                    )

                }
            ) {
                Icon(Icons.Default.Delete, "Remove from cart", tint = Color.Red)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutForm(
    totalPrice: Double,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    var shippingAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Credit Card") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
//        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Checkout Details",
//                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shipping Address Input
            OutlinedTextField(
                value = shippingAddress,
                onValueChange = { shippingAddress = it },
                label = { Text("Shipping Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Selection
            var expanded by remember { mutableStateOf(false) }
            val paymentMethods = listOf("Credit Card", "PayPal", "Cash on Delivery")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = paymentMethod,
                    onValueChange = { },
                    label = { Text("Payment Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    paymentMethods.forEach {
//                        method ->
//                        DropdownMenuItem(
////                            onClick = {
////                                paymentMethod = method
////                                expanded = false
////                            }
//                        ) {
//                            Text(text = method)
//                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Total Price Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:",
//                    style = MaterialTheme.typography.h6
                )
                Text(
                    "$${"%.2f".format(totalPrice)}",
//                    style = MaterialTheme.typography.h6
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }

                Button(onClick = onSubmit) {
                    Text("Confirm Purchase")
                }
            }
        }
    }
}