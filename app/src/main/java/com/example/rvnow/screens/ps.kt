package com.example.rvnow

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rvnow.viewmodels.AuthViewModel
import com.example.rvnow.viewmodels.RVViewModel
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.rvnow.model.Favorite
import com.example.rvnow.model.User

@Composable
fun PS(
    navController: NavController,
    authViewModel: AuthViewModel,
    rvViewModel: RVViewModel = viewModel()
) {
//    val cartItems by rvViewModel.cartItems.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
    val rvList by rvViewModel.rvs.collectAsState()
    val userInfo by authViewModel.userInfo.observeAsState()
    val userId = userInfo?.id
    val fullName by authViewModel.fullName.observeAsState()
    val email = userInfo?.email ?: "No Email"  // Fallback value
    val profilePictureUrl = userInfo?.profilePictureUrl ?: ""  // Fallback to empty string

//    val fetchedFavourites by rvViewModel.fetchedFavourites.collectAsState()
//    val favorites = fetchedFavourites ?: emptyList()

    val favorites: List<Favorite> by rvViewModel.fetchedFavourites.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null) {
            Log.d("ProfileScreen", "Current user id: ${ userId }")
//            authViewModel.fetchUserData(userId)
            rvViewModel.loadFavorites(userId)
        }
    }


    // Debug logging
    LaunchedEffect(favorites) {
        Log.d("ProfileScreen", "Current favorites: ${favorites.size}")}

    LaunchedEffect(Unit) {
        println("Profile recomposed or entered")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 优化后的登出按g钮（图标式）
        IconButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("Signin|up") {
                    popUpTo("Signin|up") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp, end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp))
        }

        // 用户信息区域
        Spacer(modifier = Modifier.height(32.dp))
        UserInfoSection(
            profilePictureUrl = profilePictureUrl,
            user = userInfo,
            fullName = fullName,
            email = email
        )
//        UserInfoSection(profilePictureUrl=profilePictureUrl, user =userInfo, fullName=fullName, email=email)

        Spacer(modifier = Modifier.height(32.dp))

        // 收藏和发布区域
        Column(verticalArrangement = Arrangement.spacedBy(36.dp)) {
            // 收藏车辆部分
//            if (fetchedFavourites.isNotEmpty()) {
//                FavoriteSection(
//                    title = "My Favorites",
//                    items = fetchedFavourites,
//                    navController = navController
//                )
//                CustomDivider()
//            }

            // 租赁收藏
//
                FavoriteSection(
                    title = "Rental Favorites",
                    favorites = favorites.filter{!it.isForSale},
                    navController = navController
                )


            CustomDivider()


//            FavoriteSection(
//                title = "Purchase Favorites",
//                favorites = rvList.filter{ it.isForSale},
//                navController = navController
//            )

            CustomDivider()

            // 已发布车辆
//            PublishedSection(
//                rvs = rvList.filter { it.ownerId == userInfo?.uid },
//                navController = navController
//            )
        }
    }
}


//work well now
@Composable
private fun UserInfoSection(
    profilePictureUrl: String?,
    user: User?,
    fullName: String?,
    email: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageUrl = profilePictureUrl ?: ""

        // 用户信息
        user?.let {

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop
            )


            Text(
                text = fullName ?: "No Name",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = it.email ?: "No Email",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } ?: run {
            Text(
                text = "No user information available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // 编辑按钮
        Button(
            onClick = { /* 处理编辑操作 */ },
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Edit", fontSize = 16.sp)
        }
    }
}

@Composable
private fun FavoriteSection(
    title: String,
    favorites: List<Favorite>,
    navController: NavController
) {
    Column {
        // 标题行
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${favorites.size} Favorites",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 横向滚动列表
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(favorites) {
                    rv ->
                FavoriteRVCard(
                    favorite = rv,
                    navController = navController,
//                    onClick = { navController.navigate("detail/${rv.rvId}?sourcePage=profile") }
                )
            }
        }
    }
}


@Composable
private fun FavoriteRVCard(
    favorite: Favorite,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .width(160.dp) // Adjusted to a reasonable width
            .clickable {
                navController.navigate("detail/${favorite.rvId}?sourcePage=profile")
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image section
            AsyncImage(
                model = favorite.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            // Info section
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = favorite.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // You can add rating display here if needed
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Optional place or location text, if available
//                Text(
//                    text = favorite.place,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                )
            }
        }
    }
}

//@Composable
//private fun PublishedSection(
//    rvs: List<RV>,
//    navController: NavController
//) {
//    Column {
//        // 标题行
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = "Published RVs",
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "${rvs.size} Published",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // 横向滚动列表
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            items(rvs) { rv ->
//                FavoriteRVCard(favorite = rv, onClick = { navController.navigate("detail/${rv.id}") })
//            }
//        }
//    }
//}

//@Composable
//private fun FavoriteRVCard(
//    favorite: Favorite,
//    navController: NavController,
////    favorite: Favorite
////    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .width(50.dp),
////            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column {
//            Box(
//                modifier = Modifier
//                    .height(120.dp) // Fixed height
//                    .fillMaxWidth()
//                    .clickable {
//
//                        navController.navigate(
//                            "detail/${favorite.rvId}?sourcePage=profile"
//                        ) {
//                            AsyncImage(
//                                model = favorite.imageUrl,
//                                contentDescription = null,
//                                modifier = Modifier.fillMaxSize(),
////                    placeholder = painterResource(id = R.drawable.placeholder),
////                    error = painterResource(id = R.drawable.error_image),
//                                contentScale = ContentScale.Crop,
//                                alignment = Alignment.Center
//                            )
//                        }
//
//
//                        Column(modifier = Modifier.padding(12.dp)) {
//                            // 优化后的名称和评分布局
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Text(
//                                    text = favorite.name,
//                                    style = MaterialTheme.typography.titleSmall,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    modifier = Modifier.weight(1f)
//                                )
//
////                    Row(
////                        verticalAlignment = Alignment.CenterVertically,
////                        modifier = Modifier.padding(start = 4.dp)
////                    ) {
////                        Icon(
////                            imageVector = Icons.Default.Star,
////                            contentDescription = "Rating",
////                            tint = Color(0xFFFFC107),
////                            modifier = Modifier.size(14.dp)
////                        )
////                        Text(
////                            text = "%.1f".format(rv.averageRating),
////                            style = MaterialTheme.typography.bodySmall,
////                            modifier = Modifier.padding(start = 4.dp)
////                        )
////                    }
//                            }
//
//                            Spacer(modifier = Modifier.height(4.dp))
//
////                Text(
////                    text = rv.place,
////                    style = MaterialTheme.typography.bodySmall,
////                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
////                )
//                        }
//
//
//                    }
//        }
//    }
//}





@Composable
private fun CustomDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(0.9f)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}