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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.animal_adoption.viewmodel.RemoteAdoptionRequestViewModel
import com.example.animal_adoption.viewmodel.AdoptionRequestUiState
import com.example.animal_adoption.viewmodel.UpdateAdoptionRequestStatusUiState
import android.util.Log // Para logs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterAdoptionRequestsScreen(
    navController: NavController,
    shelterId: Int,
    adoptionRequestViewModel: RemoteAdoptionRequestViewModel
) {
    val context = LocalContext.current
    val uiState by adoptionRequestViewModel.adoptionRequestUiState.collectAsState()
    val updateStatusState by adoptionRequestViewModel.updateAdoptionRequestStatusUiState.collectAsState()

    LaunchedEffect(shelterId, updateStatusState) {
        if (updateStatusState is UpdateAdoptionRequestStatusUiState.Success ||
            updateStatusState is UpdateAdoptionRequestStatusUiState.Error ||
            updateStatusState is UpdateAdoptionRequestStatusUiState.Idle) {
            Log.d("ShelterAdoptionRequests", "Recargando solicitudes para refugio ID: $shelterId")
            adoptionRequestViewModel.getAdoptionRequestsByShelterId(shelterId)
        }
    }

    LaunchedEffect(updateStatusState) {
        when (updateStatusState) {
            is UpdateAdoptionRequestStatusUiState.Success -> {
                Toast.makeText(context, "Estado de solicitud actualizado con éxito.", Toast.LENGTH_SHORT).show()
                adoptionRequestViewModel.resetUpdateAdoptionRequestStatusUiState() // Resetear el estado
            }
            is UpdateAdoptionRequestStatusUiState.Error -> {
                val message = (updateStatusState as UpdateAdoptionRequestStatusUiState.Error).message
                Toast.makeText(context, "Error al actualizar estado: $message", Toast.LENGTH_LONG).show()
                adoptionRequestViewModel.resetUpdateAdoptionRequestStatusUiState() // Resetear el estado
            }
            UpdateAdoptionRequestStatusUiState.Loading -> { /* Se gestiona en el botón */ }
            UpdateAdoptionRequestStatusUiState.Idle -> { /* Estado inicial */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de Adopción (Refugio)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is AdoptionRequestUiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Cargando solicitudes...")
                }
                is AdoptionRequestUiState.Error -> {
                    Text("Error al cargar las solicitudes de adopción del refugio. Inténtalo de nuevo.", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { adoptionRequestViewModel.getAdoptionRequestsByShelterId(shelterId) }) {
                        Text("Reintentar")
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
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("ID Solicitud: ${request.id}", style = MaterialTheme.typography.titleMedium)
                                        Text("ID Usuario: ${request.userId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("ID Animal: ${request.animalId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Estado: ${request.status}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Fecha: ${request.requestDate}", style = MaterialTheme.typography.bodySmall)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Opciones para cambiar el estado si está PENDIENTE
                                        if (request.status == "PENDING") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                Button(
                                                    onClick = {
                                                        adoptionRequestViewModel.updateAdoptionRequestStatus(request.id, "ACCEPTED")
                                                    },
                                                    enabled = updateStatusState !is UpdateAdoptionRequestStatusUiState.Loading // Deshabilitar si se está actualizando
                                                ) {
                                                    if (updateStatusState is UpdateAdoptionRequestStatusUiState.Loading) {
                                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                                    } else {
                                                        Text("Aceptar")
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
                                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                                    } else {
                                                        Text("Rechazar")
                                                    }
                                                }
                                            }
                                        } else {
                                            Text("Estado: ${request.status}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is AdoptionRequestUiState.OneRequest -> { /* No esperado aquí */ }
                AdoptionRequestUiState.Idle -> { /* Estado inicial */ }
            }
        }
    }
}