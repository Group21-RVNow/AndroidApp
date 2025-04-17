package com.example.rvnow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import com.example.rvnow.viewmodels.GoRVingViewModel

// Define spacing constants consistent with HomeScreen
private val SECTION_SPACING = 24.dp
private val SECTION_SPACING_SMALL = 16.dp
private val HORIZONTAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp
private val CARD_CONTENT_PADDING = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelGuideDetailsScreen(
    navController: NavController,
    guideId: String
) {
    val viewModel = viewModel<GoRVingViewModel>()

    LaunchedEffect(guideId) {
        viewModel.getTravelGuideById(guideId)
    }

    val selectedTravelGuide by viewModel.selectedTravelGuide.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Define colors consistent with HomeScreen
    val primaryColor = Color(0xFFE27D5F)  // Terracotta orange
    val secondaryColor = Color(0xFF5D8AA8)  // Lake blue
    val tertiaryColor = Color(0xFF6B8E23)  // Moss green
    val neutralColor = Color(0xFFA78A7F)   // Light camel

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        selectedTravelGuide?.title ?: "Travel Guide",
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
            selectedTravelGuide?.let { guide ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Main image
                    Box {
                        AsyncImage(
                            model = guide.imageUrl,
                            contentDescription = guide.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Add semi-transparent overlay for better text readability
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Color.Black.copy(alpha = 0.5f)
                                )
                        )

                        // Add title at the bottom of the image
                        Text(
                            text = guide.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL)
                        )
                    }

                    // Author and date info card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HORIZONTAL_PADDING)
                            .offset(y = (-20).dp),
                        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(CARD_CONTENT_PADDING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Author info
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Author",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "By RVNow",  // Updated author
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Date info
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Date",
                                    tint = secondaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "2025-04-16",  // Updated date
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Summary section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HORIZONTAL_PADDING, SECTION_SPACING_SMALL),
                        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                        colors = CardDefaults.cardColors(
                            containerColor = secondaryColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = guide.summary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Default,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(CARD_CONTENT_PADDING)
                        )
                    }

                    // Tags section
                    if (guide.tags.isNotEmpty()) {
                        Text(
                            text = "Topics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            modifier = Modifier.padding(
                                start = HORIZONTAL_PADDING,
                                top = SECTION_SPACING_SMALL,
                                bottom = 8.dp
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HORIZONTAL_PADDING)
                                .padding(bottom = SECTION_SPACING_SMALL)
                        ) {
                            guide.tags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = primaryColor.copy(alpha = 0.1f),
                                    tonalElevation = 0.dp
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = HORIZONTAL_PADDING)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(SECTION_SPACING_SMALL))

                    // Content section with improved readability
                    Text(
                        text = "Guide Content",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default,
                        modifier = Modifier.padding(
                            start = HORIZONTAL_PADDING,
                            bottom = SECTION_SPACING_SMALL
                        )
                    )

                    // Split content into sections for better readability
                    val contentSections = parseContentIntoSections(guide.content)

                    contentSections.forEach { section ->
                        // Section title if available
                        if (section.title.isNotEmpty()) {
                            Text(
                                text = section.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default,
                                color = tertiaryColor,
                                modifier = Modifier.padding(
                                    horizontal = HORIZONTAL_PADDING,
                                    vertical = 8.dp
                                )
                            )
                        }

                        // Section paragraphs
                        section.paragraphs.forEach { paragraph ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = HORIZONTAL_PADDING,
                                        vertical = 4.dp
                                    ),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Text(
                                    text = paragraph,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Default,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Bottom spacing
                    Spacer(modifier = Modifier.height(SECTION_SPACING))
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Travel guide information not available",
                        color = Color.Gray,
                        fontFamily = FontFamily.Default,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Data class to represent a content section
data class ContentSection(
    val title: String,
    val paragraphs: List<String>
)

// Helper function to parse content into sections for better readability
fun parseContentIntoSections(content: String): List<ContentSection> {
    val sections = mutableListOf<ContentSection>()

    // Split content by double newlines to get paragraphs
    val paragraphs = content.split("\n\n").filter { it.isNotEmpty() }

    var currentTitle = ""
    val currentParagraphs = mutableListOf<String>()

    paragraphs.forEach { paragraph ->
        // Check if paragraph looks like a section title (short and ends with colon or all caps)
        if ((paragraph.length < 50 && (paragraph.endsWith(":") || paragraph.endsWith("."))) ||
            paragraph.uppercase() == paragraph) {

            // If we have accumulated paragraphs, save the previous section
            if (currentParagraphs.isNotEmpty()) {
                sections.add(ContentSection(currentTitle, currentParagraphs.toList()))
                currentParagraphs.clear()
            }

            // Set new title
            currentTitle = paragraph.removeSuffix(":").removeSuffix(".")
        } else {
            // Regular paragraph
            currentParagraphs.add(paragraph)
        }
    }

    // Add the last section
    if (currentParagraphs.isNotEmpty()) {
        sections.add(ContentSection(currentTitle, currentParagraphs.toList()))
    }

    // If no sections were created with titles, create one section with all paragraphs
    if (sections.isEmpty() && paragraphs.isNotEmpty()) {
        sections.add(ContentSection("", paragraphs))
    }

    return sections
}



