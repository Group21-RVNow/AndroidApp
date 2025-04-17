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
                        placeholder = { Text("Search destinations", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    isSearching = true
                                    coroutineScope.launch {
                                        viewModel.clearSearch()
                                        viewModel.search(searchQuery).collect { results ->
                                            isSearching = false
                                            if (results.isNotEmpty()) {
                                                navController.navigate("search_results")
                                            }
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
                                        viewModel.search(searchQuery).collect { results ->
                                            isSearching = false
                                            if (results.isNotEmpty()) {
                                                navController.navigate("search_results")
                                            }
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
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
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
    Card(
        modifier = modifier
            .width(280.dp)
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
//                    .background(
//                        neutralColor.copy(alpha = 0.5f)
            )


            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(CARD_CONTENT_PADDING)
            ) {
                Text(
                    text = destination.name,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )

                Text(
                    text = "${destination.location}, ${destination.country}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default

                )
            }

            // 添加评分标签
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
    }
}


@Composable
fun CountryCard(
    country: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 硬编码国旗图片URL（使用flagcdn高质量图片）
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
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = flagImageUrl,
                contentDescription = country,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 半透明覆盖层
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Text(
                text = country,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center
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
    Card(
        modifier = modifier
            .width(280.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = travelGuide.imageUrl,
                    contentDescription = travelGuide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}
