package com.example.animal_adoption.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.widgets.UserBottomBar
import com.example.animal_adoption.viewmodel.AnimalUiState
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHome(
    navController: NavHostController,
    user: UserDTO?,
    animalViewModel: RemoteAnimalViewModel = viewModel(),
    shelterViewModel: RemoteShelterViewModel = viewModel()
) {
    val animalUiState by animalViewModel.animalUiState.collectAsState()
    val shelterMap by shelterViewModel.shelterMap.collectAsState(initial = emptyMap<Int, String>())

    LaunchedEffect(Unit) {
        animalViewModel.getAllAnimals()
        shelterViewModel.loadShelters()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome!") })
        },
        bottomBar = {
            UserBottomBar(navController = navController, user = user)
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            when (animalUiState) {
                is AnimalUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is AnimalUiState.Error -> {
                    Text("Error loading animals", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }

                is AnimalUiState.Success -> {
                    val animals = (animalUiState as AnimalUiState.Success).animals
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(animals) { animal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        // Navegar al detalle si tienes esa pantalla
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${animal.name}")
                                    Text("REIAC: ${animal.reiac}")
                                    Text("Refugio: ${shelterMap[animal.shelterId] ?: "Desconocido"}")
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
