package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AdoptionRequestDTO
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.widgets.UserBottomBar
import com.example.animal_adoption.viewmodel.AdoptionRequestUiState
import com.example.animal_adoption.viewmodel.AnimalSearchUiState
import com.example.animal_adoption.viewmodel.RemoteAdoptionViewModel

@Composable
fun AdoptionSearchBar(
    navController: NavHostController,
    viewModel: RemoteAdoptionViewModel,
    user: UserDTO? = null,
    shelter: ShelterDTO? = null
) {
    val adoptionUiState by viewModel.adoptionUiState.collectAsState()
    val animalSearchState by viewModel.animalSearchState.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            UserBottomBar(navController = navController, user = user)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.setSearchQuery(it)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar animal...") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filtrar por estado")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        listOf("ALL", "PENDING", "ACCEPTED", "REJECTED").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    expanded = false
                                    viewModel.setStatusFilter(
                                        user = user,
                                        shelter = shelter,
                                        status = if (status == "ALL") null else status
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                when (val state = animalSearchState) {
                    is AnimalSearchUiState.Success -> {
                        if (state.animals.isEmpty()) {
                            Text("No se encontraron animales.")
                        } else {
                            LazyColumn {
                                items(state.animals) { animal ->
                                    AnimalSearchItem(animal)
                                }
                            }
                        }
                    }

                    is AnimalSearchUiState.Error -> {
                        Text("Error buscando animal.")
                    }
                    else -> {}


                }
            } else {
                when (val state = adoptionUiState) {
                    is AdoptionRequestUiState.Success -> {
                        LazyColumn {
                            items(state.requests) { request ->
                                AdoptionRequestItem(request)
                            }
                        }
                    }

                    is AdoptionRequestUiState.Error -> {
                        Text("Error cargando solicitudes.")
                    }

                   else -> {}
                }
            }
        }
    }
}

@Composable
fun AnimalSearchItem(animal: AnimalDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Nombre: ${animal.name}")
            Text("ID: ${animal.id}")
            Text("ID Shelter: ${animal.shelterId}")
        }
    }
}

@Composable
fun AdoptionRequestItem(request: AdoptionRequestDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Animal ID: ${request.animalId}")
            Text("Estado: ${request.status}")
            Text("Refugio ID: ${request.shelterId}")
            Text("Fecha: ${request.requestDate}")
        }
    }
}
