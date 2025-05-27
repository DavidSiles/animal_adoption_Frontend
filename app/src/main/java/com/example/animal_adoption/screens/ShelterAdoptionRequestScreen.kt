package com.example.animal_adoption.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.animal_adoption.viewmodel.RemoteAdoptionRequestViewModel
import com.example.animal_adoption.viewmodel.AdoptionRequestUiState
import com.example.animal_adoption.viewmodel.UpdateAdoptionRequestStatusUiState
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ShelterBottomBar

val TuonsBlue = Color(0xFF4285F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterAdoptionRequestsScreen(
    navController: NavHostController,
    shelter: ShelterDTO,
    adoptionRequestViewModel: RemoteAdoptionRequestViewModel
) {
    // Disable device back button
    BackHandler(enabled = true) {}

    val context = LocalContext.current
    val uiState by adoptionRequestViewModel.adoptionRequestUiState.collectAsState()
    val updateStatusState by adoptionRequestViewModel.updateAdoptionRequestStatusUiState.collectAsState()

    LaunchedEffect(shelter.id, updateStatusState) {
        if (updateStatusState is UpdateAdoptionRequestStatusUiState.Success ||
            updateStatusState is UpdateAdoptionRequestStatusUiState.Error ||
            updateStatusState is UpdateAdoptionRequestStatusUiState.Idle
        ) {
            Log.d("ShelterAdoptionRequests", "Recargando solicitudes para refugio ID: ${shelter.id}")
            adoptionRequestViewModel.getAdoptionRequestsByShelterId(shelter.id)
        }
    }

    LaunchedEffect(updateStatusState) {
        when (updateStatusState) {
            is UpdateAdoptionRequestStatusUiState.Success -> {
                Toast.makeText(context, "Estado de solicitud actualizado con éxito.", Toast.LENGTH_SHORT).show()
                adoptionRequestViewModel.resetUpdateAdoptionRequestStatusUiState()
            }
            is UpdateAdoptionRequestStatusUiState.Error -> {
                val message = (updateStatusState as UpdateAdoptionRequestStatusUiState.Error).message
                Toast.makeText(context, "Error al actualizar estado: $message", Toast.LENGTH_LONG).show()
                adoptionRequestViewModel.resetUpdateAdoptionRequestStatusUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = { ShelterBottomBar(navController, shelter) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (uiState) {
                is AdoptionRequestUiState.Loading -> {
                    CircularProgressIndicator(color = TuonsBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cargando solicitudes...")
                }
                is AdoptionRequestUiState.Error -> {
                    Text(
                        "Error al cargar las solicitudes de adopción del refugio. Inténtalo de nuevo.",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { adoptionRequestViewModel.getAdoptionRequestsByShelterId(shelter.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = TuonsBlue)
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
                is AdoptionRequestUiState.Success -> {
                    val requests = (uiState as AdoptionRequestUiState.Success).requests
                    if (requests.isEmpty()) {
                        Text("No hay solicitudes de adopción para este refugio.")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(requests) { request ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Solicitud #${request.id}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = TuonsBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Usuario: ${request.userId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Animal: ${request.animalId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Estado: ${request.status}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Fecha: ${request.requestDate}", style = MaterialTheme.typography.bodySmall)

                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (request.status == "PENDING") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Button(
                                                    onClick = {
                                                        adoptionRequestViewModel.updateAdoptionRequestStatus(request.id, "ACCEPTED")
                                                    },
                                                    enabled = updateStatusState !is UpdateAdoptionRequestStatusUiState.Loading,
                                                    colors = ButtonDefaults.buttonColors(containerColor = TuonsBlue)
                                                ) {
                                                    if (updateStatusState is UpdateAdoptionRequestStatusUiState.Loading) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(20.dp),
                                                            color = Color.White
                                                        )
                                                    } else {
                                                        Text("Aceptar", color = Color.White)
                                                    }
                                                }
                                                Button(
                                                    onClick = {
                                                        adoptionRequestViewModel.updateAdoptionRequestStatus(request.id, "REJECTED")
                                                    },
                                                    enabled = updateStatusState !is UpdateAdoptionRequestStatusUiState.Loading,
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                                ) {
                                                    if (updateStatusState is UpdateAdoptionRequestStatusUiState.Loading) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(20.dp),
                                                            color = Color.White
                                                        )
                                                    } else {
                                                        Text("Rechazar", color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
