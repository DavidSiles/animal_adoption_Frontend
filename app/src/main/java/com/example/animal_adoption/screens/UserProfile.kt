package com.example.animal_adoption.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun UserProfile(navController: NavHostController, id: Int?) {
    // Sample user data
    val userName = "Dayanna19"
    val userBio = "Animal lover 🐾 | Adopting and fostering pets"
    val userStats = mapOf("Adoptions" to 1, "Likes" to 12)
    val userInfo = mapOf(
        "Username" to userName,
        "Password" to "********" // Placeholder for security
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp)
    ) {
        // Profile header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

        }

        // User name and bio
        Text(
            text = userName,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "User ID: $id",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = userBio,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // User information card
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
                Text(
                    text = "User Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                userInfo.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = value,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.End
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate("UserHome/$id") }, // Updated to match MainActivity.kt route
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(
                    text = "Home",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { navController.navigate("Search/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 5.dp)
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
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(
                    text = "Likes",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = { navController.navigate("UserProfile/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(
                    text = "Me",
                    color = Color(0xFF0095F6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}