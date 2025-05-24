package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.FieldValidations
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.example.animal_adoption.viewmodel.UpdateAnimalMessageUiState
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterUpdateAnimal(
    navController: NavController,
    remoteAnimalViewModel: RemoteAnimalViewModel,
    animal: AnimalDTO?,
    shelter: ShelterDTO?
) {
    // Handle null animal or shelter data
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

    // State for editable fields
    var name by remember { mutableStateOf(animal.name) }
    var reiac by remember { mutableStateOf(animal.reiac.toString()) }
    var reiacError by remember { mutableStateOf<String?>(null) }
    var animalNameError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val updateAnimalMessageUiState by remoteAnimalViewModel.updateAnimalMessageUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update ${animal.name} Details") },
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
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Animal Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                isError = animalNameError != null,
                supportingText = {
                    animalNameError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // REIAC field
            OutlinedTextField(
                value = reiac,
                onValueChange = { reiac = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("REIAC") },
                modifier = Modifier.fillMaxWidth()
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = reiacError != null,
                supportingText = {
                    reiacError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Update button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(5.dp),
                onClick = {
                    errorMessage = ""
                    when {
                        reiac.isEmpty() -> errorMessage = "Please enter a valid reiac"
                        else -> {
                            // Validar reiac
                            val reiac = reiac.toIntOrNull()
                            if (reiac == null) {
                                errorMessage = "Reiac must be a valid number"
                            } else {
                                val reiacError = FieldValidations.validateReiac(reiac)
                                if (reiacError != null) {
                                    errorMessage = reiacError
                                } else {
                                    // Validar animalName
                                    val nameError = FieldValidations.validateAnimalName(name)
                                    if (nameError != null) {
                                        errorMessage = nameError
                                    } else {
                                        val updatedAnimal = AnimalDTO(
                                            id = animal.id,
                                            name = name,
                                            reiac = reiac,
                                            shelterId = shelter.id
                                        )
                                        remoteAnimalViewModel.updateAnimal(
                                            updatedAnimal = updatedAnimal,
                                            onSuccess = { updatedAnimal ->
                                                // Handle success (e.g., navigate back)
                                                val shelterJson = Gson().toJson(shelter)
                                                val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                                                navController.navigate("ShelterListAnimals/$encodedShelterJson") {
                                                    popUpTo("ShelterListAnimals/{shelter}") {
                                                        inclusive = false
                                                    }
                                                    launchSingleTop = true
                                                }
                                            },
                                            onFailure = { error ->
                                                errorMessage = error
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            ) {
                Text("Save Changes")
            }

            // Error message display
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Handle Update UI State
    when (updateAnimalMessageUiState) {
        is UpdateAnimalMessageUiState.Success -> {
            LaunchedEffect(Unit) {
                val shelterJson = Gson().toJson(shelter)
                val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                navController.navigate("ShelterListAnimals/$encodedShelterJson") {
                    popUpTo("ShelterListAnimals/{shelter}") {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        is UpdateAnimalMessageUiState.Error -> {
            errorMessage = "Failed to update animal"
        }
        is UpdateAnimalMessageUiState.Loading -> {
            // Optionally show a loading indicator
        }
    }
}