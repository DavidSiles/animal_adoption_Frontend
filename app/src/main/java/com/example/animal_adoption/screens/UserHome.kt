package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.R

// Data class for dogs with image
data class Dog(val name: String, val age: Int, val description: String, val image: Int)

@Composable
fun UserHome(navController: NavHostController, id: Int?) {
    // Comprobamos si el userId es null
    if (id == null) {
        Log.e("Home", "User ID is null, can't navigate or display user data.")
        return
    }

    // Navegar a la pantalla correcta cuando se entra al HomeScreen
    LaunchedEffect(id) {
        try {
            navController.navigate("Home/$id")
        } catch (e: Exception) {
            Log.e("Login", "Navigation failed: ${e.message}")
        }
    }

    // Sample dog data with images
    var dogs by remember { mutableStateOf(listOf(
        Dog("Roco", 6, "Adopt me", R.drawable.roco),
        Dog("Luna", 3, "Playful pup", R.drawable.luna),
        Dog("Max", 5, "Loves walks", R.drawable.max)
    )) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp)
    ) {
        // Título de bienvenida
        Text(
            text = "Welcome again!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 16.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = Color.Black
            )
        )

        Text(
            text = "Your User ID: $id",
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF666666)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Scrollable list of dogs
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(dogs) { dog ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = dog.image),
                                contentDescription = dog.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                            )
                        }
                        Text(
                            text = dog.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "${dog.age} años",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = dog.description,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate("UserHome/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Home",
                    color = Color(0xFF0095F6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { navController.navigate("Search/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Search",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { navController.navigate("Likes/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Likes",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { navController.navigate("UserProfile/$id") }, // Navigate to UserProfile
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Profile",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}