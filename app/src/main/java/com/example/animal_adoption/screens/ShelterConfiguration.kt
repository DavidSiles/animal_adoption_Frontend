package com.example.animal_adoption.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ConfirmationDialog
import com.example.animal_adoption.screens.widgets.ShelterBottomBar
import com.example.animal_adoption.viewmodel.DeleteShelterMessageUiState
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson

@Composable
fun ShelterConfiguration(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel,
    shelter: ShelterDTO?
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val deleteShelterMessageUiState by remoteShelterViewModel.deleteShelterMessageUiState.collectAsState()

    Scaffold(
        topBar = {
            IconButton(
                modifier = Modifier.padding(5.dp).padding(top = 10.dp),
                onClick = {
                    val shelterJson = Gson().toJson(shelter)
                    navController.navigate("ShelterProfile/$shelterJson") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back to Profile",
                    tint = Color.Black
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Application Version Information
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Versión de la Aplicación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Versión: 1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Fecha de Lanzamiento: 20 de Mayo de 2025",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // Legal Data Information
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Datos Legales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Términos y Condiciones: Consulta nuestros términos en www.animaladoption.com/terms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Política de Privacidad: Lee nuestra política en www.animaladoption.com/privacy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Delete Account Button
                Button(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(
                        text = "Eliminar Cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Handle Delete UI State
                when (deleteShelterMessageUiState) {
                    is DeleteShelterMessageUiState.Success -> {
                        LaunchedEffect(Unit) {
                            navController.navigate("ShelterRegister") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    is DeleteShelterMessageUiState.Error -> {
                        errorMessage = (deleteShelterMessageUiState as DeleteShelterMessageUiState.Error).message
                    }
                    is DeleteShelterMessageUiState.Loading -> {

                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Delete Confirmation Dialog
                if (showDeleteConfirmation) {
                    ConfirmationDialog(
                        title = "Confirmar Eliminación",
                        message = "¿Estás seguro de que quieres eliminar tu cuenta ${shelter?.sheltername}? Esta acción no se puede deshacer.",
                        confirmText = "Eliminar",
                        dismissText = "Cancelar",
                        onConfirm = {
                            showDeleteConfirmation = false
                            shelter?.id?.let { shelterId ->
                                remoteShelterViewModel.deleteShelter(
                                    shelterId = shelterId,
                                    onSuccess = { message ->
                                    },
                                    onFailure = { error ->
                                        errorMessage = error
                                    }
                                )
                            } ?: run {
                                errorMessage = "No shelter ID available"
                            }
                        },
                        onDismiss = { showDeleteConfirmation = false }
                    )
                }
            }
        }
    )
}