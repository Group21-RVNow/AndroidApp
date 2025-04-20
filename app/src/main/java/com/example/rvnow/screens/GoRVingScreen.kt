package com.example.rvnow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.input.ImeAction
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import kotlinx.coroutines.launch

// Define spacing constants consistent with HomeScreen
private val SECTION_SPACING = 32.dp
private val SECTION_SPACING_SMALL = 20.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CORNER_RADIUS_SMALL = 8.dp
private val CARD_CONTENT_PADDING = 12.dp

val primaryColor = Color(0xFFE27D5F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoRVingScreen(
    navController: NavController,
    viewModel: GoRVingViewModel = viewModel()
) {
    // Define colors consistent with HomeScreen
    val primaryColor = Color(0xFFE27D5F)  // Terracotta orange
    val secondaryColor = Color(0xFF5D8AA8)  // Lake blue
    val tertiaryColor = Color(0xFF6B8E23)  // Moss green
    val neutralColor = Color(0xFFA78A7F)   // Light camel - used for featured destination background

    var searchQuery by remember { mutableStateOf("") }
    val destinations by viewModel.destinations.collectAsState()
    val featuredDestinations by viewModel.featuredDestinations.collectAsState()
    val travelGuides by viewModel.travelGuides.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 添加协程作用域用于处理搜索
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadDestinations()
        viewModel.loadFeaturedDestinations()
        viewModel.loadTravelGuides()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryColor.copy(alpha = 0.2f))
                        .padding(vertical = 12.dp, horizontal = HORIZONTAL_PADDING)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        Text(
                            "Go RVing",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(CARD_CORNER_RADIUS))
                        Text(
                            "Discover amazing RV destinations",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = SECTION_SPACING),
                verticalArrangement = Arrangement.spacedBy(SECTION_SPACING)
            ) {
                // Search bar
                item {
                    Spacer(modifier = Modifier.height(SECTION_SPACING))

                    var isSearching by remember { mutableStateOf(false) }

                    if (isSearching) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HORIZONTAL_PADDING),
                            color = primaryColor
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HORIZONTAL_PADDING),
                        placeholder = { Text("Search destinations and Guides", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    isSearching = true
                                    coroutineScope.launch {
                                        viewModel.clearSearch()
                                        // 直接调用搜索方法，不使用Flow收集
                                        viewModel.performSearch(searchQuery)
                                        isSearching = false
                                        // 检查搜索结果状态流
                                        if (viewModel.searchResults.value.isNotEmpty()) {
                                            navController.navigate("search_results")
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotEmpty()) {
                                    isSearching = true
                                    coroutineScope.launch {
                                        viewModel.clearSearch()
                                        // 直接调用搜索方法，不使用Flow收集
                                        viewModel.performSearch(searchQuery)
                                        isSearching = false
                                        // 检查搜索结果状态流
                                        if (viewModel.searchResults.value.isNotEmpty()) {
                                            navController.navigate("search_results")
                                        }
                                    }
                                }
                            }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            cursorColor = primaryColor,
                        )
                    )
                }

                // Featured destinations
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
//                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                        Text(
                            text = "Featured Destinations",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING)
                        )

                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING),
                            horizontalArrangement = Arrangement.spacedBy(SECTION_SPACING_SMALL))
                        {
                            items(featuredDestinations) { destination ->
                                FeaturedDestinationCard(
                                    destination = destination,
                                    onClick = {
                                        navController.navigate("destination_details/${destination.id}")
                                    },
                                    neutralColor = neutralColor
                                )
                            }
                        }
                    }
                }



                // Browse by country
                item {
                    Column {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        Text(
                            text = "Browse by Country",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING)
                        )

                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        // Get unique countries
                        val countries = destinations.map { it.country }.distinct()

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING),
                            horizontalArrangement = Arrangement.spacedBy(SECTION_SPACING))
                        {
                            items(countries) { country ->
                                CountryCard(
                                    country = country,
                                    onClick = {
                                        navController.navigate("country_destinations/$country")
                                    }
                                )
                            }
                        }
                    }
                }

                // Travel guides
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                        Text(
                            text = "Travel Guides",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = HORIZONTAL_PADDING)
                        )

                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING),
                            horizontalArrangement = Arrangement.spacedBy(SECTION_SPACING_SMALL)
                        ) {
                            items(travelGuides) { guide ->
                                TravelGuideCard(
                                    travelGuide = guide,
                                    onClick = {
                                        navController.navigate("travel_guide_details/${guide.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedDestinationCard(
    destination: RVDestination,
    onClick: () -> Unit,
    neutralColor: Color,
    modifier: Modifier = Modifier
) {
    // Image height increased by 50% (120dp * 1.5 = 180dp)
    val imageHeight = 180.dp
    // Total card height calculation (image + text section)
    val cardHeight = imageHeight + 60.dp // Reduced from 80dp to 60dp for less bottom padding

    Card(
        modifier = modifier
            .width(280.dp)
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image section with 50% increased height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            ) {
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Rating badge
                if (destination.rating > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = primaryColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${destination.rating}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Text section with reduced padding (matches travel guide style)
            Column(
                modifier = Modifier
                    .padding(horizontal = CARD_CONTENT_PADDING)
                    .padding(top = 8.dp, bottom = 8.dp) // Reduced bottom padding
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

                Text(
                    text = destination.location, // Removed country to avoid duplication
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CountryCard(
    country: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val flagImageUrl = when (country) {
        "Sweden" -> "https://flagcdn.com/w640/se.jpg"
        "Norway" -> "https://flagcdn.com/w640/no.jpg"
        "Finland" -> "https://flagcdn.com/w640/fi.jpg"
        "Denmark" -> "https://flagcdn.com/w640/dk.jpg"
        "Iceland" -> "https://flagcdn.com/w640/is.jpg"
        "USA" -> "https://flagcdn.com/w640/us.jpg"
        "Canada" -> "https://flagcdn.com/w640/ca.jpg"
        "Germany" -> "https://flagcdn.com/w640/de.jpg"
        "France" -> "https://flagcdn.com/w640/fr.jpg"
        "Spain" -> "https://flagcdn.com/w640/es.jpg"
        else -> "https://flagcdn.com/w640/generic.png"
    }

    Card(
        modifier = modifier
            .width(120.dp)
            .height(100.dp)  // 增加高度以容纳文字
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS_SMALL),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 国旗图片部分
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)  // 设置固定高度
            ) {
                AsyncImage(
                    model = flagImageUrl,
                    contentDescription = country,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // 国家名称文字部分
            Text(
                text = country,
                color = Color.Black,  // 改为黑色文字
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)  // 底部留白
            )
        }
    }
}

@Composable
fun TravelGuideCard(
    travelGuide: RVTravelGuide,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Match the featured destination card dimensions
    val imageHeight = 180.dp // 50% taller than original
    val cardHeight = imageHeight + 60.dp

    Card(
        modifier = modifier
            .width(280.dp)
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Taller image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            ) {
                AsyncImage(
                    model = travelGuide.imageUrl,
                    contentDescription = travelGuide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Text section with same padding as featured destinations
            Column(
                modifier = Modifier
                    .padding(horizontal = CARD_CONTENT_PADDING)
                    .padding(top = 8.dp, bottom = 8.dp)
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}