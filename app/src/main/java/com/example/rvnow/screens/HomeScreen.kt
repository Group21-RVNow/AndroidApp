package com.example.rvnow
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rvnow.model.RV
import com.example.rvnow.viewmodels.RVViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.Star
import com.example.rvnow.viewmodels.AuthViewModel
import androidx.compose.foundation.layout.Column as Column
import androidx.compose.material.icons.filled.ArrowBack
private val SECTION_SPACING_LARGE = 32.dp
private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_SMALL = 16.dp
private val BUTTONS_TO_RENTAL_SPACING_LARGE = 48.dp
private val BUTTONS_TO_RENTAL_SPACING = 40.dp
private val BUTTONS_TO_RENTAL_SPACING_SMALL = 32.dp
private val BETWEEN_RVS_SPACING_LARGE = 48.dp
private val BETWEEN_RVS_SPACING = 36.dp
private val BETWEEN_RVS_SPACING_SMALL = 24.dp
private val HORIZONTAL_PADDING_LARGE = 24.dp
private val HORIZONTAL_PADDING = 16.dp
private val HORIZONTAL_PADDING_SMALL = 12.dp
private val SECTION_TITLE_PADDING_START = 8.dp
private val SECTION_TITLE_PADDING_BOTTOM = 16.dp
private val CARD_SPACING = 12.dp
private val BUTTON_SPACING = 16.dp
private val BUTTON_SPACING_SMALL = 8.dp
private val BUTTON_HEIGHT = 48.dp
private val BUTTON_CORNER_RADIUS = 8.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CONTENT_PADDING = 12.dp

@Composable
fun HomeScreen(
    rvViewModel: RVViewModel = viewModel(),
    navController: NavController,
    authViewModel: AuthViewModel

) {
    val rvList by rvViewModel.rvs.collectAsState()
    val heroImage = rememberAsyncImagePainter("file:///android_asset/images/brighter_image_3.png")

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val isMediumScreen = screenWidth >= 360.dp && screenWidth < 600.dp
    val isLargeScreen = screenWidth >= 600.dp

    val horizontalPadding = when {
        isLargeScreen -> HORIZONTAL_PADDING_LARGE
        isSmallScreen -> HORIZONTAL_PADDING_SMALL
        else -> HORIZONTAL_PADDING
    }

    val sectionSpacing = when {
        isLargeScreen -> SECTION_SPACING_LARGE
        isSmallScreen -> SECTION_SPACING_SMALL
        else -> SECTION_SPACING
    }

    val buttonsToRentalSpacing = when {
        isLargeScreen -> BUTTONS_TO_RENTAL_SPACING_LARGE
        isSmallScreen -> BUTTONS_TO_RENTAL_SPACING_SMALL
        else -> BUTTONS_TO_RENTAL_SPACING
    }

    val betweenRVsSpacing = when {
        isLargeScreen -> BETWEEN_RVS_SPACING_LARGE
        isSmallScreen -> BETWEEN_RVS_SPACING_SMALL
        else -> BETWEEN_RVS_SPACING
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroSection(heroImage = heroImage)

        Spacer(modifier = Modifier.height(sectionSpacing))

        ActionButtonsSection(
            navController = navController,
            horizontalPadding = horizontalPadding,
            isSmallScreen = isSmallScreen
        )

        Spacer(modifier = Modifier.height(buttonsToRentalSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = horizontalPadding)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.height(sectionSpacing))

        PopularRVsSection(
            title = "Popular Rental RVs",
            rvs = rvList.filter { !it.isForSale && it.isPopular },
            navController = navController,
            horizontalPadding = horizontalPadding,
            authViewModel = authViewModel,
            rvViewModel = rvViewModel

        )

        Spacer(modifier = Modifier.height(betweenRVsSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = horizontalPadding)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(sectionSpacing))

        PopularRVsSection(
            title = "Popular Listed RVs",
            rvs = rvList.filter { it.isForSale  },
            navController = navController,
            horizontalPadding = horizontalPadding,
            authViewModel = authViewModel,
            rvViewModel = rvViewModel
        )


        Spacer(modifier = Modifier.height(sectionSpacing))
    }
}

@Composable
private fun HeroSection(heroImage: Painter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Image(
            painter = heroImage,
            contentDescription = "RV with aurora background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = HORIZONTAL_PADDING + SECTION_TITLE_PADDING_START, bottom = 75.dp)
        ) {
            Text(
                text = "Journey boldly",
                color = Color.White,
                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.5.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "feel at home wherever you go",
                color = Color.White,
                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.5.sp,
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    navController: NavController,
    horizontalPadding: Dp,
    isSmallScreen: Boolean
) {
    fun darkenColor(color: Color, factor: Float = 0.25f): Color {
        return Color(
            red = (color.red * (1 - factor)).coerceIn(0f, 1f),
            green = (color.green * (1 - factor)).coerceIn(0f, 1f),
            blue = (color.blue * (1 - factor)).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        val iconWidth = 24.dp
        val buttonSpacing = if (isSmallScreen) BUTTON_SPACING_SMALL else BUTTON_SPACING


        val colors = listOf(
            Color(0xFFE27D5F),  // 陶土橙（温暖活力）
            Color(0xFF5D8AA8),  // 湖蓝（自然平衡）
            Color(0xFF6B8E23),  // 苔藓绿（生机感）
            Color(0xFFA78A7F)   // 浅驼色（家的温暖）
        ).map { darkenColor(it) }

        if (isSmallScreen) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CustomActionButton(
                    text = "Rent an RV",
                    icon = Icons.Default.DirectionsCar,
                    onClick = { navController.navigate("rental") },
                    color = colors[0],
                    iconWidth = iconWidth,
                    isSmallScreen = true
                )

                CustomActionButton(
                    text = "Buy an RV",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("sales") },
                    color = colors[1],
                    iconWidth = iconWidth,
                    isSmallScreen = true
                )

                CustomActionButton(
                    text = "RV Owner",
                    icon = Icons.Default.Key,
                    onClick = { navController.navigate("owner") },
                    color = colors[2],
                    iconWidth = iconWidth,
                    isSmallScreen = true
                )

                CustomActionButton(
                    text = "Go RVing",
                    icon = Icons.Default.Public,
                    onClick = { navController.navigate("go_rving") },
                    color = colors[3],
                    iconWidth = iconWidth,
                    isSmallScreen = true
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomActionButton(
                    text = "Rent an RV",
                    icon = Icons.Default.DirectionsCar,
                    onClick = { navController.navigate("rental") },
                    color = colors[0],
                    iconWidth = iconWidth,
                    isSmallScreen = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(buttonSpacing))
                CustomActionButton(
                    text = "Buy an RV",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("sales") },
                    color = colors[1],
                    iconWidth = iconWidth,
                    isSmallScreen = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(buttonSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomActionButton(
                    text = "RV Owner",
                    icon = Icons.Default.Key,
                    onClick = { navController.navigate("owner") },
                    color = colors[2],
                    iconWidth = iconWidth,
                    isSmallScreen = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(buttonSpacing))
                CustomActionButton(
                    text = "Go RVing",
                    icon = Icons.Default.Public,
                    onClick = { navController.navigate("go_rving") },
                    color = colors[3],
                    iconWidth = iconWidth,
                    isSmallScreen = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CustomActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color,
    iconWidth: Dp,
    isSmallScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(BUTTON_CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.16f),
            contentColor = color
        ),
        modifier = modifier
            .height(BUTTON_HEIGHT)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isSmallScreen) Arrangement.Start else Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(iconWidth)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isSmallScreen) TextAlign.Start else TextAlign.Center
            )
        }
    }
}

@Composable
private fun PopularRVsSection(
    title: String,
    rvs: List<RV>,
    navController: NavController,
    horizontalPadding: Dp,
    authViewModel: AuthViewModel,
    rvViewModel: RVViewModel
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = SECTION_TITLE_PADDING_START,
                bottom = SECTION_TITLE_PADDING_BOTTOM
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(CARD_SPACING),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(rvs) { rv ->
                LaunchedEffect(rv.id) {
                    rvViewModel.loadAverageRating(rv.id)
                    Log.d("AverageRating", "Loading Average Rating for RV: ${rv.id}")
                }
                RVCard(
                    rv = rv,
                    rvId = rv.id,  // Correctly passing rvId
                    title = rv.name ?: if (title.contains("Rental")) "Luxury RV" else "Premium RV",
                    features = rv.description ?: if (title.contains("Rental"))
                        "Family | Amazing Features | Comfort..."
                    else "Spacious | Luxury | All Included",
                    navController = navController,
                    horizontalPadding = horizontalPadding,
                    rvViewModel = rvViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun StarRatingBar2(
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
private fun RVCard(
    authViewModel: AuthViewModel,
    rv:RV,
    rvId: String,
    title: String,
    features: String,
    navController: NavController,
    horizontalPadding: Dp,
    rvViewModel: RVViewModel,
) {
    val rvList by rvViewModel.rvs.collectAsState()
    // Find the RV that matches the provided rvId
    val rvSpecific = rvList.firstOrNull { it.id == rvId }
    val name = rvSpecific?.name ?: ""

    var currentImageIndex by remember { mutableStateOf(0) }
    val allImages = listOfNotNull(rv.imageUrl) + (rv.additionalImages ?: emptyList())
    val visibleImages = allImages.take(6)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = (screenWidth - horizontalPadding * 2 - CARD_SPACING) * 0.8f
    val ratings by rvViewModel.ratings.collectAsState(emptyList())
    val context = LocalContext.current

    val isForRental = rvSpecific?.isForRental ?: false
    val imageUrl = rvSpecific?.imageUrl ?: ""

    val isForSale = rvSpecific?.isForSale ?: false

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
    val textSectionHeight = cardHeight - imageHeight - CARD_CONTENT_PADDING * 2
    var isProcessingFavorite by remember { mutableStateOf(false) }
    LaunchedEffect(rvId, currentUser?.id) {
        currentUser?.id?.let { userId ->
            rvViewModel.checkFavoriteStatus(userId, rvId) { isFav ->
                isFavorite = isFav
            }
        } ?: run {
            // User not logged in, set to false
            isFavorite = false
        }
    }


    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { navController.navigate("detail/${rv.id}?sourcePage=home") },
    ) {
        Column {
            // Image section with fixed height
            Box(
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = CARD_CORNER_RADIUS,
                            topEnd = CARD_CORNER_RADIUS,
                            bottomStart = CARD_CORNER_RADIUS / 1.5f,
                            bottomEnd = CARD_CORNER_RADIUS / 1.5f
                        )
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = visibleImages.getOrNull(
                            currentImageIndex
                        )
                    ),
                    contentDescription = "RV Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (visibleImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                currentImageIndex =
                                    (currentImageIndex - 1 + visibleImages.size) % visibleImages.size
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                currentImageIndex = (currentImageIndex + 1) % visibleImages.size
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(visibleImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (index == currentImageIndex) Color.White else Color.White.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(1.dp)
                            )
                            if (index < visibleImages.size - 1) Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }

            // Text section with fixed height
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CARD_CONTENT_PADDING, vertical = CARD_CONTENT_PADDING)
            ) {
                // Title row
                Box(
                    modifier = Modifier.fillMaxWidth(),

                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.Center) // This ensures the Text is centered horizontally within the Box
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = features,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Box(

                    ) {
                        // Display stars and average rating
                        StarRatingBar2(
                            rating = rating,
                            averageRating = averageRating, // Show average behind the stars
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
}
