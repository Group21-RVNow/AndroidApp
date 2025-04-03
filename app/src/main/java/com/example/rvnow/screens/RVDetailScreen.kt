// Add a new DetailScreen composable for displaying detailed information of the RV
package com.example.rvnow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.rvnow.model.RV
import com.example.rvnow.viewmodels.RVViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*

import androidx.compose.material3.Icon

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf


import androidx.compose.runtime.setValue

import java.util.*

//@Composable
//fun RVDetailScreen(
//    rvId: String,
//    rvViewModel: RVViewModel,
//    navController: NavController
//) {
//    // Collect the list of RVs from the ViewModel
//    val rvList by rvViewModel.rvs.collectAsState(initial = emptyList())
//    val rv = rvList.firstOrNull { it.id == rvId }
//
//    // State variables for favorite status, comment input, and comment list
//    var isFavorite by remember { mutableStateOf(rv?.isFavorite ?: false) }
//    var comment by remember { mutableStateOf("") }
//    var comments by remember { mutableStateOf(rv?.comments ?: listOf()) }
//
//    // Image painter for the main RV image
//    val imagePainter = rememberAsyncImagePainter(model = rv?.imageUrl)
//
//    // Function to handle adding a comment
//    fun addComment() {
//        if (comment.isNotBlank()) {
//            comments = comments + comment
//            comment = ""
//            // Optionally, update the RV's comments in the ViewModel or repository
//        }
//    }
//
//    // Function to toggle the favorite status
//    fun toggleFavorite() {
//        isFavorite = !isFavorite
//        // Optionally, update the RV's favorite status in the ViewModel or repository
//    }
//
//    // Main content
//    LazyColumn(modifier = Modifier.padding(10.dp)) {
//        item {
//            // Top Image Banner
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp)
//                    .padding(bottom = 16.dp)
//                    .background(color = Color.Gray)
//            ) {
//                Image(
//                    painter = imagePainter,
//                    contentDescription = rv?.name,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//                Text(
//                    text = "Welcome to RVNow",
//                    color = Color.White,
//                    fontSize = 34.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }
//        rv?.let {
//            item {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    // Main RV Image
//                    Image(
//                        painter = imagePainter,
//                        contentDescription = it.name,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(300.dp),
//                        contentScale = ContentScale.Crop
//                    )
//
//                    // Favorite Button
//                    IconButton(
//                        onClick = { toggleFavorite() },
//                        modifier = Modifier.align(Alignment.End)
//                    ) {
//                        Icon(
//                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
//                            contentDescription = "Favorite",
//                            tint = if (isFavorite) Color.Red else Color.Gray
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // RV Details
//                    Text(text = "Name: ${it.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(text = "Description: ${it.description}", fontSize = 16.sp)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(text = "Place: ${it.place}", fontSize = 16.sp)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(text = "Status: ${it.status}", fontSize = 16.sp)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(text = "Price Per Day: \$${it.pricePerDay}", fontSize = 16.sp)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(text = "Driver License Required: ${it.driverLicenceRequired}", fontSize = 16.sp)
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Additional Images
//                    Text(text = "Additional Images:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                    LazyRow(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(it.additionalImages) { imageUrl ->
//                            AsyncImage(
//                                model = imageUrl,
//                                contentDescription = "Additional Image",
//                                modifier = Modifier
//                                    .size(150.dp)
//                                    .clip(RoundedCornerShape(8.dp)),
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Comment Section
//                    Text(text = "Leave a Comment:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                    OutlinedTextField(
//                        value = comment,
//                        onValueChange = { comment = it },
//                        label = { Text("Write a comment...") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Button(
//                        onClick = { addComment() },
//                        modifier = Modifier.align(Alignment.End)
//                    ) {
//                        Text("Submit Comment")
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Display Comments
//                    if (comments.isNotEmpty()) {
//                        Text(text = "Comments:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                        Column {
//                            comments.forEach { commentText ->
//                                Card(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 4.dp)
//                                ) {
//                                    Text(
//                                        text = commentText,
//                                        modifier = Modifier.padding(8.dp),
//                                        fontSize = 14.sp
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } ?: item {
//            Text(text = "RV not found", modifier = Modifier.padding(16.dp))
//        }
//    }
//}


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.layout.size


@Composable
fun RVDetailScreen(rvId: String, rvViewModel: RVViewModel, navController: NavController) {
    val rvList by rvViewModel.rvs.collectAsState()
    val rv = rvList.firstOrNull { it.id == rvId }

    var isFavorite by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf<String>()) }

    val image1 = rememberAsyncImagePainter("file:///android_asset/images/11.png")
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(bottom = 16.dp)
                .background(color = Color.White)
        ) {
            Image(
                painter = image1,
                contentDescription = "RV Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Welcome to RVNow",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.padding(10.dp)) {


            rv?.let {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description: ${it.name}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            painter = rememberAsyncImagePainter(model = it.imageUrl),
                            contentDescription = it.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* Implement add to cart logic */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Description: ${it.description}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Departure: ${it.place}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Availability: ${it.status}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Price Per Day: \$${it.pricePerDay}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Driver Licence Required: ${it.driverLicenceRequired}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Additional Images:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(it.additionalImages) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Image",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Leave a Comment:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Write a comment...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (comment.isNotBlank()) {
                                    comments = comments + comment
                                    comment = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Submit Comment")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (comments.isNotEmpty()) {
                            Text(text = "Comments:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Column {
                                comments.forEach { commentText ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = commentText,
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 14.sp
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }



}


