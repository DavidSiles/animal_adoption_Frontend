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
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.widgets.ConfirmationDialog
import com.example.animal_adoption.viewmodel.DeleteAnimalMessageUiState
import com.example.animal_adoption.viewmodel.DeleteShelterMessageUiState
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
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

    if (animal == null || shelter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading animal or shelter data",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // Log animal details to debug REIAC
    Log.d("ShelterAnimalView", "Animal: ${animal.name}, REIAC: ${animal.reiac}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${animal.name} details") },
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to shelter animals"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Icono y menú desplegable en la esquina superior derecha
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                    ) {
                        IconButton(
                            onClick = { showMenu = !showMenu },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .width(IntrinsicSize.Max)
                                .background(Color.Red),
                            shadowElevation = MenuDefaults.ShadowElevation,
                            offset = DpOffset(
                                x = (-8).dp, // Ajusta hacia la izquierda para alinear al borde derecho
                                y = 8.dp // Espacio vertical para evitar superposición con el TopAppBar
                            )
                        ) {
                            DropdownMenuItem(
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Animal",
                                        tint = Color.Black
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Delete Animal",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
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
            BottomAppBar {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(5.dp),
                    onClick = {
                        val animalJson = Gson().toJson(animal)
                        val shelterJson = Gson().toJson(shelter)
                        val encodedAnimalJson = URLEncoder.encode(animalJson, StandardCharsets.UTF_8.toString())
                        val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("ShelterUpdateAnimal/$encodedAnimalJson/$encodedShelterJson")
                    }
                ) {
                    Text("Update Animal data")
                }
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Name: ${animal.name}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REIAC: ${animal.reiac}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Handle Delete UI State
    when (deleteAnimalMessageUiState) {
        is DeleteAnimalMessageUiState.Success -> {
            LaunchedEffect(Unit) {
                val shelterJson = Gson().toJson(shelter)
                navController.navigate("ShelterListAnimals/$shelterJson") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
        is DeleteAnimalMessageUiState.Error -> {
            errorMessage = (deleteAnimalMessageUiState as DeleteAnimalMessageUiState.Error).message
        }
        is DeleteAnimalMessageUiState.Loading -> {

        }
    }

    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Confirmar Eliminación",
            message = "¿Estás seguro de que quieres eliminar tu animal ${animal.name}? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            onConfirm = {
                showDeleteConfirmation = false
                animal.id.let { animalId ->
                    remoteAnimalViewModel.deleteAnimal(
                        animalId = animal.id,
                        onSuccess = { message ->
                        },
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

