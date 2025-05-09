package com.example.animal_adoption.screens.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.google.gson.Gson


@Composable
fun ShelterBottomBar(navController: NavHostController, shelter: ShelterDTO?) {
    val shelterJson = Gson().toJson(shelter)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("ShelterHome/$shelterJson") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Animals") },
            label = { Text("Animals") },
            selected = false,
            onClick = { /* Navigate to Animals */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Chats") },
            label = { Text("Chats") },
            selected = false,
            onClick = { /* Navigate to Events */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = {
                navController.navigate("ShelterProfile/$shelterJson")}
        )
    }
}