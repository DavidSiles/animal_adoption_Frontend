package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.animal_adoption.viewmodel.RemoteAdoptionRequestViewModel
import com.example.animal_adoption.viewmodel.AdoptionRequestUiState
import android.util.Log // Para logs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdoptionRequestsScreen(
    navController: NavController,
    userId: Int,
    adoptionRequestViewModel: RemoteAdoptionRequestViewModel
) {
    val uiState by adoptionRequestViewModel.adoptionRequestUiState.collectAsState()

    LaunchedEffect(userId) {
        Log.d("UserAdoptionRequests", "Cargando solicitudes para usuario ID: $userId")
        adoptionRequestViewModel.getAdoptionRequestsByUserId(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes de Adopción") },
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
                    Text("Error al cargar tus solicitudes de adopción. Inténtalo de nuevo.", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { adoptionRequestViewModel.getAdoptionRequestsByUserId(userId) }) {
                        Text("Reintentar")
                    }
                }
                is AdoptionRequestUiState.Success -> {
                    val requests = (uiState as AdoptionRequestUiState.Success).requests
                    if (requests.isEmpty()) {
                        Text("No has enviado ninguna solicitud de adopción aún.")
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
                                        Text("ID Animal: ${request.animalId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("ID Refugio: ${request.shelterId}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Estado: ${request.status}", style = MaterialTheme.typography.bodyMedium)
                                        Text("Fecha: ${request.requestDate}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
                is AdoptionRequestUiState.OneRequest -> { /* No esperado aquí, pero se maneja */ }
                AdoptionRequestUiState.Idle -> { /* Estado inicial */ }
            }
        }
    }
}