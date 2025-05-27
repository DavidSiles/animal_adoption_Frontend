package com.example.animal_adoption.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.widgets.InfoCard
import com.example.animal_adoption.screens.widgets.UserBottomBar
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    navController: NavHostController,
    user: UserDTO?
) {
    // Disable device back button
    BackHandler(enabled = true) {}

    // Define orange color scheme
    val TuonsOrange = Color(0xFFFF7043) // Primary orange color
    val lightOrangeBackground = Color(0xFFFFF3E0) // Light orange for cards

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { UserBottomBar(navController, user) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()) // Add scroll for responsiveness
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Match ShelterProfile height
                            .background(TuonsOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)) // Match ShelterProfile styling
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "User Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp) // Match ShelterProfile icon size
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = user?.username ?: "Username",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Match ShelterProfile spacing

                    // CARD: Birthday (static)
                    InfoCard("Birthday", "21/10/2003", TuonsOrange, lightOrangeBackground)
                    Spacer(modifier = Modifier.height(8.dp))

                    // CARD: City (static)
                    InfoCard("City", "Barcelona", TuonsOrange, lightOrangeBackground)
                    Spacer(modifier = Modifier.height(8.dp))


                    Spacer(modifier = Modifier.weight(0.5f)) // Match ShelterProfile's bottom spacing
                }

                // Dropdown menu in top-right corner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp) // Match ShelterProfile padding
                ) {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.White // Match ShelterProfile
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-10).dp, y = 0.dp), // Match ShelterProfile offset
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Match ShelterProfile styling
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar perfil") },
                            onClick = {
                                Log.d("UserProfile", "Editar perfil")
                                showMenu = false
                                val userJson = Gson().toJson(user)
                                val encoded = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("UserUpdateData/$encoded")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                Log.d("UserProfile", "Configuración")
                                showMenu = false
                                navController.navigate("UserConfiguration/${Gson().toJson(user)}")
                            }
                        )
                        DropdownMenuItem(
                            trailingIcon = {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = TuonsOrange) // Use orange
                            },
                            text = { Text("Exit", color = TuonsOrange) }, // Use orange
                            onClick = {
                                Log.d("UserProfile", "Cerrar sesión")
                                showMenu = false
                                navController.navigate("FirstScreen") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
