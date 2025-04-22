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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.rvnow.model.Favourite
import com.example.rvnow.model.RV
import com.example.rvnow.model.User
//import com.example.rvnow.viewmodels.RVViewModel
@Composable
fun ProfileScreen(
    navController: NavController,
    rvViewModel: RVViewModel,
    authViewModel: AuthViewModel
) {
//    val cartItems by rvViewModel.cartItems.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(initial = false)
    val rvList by rvViewModel.rvs.collectAsState()
    val userInfo by authViewModel.userInfo.observeAsState()
    val userId = userInfo?.id

    LaunchedEffect(userId) {
        if (userId != null) {
            Log.d("ProfileScreen", "Current user id: ${ userId }")
//            authViewModel.fetchUserData(userId)
            rvViewModel.loadFavorites(userId)
        }
    }

    val fullName by authViewModel.fullName.observeAsState()
    val email = userInfo?.email ?: "No Email"  // Fallback value
    val profilePictureUrl = userInfo?.profilePictureUrl ?: ""
    val favorites: List<Favourite> by rvViewModel.fetchedFavourites.collectAsState()
    val publishedRV = rvList.filter { it.ownerId == userId }



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
//        profilePictureUrl: String?,
//    user: User?,
//    fullName: String?,
//    email: String,
//    authViewModel: AuthViewModel
        UserInfoSection(
            profilePictureUrl = profilePictureUrl,
            user = userInfo,
            fullName = fullName,
            email = email,
            authViewModel = authViewModel
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


            // 已发布车辆
            PublishedSection(
                rvs = publishedRV,
                navController = navController
            )

            CustomDivider()

//            if (isLoggedIn) {
//
//                FavoriteSection(
////                    title = "Rental Favorites",
//                    favourites = favorites.filter { !it.isForSale },
//                    navController = navController
//                )
//
//                // Add Purchase Favorites too if needed
//                CustomDivider()
//            } else {
//                Text("You are not logged in", modifier = Modifier.padding(16.dp))
//            }
//
//
//            CustomDivider()


        }
    }
}


//work well now
@Composable
private fun UserInfoSection(
    profilePictureUrl: String?,
    user: User?,
    fullName: String?,
    email: String,
    authViewModel: AuthViewModel
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(fullName ?: "") }
    var editedProfileUrl by remember { mutableStateOf(profilePictureUrl ?: "") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageUrl = profilePictureUrl ?: ""

        // Profile Image
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = fullName ?: "No Name",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user?.email ?: "No Email",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isEditing = !isEditing },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA3DC6F),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(if (isEditing) "Cancel" else "Edit", fontSize = 16.sp)
        }

        if (isEditing) {
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = editedProfileUrl,
                onValueChange = { editedProfileUrl = it },
                label = { Text("Profile Picture URL") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    user?.id?.let { userId ->
                        authViewModel.updateUserInfo(userId, editedName, editedProfileUrl)
                        isEditing = false // close edit form
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ),
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}






@Composable
private fun FavoriteSection1(
    title: String,
    favorites: List<Favourite>,
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
//        LazyRow {
//            items(favorites, key = { it.rvId }) { favorite ->
//                FavoriteRVCard(favorite = favorite, navController = navController)
//            }
//        }

        // 横向滚动列表
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(favorites, key = { it.rvId }) { rv ->
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
private fun PublishedSection(
    rvs: List<RV>,
    navController: NavController
) {
    Column {
        // 标题行
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Published RVs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${rvs.size} Published",
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
            items(rvs) { rv ->
                FavoriteRVCard1(
                    rv = rv, onClick = { navController.navigate("detail/${rv.id}") },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun FavoriteRVCard(
    favorite: Favourite,
    navController: NavController,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = favorite.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

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
                }
            }
        }
    }
}

@Composable
private fun FavoriteSection(
    title: String,
    favorites: List<Favourite>,
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
fun FavoriteRVCard1(
    rv: RV,
    navController: NavController,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = rv.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = rv.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}





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