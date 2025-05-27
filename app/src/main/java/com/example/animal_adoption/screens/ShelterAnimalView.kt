package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ConfirmationDialog
import com.example.animal_adoption.viewmodel.DeleteAnimalMessageUiState
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterAnimalView(
    navController: NavController,
    remoteAnimalViewModel: RemoteAnimalViewModel,
    animal: AnimalDTO?,
    shelter: ShelterDTO?
) {

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val deleteAnimalMessageUiState by remoteAnimalViewModel.deleteAnimalMessageUiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }


    val TuonsBlue = Color(0xFF4285F4)

    if (animal == null || shelter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error al cargar los datos del animal o refugio.",
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center
            )
        }
        return
    }


    LaunchedEffect(deleteAnimalMessageUiState) {
        when (deleteAnimalMessageUiState) {
            is DeleteAnimalMessageUiState.Success -> {
                isLoading = false
                errorMessage = ""
                val shelterJson = Gson().toJson(shelter)
                val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                navController.navigate("ShelterListAnimals/$encodedShelterJson") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
            is DeleteAnimalMessageUiState.Error -> {
                isLoading = false
                errorMessage = (deleteAnimalMessageUiState as DeleteAnimalMessageUiState.Error).message
            }
            is DeleteAnimalMessageUiState.Loading -> {
                isLoading = true
                errorMessage = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${animal.name} - Detalles",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val shelterJson = Gson().toJson(shelter)
                        val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("ShelterListAnimals/$encodedShelterJson") {
                            popUpTo("ShelterListAnimals/{shelter}") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la lista de animales",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TuonsBlue.copy(alpha = 0.85f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { showMenu = !showMenu },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .width(IntrinsicSize.Max)
                                .background(MaterialTheme.colorScheme.surface),
                            shadowElevation = MenuDefaults.ShadowElevation,
                            offset = DpOffset(
                                x = (-8).dp,
                                y = 8.dp
                            )
                        ) {
                            DropdownMenuItem(
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar Animal",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Eliminar Animal",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Default
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirmation = true
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TuonsBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                onClick = {
                    val animalJson = Gson().toJson(animal)
                    val shelterJson = Gson().toJson(shelter)
                    val encodedAnimalJson = URLEncoder.encode(animalJson, StandardCharsets.UTF_8.toString())
                    val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("ShelterUpdateAnimal/$encodedAnimalJson/$encodedShelterJson")
                }
            ) {
                Text("Actualizar Datos del Animal", fontSize = 18.sp, fontFamily = FontFamily.Default)
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nombre: ${animal.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REIAC: ${animal.reiac}",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Confirmar Eliminación",
            message = "¿Estás seguro de que quieres eliminar al animal ${animal.name}? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            onConfirm = {
                showDeleteConfirmation = false
                animal.id.let { animalId ->
                    remoteAnimalViewModel.deleteAnimal(
                        animalId = animal.id,
                        onSuccess = { /* Handled by LaunchedEffect */ },
                        onFailure = { error ->
                            errorMessage = error
                        }
                    )
                }
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}
