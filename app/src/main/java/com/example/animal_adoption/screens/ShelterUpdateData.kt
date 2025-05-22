package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.transform.CircleCropTransformation
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.example.animal_adoption.viewmodel.UpdateShelterMessageUiState
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterUpdateData(
    navController: NavController,
    remoteShelterViewModel: RemoteShelterViewModel,
    shelter: ShelterDTO?
) {
    // Handle null shelter data
    if (shelter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading shelter data",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // State for editable fields
    var sheltername by remember { mutableStateOf(shelter.sheltername) }
    var errorMessage by remember { mutableStateOf("") }
    val updateShelterMessageUiState by remoteShelterViewModel.updateShelterMessageUiState.collectAsState()
    val updatedShelter by remoteShelterViewModel.shelter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update ${updatedShelter?.sheltername ?: shelter.sheltername} details") },
                navigationIcon = {
                    IconButton(onClick = {
                        val shelterJson = Gson().toJson(updatedShelter ?: shelter)
                        val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("ShelterProfile/$encodedShelterJson") {
                            popUpTo("ShelterProfile/{shelter}") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to shelter profile"
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
                value = sheltername,
                onValueChange = { sheltername = it },
                label = { Text("Shelter Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Update button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(5.dp),
                onClick = {
                    val updatedShelterDTO = ShelterDTO(
                        id = shelter.id,
                        sheltername = sheltername,
                        password = shelter.password ?: "" // Preserve existing password
                    )
                    remoteShelterViewModel.updateShelter(
                        updatedShelter = updatedShelterDTO,
                        shelter = shelter,
                        onSuccess = { newUpdatedShelter ->
                            sheltername = newUpdatedShelter.sheltername // Update local state
                            val shelterJson = Gson().toJson(newUpdatedShelter)
                            val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("ShelterProfile/$encodedShelterJson") {
                                popUpTo("ShelterProfile/{shelter}") {
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
    when (updateShelterMessageUiState) {
        is UpdateShelterMessageUiState.Success -> {
            LaunchedEffect(updateShelterMessageUiState) {
                val newUpdatedShelter = (updateShelterMessageUiState as UpdateShelterMessageUiState.Success).shelter
                sheltername = newUpdatedShelter.sheltername // Update local state
                val shelterJson = Gson().toJson(newUpdatedShelter)
                val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                navController.navigate("ShelterProfile/$encodedShelterJson") {
                    popUpTo("ShelterProfile/{shelter}") {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        is UpdateShelterMessageUiState.Error -> {
            errorMessage = (updateShelterMessageUiState as UpdateShelterMessageUiState.Error).message
        }
        is UpdateShelterMessageUiState.Loading -> {
            // Optionally show a loading indicator
        }
    }
}