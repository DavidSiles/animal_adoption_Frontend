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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.AnimalCard // Assuming AnimalCard is already styled
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

    val TuonsBlue = Color(0xFF4285F4)

    if (shelter == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Error: Datos del refugio no disponibles.",
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center
            )
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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis Animales",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TuonsBlue.copy(alpha = 0.85f),
                    titleContentColor = Color.White
                )
            )
        },
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
                    CircularProgressIndicator(color = TuonsBlue)
                }
            }
            is GetShelterAnimalsListMessageUiState.Success -> {
                val animals = (uiState as GetShelterAnimalsListMessageUiState.Success).getShelterAnimalsListMessage
                if (animals.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aún no tienes animales registrados.",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Default,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // "Add New Animal" card, styled like ShelterHome
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(180.dp)
                                    .clickable {
                                        val shelterJson = Gson().toJson(shelter)
                                        navController.navigate("ShelterCreateAnimal/$shelterJson")
                                    }
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize().padding(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(TuonsBlue)
                                            .border(2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add new animal",
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Add new animal",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        ),
                                        fontFamily = FontFamily.Default,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Error: ${(uiState as GetShelterAnimalsListMessageUiState.Error).message}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Default,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                remoteShelterViewModel.getShelterAnimals(
                                    shelterId = shelter.id,
                                    onSuccess = {},
                                    onFailure = {}
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TuonsBlue,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("Retry", fontSize = 18.sp, fontFamily = FontFamily.Default)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(180.dp)
                                .clickable {
                                    val shelterJson = Gson().toJson(shelter)
                                    navController.navigate("ShelterCreateAnimal/$shelterJson")
                                }
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(TuonsBlue)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new animal",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Add new animal",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    fontFamily = FontFamily.Default,
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
