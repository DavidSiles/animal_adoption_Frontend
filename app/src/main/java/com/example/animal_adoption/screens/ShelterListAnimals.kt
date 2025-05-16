package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ShelterBottomBar
import com.example.animal_adoption.viewmodel.GetShelterAnimalsListMessageUiState
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterListAnimals(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel,
    shelter: ShelterDTO?
) {
    if (shelter == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No shelter data available")
        }
        return
    }

    val uiState by remoteShelterViewModel.getShelterAnimalsListMessage.collectAsState()

    LaunchedEffect(shelter.id) {
        Log.d("ShelterListAnimals", "Fetching animals for shelter ID: ${shelter.id}")
        remoteShelterViewModel.getShelterAnimals(
            shelterId = shelter.id,
            onSuccess = {
                Log.d("ShelterListAnimals", "Success: Fetched animals")
            },
            onFailure = {
                Log.e("ShelterListAnimals", "Failure: Error fetching animals")
            }
        )
    }

    Scaffold(
        bottomBar = { ShelterBottomBar(navController, shelter) },
    ) { padding ->
        when (uiState) {
            is GetShelterAnimalsListMessageUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GetShelterAnimalsListMessageUiState.Success -> {
                val animals = (uiState as GetShelterAnimalsListMessageUiState.Success).getShelterAnimalsListMessage
                Log.d("ShelterListAnimals", "Animals received: $animals")
                if (animals.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No animals found for this shelter")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(animals.filterNotNull()) { animal ->
                            AnimalCard(animal)
                        }
                    }
                }
            }
            is GetShelterAnimalsListMessageUiState.Error -> {
                Log.e("ShelterListAnimals", "Error state reached")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to load animals",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            remoteShelterViewModel.getShelterAnimals(
                                shelterId = shelter.id,
                                onSuccess = {},
                                onFailure = {}
                            )
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalCard(animal: AnimalDTO) {
    Log.d("AnimalCard", "name: ${animal.name}, reiac: ${animal.reiac}, Sid: ${animal.shelter?.id}")
    Card(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = animal.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "REIAC: ${animal.reiac}")
        }
    }
}