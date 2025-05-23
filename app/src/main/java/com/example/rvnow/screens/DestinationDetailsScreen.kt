package com.example.rvnow.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
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
import com.example.rvnow.viewmodels.GoRVingViewModel
import androidx.compose.ui.text.style.TextOverflow

private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_SMALL = 16.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CONTENT_PADDING = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailsScreen(
    navController: NavController,
    destinationId: String
) {
    val viewModel = viewModel<GoRVingViewModel>()

    LaunchedEffect(destinationId) {
        viewModel.getDestinationById(destinationId)
    }

    val selectedDestination by viewModel.selectedDestination.collectAsState()
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
                        selectedDestination?.name ?: "Destination Details",
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
        } else {
            selectedDestination?.let { destination ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 修改后的主图片部分 - 移除了名称和背景
                    item {
                        Box {
                            AsyncImage(
                                model = destination.imageUrl,
                                contentDescription = destination.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )

                            // 只保留评分显示
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


                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HORIZONTAL_PADDING)
                                .offset(y = (-20).dp),
                            shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(CARD_CONTENT_PADDING)
                            ) {
                                // 第一行：图标和主要信息
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // 价格范围
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.AttachMoney,
                                            contentDescription = "Price Range",
                                            tint = primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = destination.priceRange ?: "N/A",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    // 最佳时间
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Best Time",
                                            tint = secondaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = destination.bestTime ?: "N/A",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    // 停车位
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.LocalParking,
                                            contentDescription = "Parking",
                                            tint = tertiaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${destination.parkingSpots?.size ?: 0}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                // 第二行：标题（确保对齐）
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = "Price Range",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = "Best Time",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = "Parking Spots",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 描述部分
                    item {
                        Column(
                            modifier = Modifier.padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL)
                        ) {
                            Text(
                                text = "Description",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Text(
                                    text = destination.description ?: "No description available",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Default,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(CARD_CONTENT_PADDING)
                                )
                            }
                        }
                    }

                    // 分隔线
                    item {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = HORIZONTAL_PADDING)
                                .background(Color.LightGray.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                    }

                    // 最佳访问时间详情
                    item {
                        Column(
                            modifier = Modifier.padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL)
                        ) {
                            Text(
                                text = "Best Time to Visit",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                                colors = CardDefaults.cardColors(
                                    containerColor = secondaryColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(CARD_CONTENT_PADDING)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Best Time",
                                        tint = secondaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = destination.bestTimeToVisit ?: "Information not available",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Default,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }
                    }

                    // 分隔线
                    item {
                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = HORIZONTAL_PADDING)
                                .background(Color.LightGray.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))
                    }

                    // 停车位详情
                    item {
                        Column(
                            modifier = Modifier.padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL)
                        ) {
                            Text(
                                text = "Parking Spots",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (!destination.parkingSpots.isNullOrEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    items(destination.parkingSpots) { spot ->
                                        ParkingSpotCard(spot, tertiaryColor)
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                                    colors = CardDefaults.cardColors(
                                        containerColor = tertiaryColor.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(CARD_CONTENT_PADDING)
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = "No Parking",
                                            tint = tertiaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = "No parking spots information available",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily.Default,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 底部间距
                    item {
                        Spacer(modifier = Modifier.height(SECTION_SPACING))
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Destination information not available",
                        color = Color.Gray,
                        fontFamily = FontFamily.Default,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ParkingSpotCard(spot: Map<String, Any>, tertiaryColor: Color) {
    val name = spot["name"]?.toString() ?: "Unnamed Spot"
    val coordinates = spot["coordinates"]?.toString()?.replace("{", "")?.replace("}", "")

    // Extract latitude and longitude
    val latitude = coordinates?.split(",")?.getOrNull(0)?.split("=")?.getOrNull(1)?.trim()
    val longitude = coordinates?.split(",")?.getOrNull(1)?.split("=")?.getOrNull(1)?.trim()

    // Format coordinates display
    val formattedLocation = if (latitude != null && longitude != null) {
        "Lat: $latitude, Long: $longitude"
    } else {
        "Location unavailable"
    }

    Card(
        modifier = Modifier
            .width(280.dp),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Location",
                    tint = tertiaryColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = formattedLocation,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    color = Color.DarkGray
                )
            }

            // Add other parking spot information
            spot.forEach { (key, value) ->
                if (key != "name" && key != "imageUrl" && key != "coordinates") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$key: $value",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}