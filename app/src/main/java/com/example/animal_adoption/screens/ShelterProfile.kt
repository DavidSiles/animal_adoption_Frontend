package com.example.animal_adoption.screens

import android.R
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ShelterBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterProfile(
    navController: NavHostController,
    shelter: ShelterDTO?
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar perfil") },
                            onClick = {
                                Log.d("ShelterProfile", "Opción seleccionada: Editar perfil")
                                showMenu = false
                                // TODO: Añadir acción, e.g., navController.navigate("ShelterEditProfile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                Log.d("ShelterProfile", "Opción seleccionada: Configuración")
                                showMenu = false
                                // TODO: Añadir acción, e.g., navController.navigate("ShelterSettings")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(
                                text = "Exit",
                                color = Color.Red
                            ) },
                            onClick = {
                                Log.d("ShelterProfile", "Opción seleccionada: Cerrar sesión")
                                showMenu = false
                                // TODO: Añadir acción, e.g., limpiar sesión y navegar a ShelterLogin
                                navController.navigate("ShelterLogin") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Exit",
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { ShelterBottomBar(navController, shelter) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // Reducido para ajustarse al topBar
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Shelter Logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = shelter?.sheltername ?: "Shelter Name",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Shelter ID: ${shelter?.id ?: "N/A"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "About Us",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This shelter is dedicated to providing care and finding homes for animals in need.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}