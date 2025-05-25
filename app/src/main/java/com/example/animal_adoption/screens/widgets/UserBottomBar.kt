package com.example.animal_adoption.screens.widgets


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun UserBottomBar(
    navController: NavHostController,
    user: UserDTO?
) {
    val userJson = user?.let {
        URLEncoder.encode(Gson().toJson(it), StandardCharsets.UTF_8.toString())
    }

    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { navController.navigate("UserHome/$userJson") }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelSmall,
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

                IconButton(onClick = { navController.navigate("AdoptionSearchBarUser/$userJson") }) {
                    Icon(Icons.Default.Menu, contentDescription = "SearchBar")
                }
                
                Text(
                    text = "Animals",
                    style = MaterialTheme.typography.labelSmall,
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
                    if (user != null) {
                        Log.d("UserBottomBar", "Navigating to UserAdoptionRequests for userId: ${user.id}")
                        navController.navigate("UserAdoptionRequests/${user.id}") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        Log.e("UserBottomBar", "User is null, cannot navigate to UserAdoptionRequests.")
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
                    .weight(1f)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { navController.navigate("UserProfile/$userJson") }) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}