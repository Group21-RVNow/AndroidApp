package com.example.rvnow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rvnow.model.RVDestination
import com.example.rvnow.viewmodels.GoRVingViewModel

// 定义与HomeScreen一致的间距常量
private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_SMALL = 16.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CONTENT_PADDING = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDestinationsScreen(
    navController: NavController,
    country: String
) {
    val viewModel = viewModel<GoRVingViewModel>()

    LaunchedEffect(country) {
        viewModel.getDestinationsByCountry(country)
    }

    val countryDestinations by viewModel.countryDestinations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 定义与HomeScreen一致的颜色
    val primaryColor = Color(0xFFE27D5F)  // 陶土橙（温暖活力）
    val secondaryColor = Color(0xFF5D8AA8)  // 湖蓝（自然平衡）
    val tertiaryColor = Color(0xFF6B8E23)  // 苔藓绿（生机感）
    val neutralColor = Color(0xFFA78A7F)   // 浅驼色（家的温暖）

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$country Destinations",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
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
        } else if (countryDestinations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No destinations found for $country",
                    color = Color.Gray,
                    fontFamily = FontFamily.Default,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 国家简介卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL),
                    shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                    colors = CardDefaults.cardColors(
                        containerColor = secondaryColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(CARD_CONTENT_PADDING)
                    ) {
                        Text(
                            text = "About $country",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = getCountryDescription(country),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Found ${countryDestinations.size} destinations",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            color = Color.Gray
                        )
                    }
                }

                // 目的地列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = HORIZONTAL_PADDING),
                    verticalArrangement = Arrangement.spacedBy(SECTION_SPACING_SMALL),
                    contentPadding = PaddingValues(vertical = SECTION_SPACING_SMALL)
                ) {
                    items(countryDestinations) { destination ->
                        CountryDestinationItem(
                            destination = destination,
                            onClick = {
                                navController.navigate("destination_details/${destination.id}")
                            },
                            primaryColor = primaryColor
                        )
                    }

                    // 底部间距
                    item {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                    }
                }
            }
        }
    }
}

@Composable
fun CountryDestinationItem(
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
        Column {
            Box {
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

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

            Column(
                modifier = Modifier.padding(CARD_CONTENT_PADDING)
            ) {
                Text(
                    text = destination.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = destination.location,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 设施标签
                if (!destination.facilities.isNullOrEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        destination.facilities.take(3).forEach { facility ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = primaryColor.copy(alpha = 0.1f),
                                tonalElevation = 0.dp
                            ) {
                                Text(
                                    text = facility,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryColor
                                )
                            }
                        }

                        // 如果有更多设施，显示+N
                        if (destination.facilities.size > 3) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.LightGray.copy(alpha = 0.3f),
                                tonalElevation = 0.dp
                            ) {
                                Text(
                                    text = "+${destination.facilities.size - 3}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 简短描述
                Text(
                    text = destination.description?.take(100)?.plus("...") ?: "No description available",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    lineHeight = 20.sp,
                    maxLines = 2
                )
            }
        }
    }
}

fun getCountryDescription(country: String): String {
    return when (country) {
        "Denmark" -> "Denmark offers scenic coastal routes and charming countryside perfect for RV travel. With well-maintained campgrounds, easy road access, and a compact geography, it's a great destination for exploring Viking heritage and coastal beauty by RV."
        "Finland" -> "Finland is an RV-friendly country known for its thousands of lakes, dense forests, and peaceful natural surroundings. It offers excellent campsite infrastructure and the freedom to explore vast wilderness areas under the Everyman's Right."
        "Norway" -> "Norway's dramatic fjords, mountains, and scenic coastal roads create a stunning backdrop for RV adventures. With a strong tradition of outdoor life and numerous campsites, Norway is perfect for travelers seeking nature and breathtaking views."
        "Sweden" -> "Sweden combines rich forests, beautiful lakes, and a relaxed travel atmosphere. Thanks to the Right of Public Access, RV travelers can enjoy the countryside freely, along with a wide range of official campgrounds and nature spots."
        else -> "Explore the beautiful destinations of $country by RV. From scenic landscapes to cultural attractions, $country offers unique experiences for RV travelers with various campgrounds and facilities throughout the country."
    }
}

