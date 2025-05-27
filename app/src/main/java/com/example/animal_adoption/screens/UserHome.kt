package com.example.animal_adoption.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.widgets.UserBottomBar
import com.example.animal_adoption.viewmodel.AnimalUiState
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHome(
    navController: NavHostController,
    user: UserDTO?,
    animalViewModel: RemoteAnimalViewModel,
    shelterViewModel: RemoteShelterViewModel
) {
    // Deshabilita el botón de retroceso del dispositivo para que el usuario no pueda salir de la pantalla de inicio
    BackHandler(enabled = true) {}

    val animalUiState by animalViewModel.animalUiState.collectAsState()
    val shelterMap by shelterViewModel.shelterMap.collectAsState(initial = emptyMap<Int, String>())

    val primaryOrange = Color(0xFFFF7043)
    LaunchedEffect(Unit) {
        animalViewModel.getAllAnimals()
        shelterViewModel.loadShelters()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Welcome, ${user?.username ?: "User"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryOrange
                )
            )
        },
        bottomBar = {
            UserBottomBar(navController = navController, user = user)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (animalUiState) {
                is AnimalUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AnimalUiState.Error -> {
                    Text(
                        "Error loading animals. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AnimalUiState.Success -> {
                    val animals = (animalUiState as AnimalUiState.Success).animals
                    if (animals.isEmpty()) {
                        Text(
                            "No animals available for adoption at the moment.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.padding(16.dp)) {
                            items(animals) { animal ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            val animalJson = URLEncoder.encode(Gson().toJson(animal), StandardCharsets.UTF_8.toString())
                                            val userJson = URLEncoder.encode(Gson().toJson(user), StandardCharsets.UTF_8.toString())
                                            navController.navigate("UserAnimalView/$animalJson/$userJson")
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Name: ${animal.name}",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = primaryOrange)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "REIAC: ${animal.reiac}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Shelter: ${shelterMap[animal.shelterId] ?: "Unknown"}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                        )
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