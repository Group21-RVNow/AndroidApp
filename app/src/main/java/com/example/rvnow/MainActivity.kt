package com.example.rvnow

import com.example.rvnow.screens.DestinationDetailsScreen
import com.example.rvnow.screens.CountryDestinationsScreen
import com.example.rvnow.screens.SearchResultsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.rvnow.model.RV
import com.example.rvnow.model.RVType
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rvnow.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import com.example.rvnow.screens.OwnerScreen
import com.example.rvnow.screens.GoRVingScreen
import com.example.rvnow.screens.TravelGuideDetailsScreen
import com.example.rvnow.viewmodels.RVViewModel
import androidx.navigation.navArgument
import androidx.navigation.NavType


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val rvViewModel: RVViewModel = viewModel()
            RVNowApp(authViewModel = authViewModel, rvViewModel = rvViewModel)
        }
    }
}


@Composable
fun RVNowApp(authViewModel: AuthViewModel, rvViewModel: RVViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController, authViewModel) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController, authViewModel = authViewModel)
            }
            composable("cart") {
                CartScreen(navController = navController, authViewModel = authViewModel, rvViewModel = rvViewModel)
            }
            composable("owner") {
                OwnerScreen(navController = navController)
            }
            composable("signup") {
                SignupScreen(navController = navController)
            }
            composable("signin_or_signup") {
                LoginScreen(navController = navController, authViewModel = authViewModel)
            }
            composable("rental") {
                RentalScreen(navController = navController)
            }
            composable("sales") {
                SalesScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController, authViewModel = authViewModel, rvViewModel = rvViewModel)
            }
            composable("go_rving") {
                GoRVingScreen(navController = navController)
            }
            composable("travel_guide_details/{guideId}") { backStackEntry ->
                val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
                TravelGuideDetailsScreen(navController = navController, guideId = guideId)
            }
            composable(
                "destination_details/{destinationId}",
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("destinationId") ?: ""
                DestinationDetailsScreen(navController = navController, destinationId = destinationId)
            }
            composable(
                "country_destinations/{country}",
                arguments = listOf(navArgument("country") { type = NavType.StringType })
            ) { backStackEntry ->
                val country = backStackEntry.arguments?.getString("country") ?: ""
                CountryDestinationsScreen(navController = navController, country = country)
            }
            composable("search_results") {
                SearchResultsScreen(navController)
            }
            composable("detail/{rvId}?sourcePage={sourcePage}") { backStackEntry ->
                val rvId = backStackEntry.arguments?.getString("rvId") ?: ""
                val sourcePage = backStackEntry.arguments?.getString("sourcePage") ?: "home"
                RVDetailScreen(
                    rvId = rvId,
                    rvViewModel = rvViewModel,
                    authViewModel = authViewModel,
                    navController = navController,
                    sourcePage = sourcePage
                )
            }
        }
    }
}


@Composable
fun BottomNavBar(navController: NavController, authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(false)

    val items = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Rent", Icons.Default.DirectionsCar, "rental"),
        NavItem("Buy", Icons.Default.ShoppingCart, "sales"),
        NavItem("Owner", Icons.Default.Key, "owner"),
        NavItem("Cart", Icons.Default.Casino, "cart"),
        NavItem(
            if (isLoggedIn) "Profile" else "Login",
            if (isLoggedIn) Icons.Default.Person else Icons.Default.Login,
            if (isLoggedIn) "profile" else "signin_or_signup"
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
//                            popUpTo(navController.graph.startDestinationId) { saveState = true }
//                            launchSingleTop = true
//                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}


data class NavItem(val label: String, val icon: ImageVector, val route: String)
