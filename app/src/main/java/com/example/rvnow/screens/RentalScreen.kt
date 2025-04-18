package com.example.rvnow

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.rvnow.model.RV
import com.example.rvnow.viewmodels.AuthViewModel
import java.util.Calendar
import com.example.rvnow.viewmodels.RVViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun RentalScreen(
    rvViewModel: RVViewModel = viewModel(),
    navController: NavController
) {
    val rvViewModel: RVViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var searchWords by remember { mutableStateOf("") }
    val context = LocalContext.current
    val rvList by rvViewModel.rvs.collectAsState()
    val calendar = Calendar.getInstance()
    var isSearchPerformed by remember { mutableStateOf(false) }

    // Date Picker for Start Date
    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            startDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Date Picker for End Date
    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            endDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val image1 = rememberAsyncImagePainter("file:///android_asset/images/11.png")

    fun RV.matchesSearch(query: String): Boolean {
        val q = query.trim().lowercase()
        if (q.isBlank()) return true

        val haystack = listOfNotNull(
            name?.lowercase(),
            description?.lowercase(),
            place?.lowercase(),
            driverLicenceRequired?.lowercase(),
            status?.lowercase(),
            type.name.lowercase()
        ).joinToString(" ")

        return haystack.contains(q)
    }

    Column() {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(bottom = 16.dp)
                .background(color = Color.White)
        ) {

            // Loading image from drawable or assets
            Image(
                painter = image1,
                contentDescription = "RV Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
//                modifier = Modifier.width(30000.dp)

            )

            Text(
                text = "Welcome to RVNow",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // Start Date Field
        Row(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { startDatePickerDialog.show() }
//                    .pointerInput(Unit) {
//                        detectTapGestures(onTap = {
//                            startDatePickerDialog.show()
//                        })
//                    }// Entire box is clickable
            ) {
                OutlinedTextField(
                    value = startDate,
//                    onValueChange = { newValue ->
//                        startDate = newValue},
                    onValueChange = {
                         searchWords = it
                         isSearchPerformed = false      // ← stop filtering while typing
                         },
                    readOnly = true,  // Prevent manual input
                    label = { Text("Start Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(false)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = {newValue ->
                    endDate = newValue },
                readOnly = true,  // Prevent typing
                label = { Text("End Date") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { endDatePickerDialog.show() } // Open Date Picker
            )

        }




//        Spacer(modifier = Modifier.height(10.dp))

        Row (modifier = Modifier.padding(10.dp)){
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                OutlinedTextField(

                    value = searchWords,
                    onValueChange = { searchWords = it  },
                    label = { Text("Driving Type") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 20.sp)
                )
            }

        }


        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth().padding(10.dp)
//                .width(100.dp) .align(Alignment.Center)
//                .clickable { startDatePickerDialog.show() } // Apply clickable to the Box
        ) {
            Button(
                onClick = {
                    isSearchPerformed = true
//                    drivingType = ""
//                    startDate = ""
//                    endDate = ""
//                    place = ""
                          },
                modifier = Modifier.fillMaxWidth(),

            ) {
                Text("Search")
            }

        }
        Spacer(modifier = Modifier.height(10.dp))



        val filteredRVs = rvList.filter { it.isForRental == true}
        val filteredRVs1 = if (isSearchPerformed) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val userSearchStart = try {
                dateFormat.parse(startDate)
            } catch (e: Exception) { null }

            val userSearchEnd = try {
                dateFormat.parse(endDate)
            } catch (e: Exception) { null }

            filteredRVs.filter { rv ->
                // FIX 3: Simplified date check
                val dateValid = if (userSearchStart != null && userSearchEnd != null) {
                    rv.bookedDates.none { bookedDate ->
                        val bookedStart = (bookedDate["startDate"] as? Timestamp)?.toDate()
                        val bookedEnd = (bookedDate["endDate"] as? Timestamp)?.toDate()

                        bookedStart != null && bookedEnd != null &&
                                !(userSearchEnd.before(bookedStart) || userSearchStart.after(bookedEnd))
                    }
                } else true

                val matchesSearch = rv.matchesSearch(searchWords)

                dateValid && matchesSearch
            }
        } else {
            filteredRVs
        }



        LazyColumn {
            items(filteredRVs1) { rv ->
                LaunchedEffect(rv.id) {
                    rvViewModel.loadAverageRating(rv.id)
                    Log.d("AverageRating", "Loading Average Rating for RV on RentalPage: ${rv.id} is $")
                }

                RVItem(
                    rv = rv,
                    navController = navController,
                    rvViewModel= rvViewModel,
                    authViewModel = authViewModel,
                    rvId = rv.id
                )
                Log.d("RV", "isForRental: ${rv.isForRental}")

            }
        }
    }
}



@Composable
fun StarRatingBar3(
    rating: Float,
    averageRating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 1
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Display stars
        Row {
            for (i in 1..starCount) {
                val starValue = i.toFloat()
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = if (rating >= starValue) Color.Yellow
                    else if (averageRating >= starValue) Color.Yellow.copy(alpha = 0.1f)
                    else Color.Gray,
                    modifier = Modifier
                        .size(25.dp)

                )
            }
        }

        // Display average rating text
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "%.1f/5".format(averageRating),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RVItem(
    rv: RV,
    navController: NavController,
    rvViewModel: RVViewModel,
    authViewModel: AuthViewModel,
    rvId: String,

    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clickable { navController.navigate("detail/${rv.id}?sourcePage=rental") },
        shape = RoundedCornerShape(15.dp)
    ) {


        val rvList by rvViewModel.rvs.collectAsState()
        val name = rv?.name ?: ""

        var currentImageIndex by remember { mutableStateOf(0) }
        val allImages = listOfNotNull(rv.imageUrl) + (rv.additionalImages ?: emptyList())
        val visibleImages = allImages.take(6)
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//        val cardWidth = (screenWidth - horizontalPadding * 2 - CARD_SPACING) * 0.8f
        val ratings by rvViewModel.ratings.collectAsState(emptyList())
        val context = LocalContext.current

        val isForRental = rv?.isForRental ?: false
        val imageUrl = rv?.imageUrl ?: ""
//        val salesPrice = rv?.salesPrice ?:""
//
        val isForSale = rv.isForSale ?: false

        var rating by remember { mutableStateOf(8.5f) }
        var isFavorite by remember { mutableStateOf(false) }
        val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
        var showWarningDialog by remember { mutableStateOf(false) }
        val currentUser by authViewModel.userInfo.observeAsState()

        val averageRating = rvViewModel.averageRatings.collectAsState().value[rvId] ?: 0f





        // Fixed height for the card
        val cardHeight = 280.dp
        // Fixed height for the image section
        val imageHeight = 192.dp
        // Fixed height for the text section (card height - image height - padding)
//        val textSectionHeight = cardHeight - imageHeight - CARD_CONTENT_PADDING * 2
        var isProcessingFavorite by remember { mutableStateOf(false) }


        Column(
            modifier = Modifier.fillMaxWidth(), // Ensure column fills width
            horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
        ) {
            rv.imageUrl?.let { imagePath ->
                Image(
                    painter = rememberImagePainter(data = rv.imageUrl),
                    contentDescription = rv.name ?: "Unknown Title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rv.name ?: "Unknown Title",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = rv.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                fontSize = 14.sp,
                maxLines = 3,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
//                    horizontalArrangement = Arrangement.spacedBy(64.dp)
            ) {

                Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.Start
                ) {
                    // Display stars and average rating
                    StarRatingBar3(
                        rating = rating,
                        averageRating = averageRating,
                        onRatingChanged = { newRating ->
                            rating = newRating
                        }
                    )
                }


                Spacer(modifier = Modifier.width(28.dp))

                IconButton(
                    onClick = {
                        if (isProcessingFavorite) return@IconButton
                        if (!isLoggedIn) {
                            // If the user is not logged in, show a warning
                            showWarningDialog = true
                            return@IconButton
                        }
                        // Optimistic UI update
                        isFavorite = !isFavorite

                        currentUser?.id?.let { userId ->
                            isProcessingFavorite = true
                            rvViewModel.toggleFavorite(
                                userId = userId,
                                rvId = rvId,
                                name = name,
                                isForRental = isForRental,
                                imageUrl = imageUrl,
                                isForSale = isForSale
                            ) { success ->
                                isProcessingFavorite = false
                                if (success) {
                                    // Success - state is already updated
                                    Toast.makeText(
                                        context,
                                        if (isFavorite) "Added to favorites" else "Removed from favorite",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Show error and revert UI state
                                    isFavorite = !isFavorite
                                    Toast.makeText(
                                        context,
                                        "Failed to update favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } ?: run {
                            showWarningDialog = true
                        }
                    },
                    enabled = !isProcessingFavorite
                ) {
                    if (isProcessingFavorite) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

            }
        }
    }
}


