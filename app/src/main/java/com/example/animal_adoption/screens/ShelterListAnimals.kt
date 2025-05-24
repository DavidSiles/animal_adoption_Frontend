package com.example.animal_adoption.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.AnimalCard
import com.example.animal_adoption.screens.widgets.ShelterBottomBar
import com.example.animal_adoption.viewmodel.GetShelterAnimalsListMessageUiState
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterListAnimals(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel,
    shelter: ShelterDTO?
) {
    // Disable device back button
    BackHandler(enabled = true) {}

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
            onSuccess = { animals ->
                Log.d("ShelterListAnimals", "Success: Fetched ${animals?.size ?: 0} animals")
                animals?.forEach { animal ->
                    Log.d("ShelterListAnimals", "Animal: ${animal.name}, REIAC: ${animal.reiac}, REIAC: ${animal.shelterId}")
                }
            },
            onFailure = { errorMessage ->
                Log.e("ShelterListAnimals", "Failure: $errorMessage")
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
                            .padding(20.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(animals.filterNotNull()) { animal ->
                            AnimalCard(animal, shelter, navController)
                        }
                    }
                }
            }
            is GetShelterAnimalsListMessageUiState.Error -> {
                Log.e("ShelterListAnimals", "Error: ${(uiState as GetShelterAnimalsListMessageUiState.Error).message}")
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
                            text = (uiState as GetShelterAnimalsListMessageUiState.Error).message,
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

                        Card(
                            modifier = Modifier
                                .clickable {
                                    val shelterJson = Gson().toJson(shelter)
                                    navController.navigate("ShelterCreateAnimal/$shelterJson")
                                }
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new animal",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add New Animal",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}