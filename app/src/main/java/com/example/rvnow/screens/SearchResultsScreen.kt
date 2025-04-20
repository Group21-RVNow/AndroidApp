package com.example.rvnow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rvnow.model.RVDestination
import com.example.rvnow.model.RVTravelGuide
import com.example.rvnow.viewmodels.GoRVingViewModel

// Define spacing constants consistent with HomeScreen
private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_SMALL = 16.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CONTENT_PADDING = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavController,
    viewModel: GoRVingViewModel
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 添加调试信息
    val searchResultsSize = searchResults.size

    // Define colors consistent with HomeScreen
    val primaryColor = Color(0xFFE27D5F)  // Terracotta orange
    val secondaryColor = Color(0xFF5D8AA8)  // Lake blue
    val tertiaryColor = Color(0xFF6B8E23)  // Moss green
    val neutralColor = Color(0xFFA78A7F)   // Light camel

    LaunchedEffect(Unit) {
        // 确保加载必要数据
        if (viewModel.destinations.value.isEmpty()) {
            viewModel.loadDestinations()
        }
        if (viewModel.travelGuides.value.isEmpty()) {
            viewModel.loadTravelGuides()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Search Results (${searchResults.size})",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSearch()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Error: $error",
                    color = Color.Red,
                    fontFamily = FontFamily.Default,
                    fontSize = 16.sp
                )
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No results found",
                        fontFamily = FontFamily.Default,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Try different keywords or browse our featured destinations",
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING)
                    )

                    Spacer(modifier = Modifier.height(SECTION_SPACING))

                    Button(
                        onClick = { navController.navigate("go_rving") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Browse Destinations",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search results summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(secondaryColor.copy(alpha = 0.1f))
                        .padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL / 2)
                ) {
                    Text(
                        text = "Found ${searchResults.size} result(s)",
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = HORIZONTAL_PADDING),
                    verticalArrangement = Arrangement.spacedBy(SECTION_SPACING_SMALL),
                    contentPadding = PaddingValues(vertical = SECTION_SPACING_SMALL)
                ) {
                    items(searchResults) { result ->
                        when (result) {
                            is RVDestination -> {
                                SearchDestinationItem(
                                    destination = result,
                                    onClick = {
                                        navController.navigate("destination_details/${result.id}")
                                    },
                                    primaryColor = primaryColor
                                )
                            }
                            is RVTravelGuide -> {
                                SearchTravelGuideItem(
                                    travelGuide = result,
                                    onClick = {
                                        navController.navigate("travel_guide_details/${result.id}")
                                    },
                                    tertiaryColor = tertiaryColor
                                )
                            }
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchDestinationItem(
    destination: RVDestination,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                modifier = Modifier
                    .width(120.dp)
                    .height(100.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = CARD_CORNER_RADIUS,
                            bottomStart = CARD_CORNER_RADIUS
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(CARD_CONTENT_PADDING)
            ) {
                Text(
                    text = destination.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${destination.location}, ${destination.country}",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rating display
                if (destination.rating != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),  // Star yellow
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${destination.rating}",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTravelGuideItem(
    travelGuide: RVTravelGuide,
    onClick: () -> Unit,
    tertiaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = travelGuide.imageUrl,
                contentDescription = travelGuide.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(100.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = CARD_CORNER_RADIUS,
                            bottomStart = CARD_CORNER_RADIUS
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(CARD_CONTENT_PADDING)
            ) {
                Text(
                    text = travelGuide.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = travelGuide.summary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = tertiaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Published: ${travelGuide.date ?: "2025-04-16"}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
