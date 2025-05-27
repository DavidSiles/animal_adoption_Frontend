package com.example.animal_adoption.screens.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson

data class ShelterBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun ShelterBottomBar(
    navController: NavHostController,
    shelter: ShelterDTO?
) {
    val shelterJson = Gson().toJson(shelter)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val TuonsBlue = Color(0xFF4285F4)
    val items = listOf(
        ShelterBottomNavItem("Home", Icons.Default.Home, "ShelterHome/$shelterJson"),
        ShelterBottomNavItem("Animals", Icons.AutoMirrored.Filled.List, "ShelterListAnimals/$shelterJson"), // Changed icon to List
        ShelterBottomNavItem("Requests", Icons.Default.Pets, "ShelterAdoptionRequests/${shelter?.id}"), // Changed label to "Requests"
        ShelterBottomNavItem("Profile", Icons.Default.AccountCircle, "ShelterProfile/$shelterJson")
    )

    NavigationBar(
        containerColor = TuonsBlue, // Apply the blue color
        tonalElevation = 4.dp // Add a subtle elevation
    ) {
        items.forEach { item ->
            // Determine if the item is selected based on the current route
            val isSelected = currentRoute == item.route ||
                    (item.route.contains("ShelterHome") && currentRoute?.startsWith("ShelterHome") == true) ||
                    (item.route.contains("ShelterListAnimals") && currentRoute?.startsWith("ShelterListAnimals") == true) ||
                    (item.route.contains("ShelterAdoptionRequests") && currentRoute?.startsWith("ShelterAdoptionRequests") == true) ||
                    (item.route.contains("ShelterProfile") && currentRoute?.startsWith("ShelterProfile") == true)

            // Determine content color based on selection state
            val contentColor = if (isSelected) {
                Color.White // White for selected item
            } else {
                Color.White.copy(alpha = 0.7f)
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = contentColor
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Default,
                        color = contentColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (item.route.contains("ShelterAdoptionRequests") && shelter?.id == null) {
                        Log.e("ShelterBottomBar", "Shelter ID is null, cannot navigate to ShelterAdoptionRequests.")
                        return@NavigationBarItem
                    }

                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid building up a large backstack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true // Save the state of the popped destination
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}