package com.example.animal_adoption.screens.widgets


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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

@Composable
fun UserBottomBar(
    navController: NavHostController,
    user: UserDTO?
) {
    val userJson = Gson().toJson(user)
    //val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
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
                IconButton(onClick = { navController.navigate("UserHome/$userJson") }) {
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
                IconButton(onClick = { navController.navigate("UserMessages/$userJson") }) {
                    Icon(Icons.Default.Menu, contentDescription = "Messages")
                }
                Text(
                    text = "Animals",
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
                IconButton(onClick = { navController.navigate("UserProfile/$userJson") }) {
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