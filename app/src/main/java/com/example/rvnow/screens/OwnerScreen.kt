package com.example.rvnow.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.rvnow.model.RV
import com.example.rvnow.viewmodels.RVViewModel
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.ui.text.font.FontFamily
import com.google.firebase.Timestamp
private val SECTION_SPACING_SMALL = 20.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
@Composable
fun OwnerScreen(navController: NavController, rvViewModel: RVViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
//    var priceForSales by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isForSale by remember { mutableStateOf(false) }
    val secondaryColor = Color(0xFF6B8E23)  // Lake blue

    var isForRental by remember { mutableStateOf(false) }
//    var price by


//    var additionalImages by remember { mutableStateOf(mutableListOf<String>()) }
//    var additionalImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var additionalImages by remember { mutableStateOf<List<String>>(emptyList()) }

// List of additional images
    var newImageUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val image1 = rememberAsyncImagePainter("file:///android_asset/images/11.png")
//    val additionalImages = additionalImages.toList()
    Column(
        modifier = Modifier
            .fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(250.dp)
//                .background(color = Color.White)
//        ) {
//            Image(
//                painter = image1,
//                contentDescription = "RV Image",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//
//            Text(
//                text = "RV Owner",
//                color = Color.White,
//                fontSize = 34.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(secondaryColor.copy(alpha = 0.2f))
                .padding(vertical = 12.dp, horizontal = HORIZONTAL_PADDING)
//
        ) {
            Column {
                Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                Text(
                    "RV Owner",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(color = Color.White)
        ){
            Text(text = "Publish Your RV", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        // Make the "Publish Your RV" title fixed at the top


        // Add Scrollable area for the inputs
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()) // Add scroll functionality here
                .padding(top = 10.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("RV Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pricePerDay,
                onValueChange = { pricePerDay = it },
                label = { Text("Price Per Day") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Sales Price") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Kilometer Limitation per Day") },
                modifier = Modifier.fillMaxWidth()
            )


            Column {
                // Add new image URL input field
                OutlinedTextField(
                    value = newImageUrl,
                    onValueChange = { newImageUrl = it },
                    label = { Text("Enter Additional Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {

                        if (newImageUrl.isNotEmpty()) {
                            additionalImages = additionalImages + newImageUrl// Add URL to list
                            newImageUrl = "" // Clear the input field
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA3DC6F),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Add Image URL")
                }

                // Display the added image URLs
                additionalImages.forEach { imageUrl ->
                    Text(imageUrl, modifier = Modifier.fillMaxWidth().padding(4.dp))
                }
            }

            // Button to add the image URL to the list
//            Button(
//                onClick = {
//                    if (newImageUrl.isNotEmpty()) {
//                        additionalImages.add(newImageUrl) // Directly add the new image URL
//                        newImageUrl = "" // Clear input field after adding
//                    }
//                },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Add Image URL")
//            }
//            var additionalImages by remember { mutableStateOf(mutableListOf<String>()) }
            // Display the added image URLs
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
//            ) {
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("more images")
//                        // Button to delete an image URL
//                        IconButton(onClick = {
////                            additionalImages.removeAt(imageUrl) // Directly remove the URL
//                        }) {
//                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
//                        }
//
//                }
//            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isForSale,
                        onCheckedChange = { isForSale = it }
                    )
                    Text(text = "For Sale")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isForRental,
                        onCheckedChange = { isForRental = it }
                    )
                    Text(text = "For Rental")
                }
            }

            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() &&pricePerDay.isNotEmpty() && price.isNotEmpty() && place.isNotEmpty()) {
                        val pricePerDayValue = pricePerDay.toDoubleOrNull()
                        val priceValue = price.toDoubleOrNull()
                        if (priceValue == null || priceValue  == null) {
                            Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val newRV = pricePerDayValue?.let {
                            RV(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                description = description,
                                pricePerDay = it,
                                price = priceValue,
                                place = place,
                                imageUrl = imageUrl,
                                ownerId = userId,
                                isForSale = isForSale,
                                isForRental = isForRental,
                                additionalImages = additionalImages, // Corrected to use the current list
                                insurance = emptyMap(),
                                driverLicenceRequired = "",
                                kilometerLimitation = 0,
                                status = "Available",
                                createdAt = Timestamp.now(),
                                bookedDates = emptyList()
                            )
                        }

                        if (newRV != null) {
                            rvViewModel.addRV(newRV)
                        } // Calls ViewModel function
                        name =""
                        description=""
                        pricePerDay=""
                        price=""
                        place=""
                        imageUrl=""
                        isForSale=false
                        isForRental=false
                        newImageUrl=""
                        additionalImages = emptyList()
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA3DC6F),
                    contentColor = Color.Black
                ),

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit RV")
            }

        }
    }
}

//
//
//
//
//
//@Composable
//fun OwnerScreen(navController: NavController, rvViewModel: RVViewModel = viewModel()) {
//    var name by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//    var price by remember { mutableStateOf("") }
//    var place by remember { mutableStateOf("") }
//    var imageUrl by remember { mutableStateOf("") }
//    var isForSale by remember { mutableStateOf(false) }
//    var isPopular by remember { mutableStateOf(false) }
//
//    var additionalImages by remember { mutableStateOf(mutableListOf<String>()) } // List of additional images
//    var newImageUrl by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    val image1 = rememberAsyncImagePainter("file:///android_asset/images/11.png")
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(250.dp)
//                .background(color = Color.White)
//        ) {
//            Image(
//                painter = image1,
//                contentDescription = "RV Image",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//
//            Text(
//                text = "Welcome to RVNow",
//                color = Color.White,
//                fontSize = 34.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        // Make the "Publish Your RV" title fixed at the top
//        Text(text = "Publish Your RV", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//
//        // Add Scrollable area for the inputs
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()) // Add scroll functionality here
//                .padding(top = 10.dp)
//        ) {
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("RV Name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                label = { Text("Description") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = price,
//                onValueChange = { price = it },
//                label = { Text("Price Per Day") },
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = place,
//                onValueChange = { place = it },
//                label = { Text("Location") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = imageUrl,
//                onValueChange = { imageUrl = it },
//                label = { Text("Image URL") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = place,
//                onValueChange = { place = it },
//                label = { Text("Kilometer Limitation per Day") },
//                modifier = Modifier.fillMaxWidth()
//            )
//// for the additional images
//            OutlinedTextField(
//                value = newImageUrl,
//                onValueChange = { newImageUrl = it },
//                label = { Text("Enter Additional Image URL") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // Button to add the image URL to the list
//            Button(
//                onClick = {
//                    if (newImageUrl.isNotEmpty()) {
//                        additionalImages = additionalImages.toMutableList().apply { add(newImageUrl) }
//                        newImageUrl = "" // Clear input field after adding
//                    }
//                },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Add Image URL")
//            }
//
//            // Display the added image URLs
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
//            ) {
//                items(additionalImages) { imageUrl ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(imageUrl, modifier = Modifier.weight(1f)) // Show URL
//                        // Button to delete an image URL
//                        IconButton(onClick = {
//                            additionalImages = additionalImages.filter { it != imageUrl }.toMutableList()
//                        }) {
//                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
//                        }
//                    }
//                }
//            }
//
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(
//                        checked = isForSale,
//                        onCheckedChange = { isForSale = it }
//                    )
//                    Text(text = "For Sale")
//                }
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(
//                        checked = isPopular,
//                        onCheckedChange = { isPopular = it }
//                    )
//                    Text(text = "For Rental")
//                }
//            }
//
//            Button(
//                onClick = {
//                    if (name.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && place.isNotEmpty()) {
//                        val priceValue = price.toDoubleOrNull()
//                        if (priceValue == null) {
//                            Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show()
//                            return@Button
//                        }
//
//                        val newRV = RV(
//                            id = UUID.randomUUID().toString(),
//                            name = name,
//                            description = description,
//                            pricePerDay = priceValue,
//                            place = place,
//                            imageUrl = imageUrl,
//                            ownerId = userId,
//                            isForSale = isForSale,
//                            isPopular = isPopular,
//                            additionalImages = emptyList(),
//                            insurance = emptyMap(),
//                            driverLicenceRequired = "",
//                            kilometerLimitation = 0,
//                            status = "Available",
//                            createdAt = Timestamp.now(),
//                            bookedDates = emptyList()
//                        )
//
//                        rvViewModel.addRV(newRV) // Calls ViewModel function
//
//
//                    } else {
//                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Submit RV")
//            }
//
//        }
//    }
//}
//
