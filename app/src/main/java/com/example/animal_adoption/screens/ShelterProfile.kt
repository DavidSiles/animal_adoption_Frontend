package com.example.animal_adoption.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ShelterBottomBar
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfile(
    navController: NavHostController,
    shelter: ShelterDTO?
) {
    // Disable device back button
    BackHandler(enabled = true) {}

    val TuonsBlue = Color(0xFF4285F4)
    val lightBlueBackground = Color(0xFFBBDEFB)
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { ShelterBottomBar(navController, shelter) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(TuonsBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Shelter Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = shelter?.sheltername ?: "Shelter Name",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CARD: Phone
                    if (!shelter?.phone.isNullOrEmpty()) {
                        InfoCard("Phone Number", shelter.phone, TuonsBlue, lightBlueBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // CARD: Birthday (estático)
                    InfoCard("Birthday", "04/10/2003", TuonsBlue, lightBlueBackground)
                    Spacer(modifier = Modifier.height(8.dp))

                    // CARD: City (estático)
                    InfoCard("City", "Barcelona", TuonsBlue, lightBlueBackground)
                    Spacer(modifier = Modifier.height(8.dp))

                    // CARD: Email
                    if (!shelter?.email.isNullOrEmpty()) {
                        InfoCard("Email", shelter.email, TuonsBlue, lightBlueBackground)
                    }

                    Spacer(modifier = Modifier.weight(0.5f))
                }

                // MENU: Icono superior derecho
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                ) {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-10).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar perfil") },
                            onClick = {
                                Log.d("ShelterProfile", "Editar perfil")
                                showMenu = false
                                val json = Gson().toJson(shelter)
                                val encoded = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                                navController.navigate("ShelterUpdateData/$encoded")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                Log.d("ShelterProfile", "Configuración")
                                showMenu = false
                                navController.navigate("ShelterConfiguration/${Gson().toJson(shelter)}")
                            }
                        )
                        DropdownMenuItem(
                            trailingIcon = {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = TuonsBlue)
                            },
                            text = { Text("Exit", color = TuonsBlue) },
                            onClick = {
                                Log.d("ShelterProfile", "Cerrar sesión")
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

@Composable
fun InfoCard(title: String, content: String, accentColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = accentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
