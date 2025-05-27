package com.example.animal_adoption.screens.widgets

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.animal_adoption.model.UserDTO
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class UserBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun UserBottomBar(
    navController: NavHostController,
    user: UserDTO?
) {
    val userJson = user?.let {
        URLEncoder.encode(Gson().toJson(it), StandardCharsets.UTF_8.toString())
    } ?: "null"

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val primaryOrange = Color(0xFFFF7043)
    val items = listOf(
        UserBottomNavItem("Home", Icons.Default.Home, "UserHome/$userJson"),
        UserBottomNavItem("Animals", Icons.Default.Search, "AdoptionSearchBarUser/$userJson"), // Usamos Icons.Default.Search para "Animals"
        UserBottomNavItem("My Requests", Icons.Default.Pets, "UserAdoptionRequests/$userJson"),
        UserBottomNavItem("Profile", Icons.Default.AccountCircle, "UserProfile/$userJson")
    )

    NavigationBar(
        containerColor = primaryOrange,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val contentColor = if (currentRoute == item.route ||
                (item.route.contains("UserHome") && currentRoute?.startsWith("UserHome") == true) ||
                (item.route.contains("AdoptionSearchBarUser") && currentRoute?.startsWith("AdoptionSearchBarUser") == true) ||
                (item.route.contains("UserAdoptionRequests") && currentRoute?.startsWith("UserAdoptionRequests") == true) ||
                (item.route.contains("UserProfile") && currentRoute?.startsWith("UserProfile") == true)) {
                Color.White
            } else {
                Color.White.copy(alpha = 0.7f)
            }
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = contentColor) },
                label = { Text(item.label, style = MaterialTheme.typography.labelMedium, color = contentColor) },
                selected = currentRoute == item.route ||
                        (item.route.contains("UserHome") && currentRoute?.startsWith("UserHome") == true) ||
                        (item.route.contains("AdoptionSearchBarUser") && currentRoute?.startsWith("AdoptionSearchBarUser") == true) ||
                        (item.route.contains("UserAdoptionRequests") && currentRoute?.startsWith("UserAdoptionRequests") == true) ||
                        (item.route.contains("UserProfile") && currentRoute?.startsWith("UserProfile") == true),
                onClick = {
                    if (item.route.contains("UserAdoptionRequests") && user == null) {
                        Log.e("UserBottomBar", "User is null, cannot navigate to UserAdoptionRequests.")
                        return@NavigationBarItem
                    }

                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}