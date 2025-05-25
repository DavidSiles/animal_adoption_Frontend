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
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson


@Composable
fun ShelterBottomBar(
    navController: NavHostController,
    shelter: ShelterDTO?
) {
    val shelterJson = Gson().toJson(shelter)

    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { navController.navigate("ShelterHome/$shelterJson") }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                Text(
                    text = "Home",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { navController.navigate("ShelterListAnimals/$shelterJson") }) {
                    Icon(Icons.Default.Menu, contentDescription = "Animals")
                }
                Text(
                    text = "Animals",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    if (shelter != null) {
                        Log.d("ShelterBottomBar", "Navigating to ShelterAdoptionRequests for userId: ${shelter.id}")
                        navController.navigate("ShelterAdoptionRequests/${shelter.id}") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        Log.e("ShelterBottomBar", "Shelter is null, cannot navigate to ShelterAdoptionRequests.")
                    }
                }) {
                    Icon(Icons.Default.ListAlt, contentDescription = "My Requests") // Icono para solicitudes
                }
                Text(
                    text = "My Requests",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { navController.navigate("ShelterProfile/$shelterJson") }) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
                Text(
                    text = "Profile",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}