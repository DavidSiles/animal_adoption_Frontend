package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.screens.widgets.FieldValidations
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.CreateNewAnimalMessageUiState
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson

@Composable
fun ShelterCreateAnimal(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel,
    shelter: ShelterDTO?
) {
    val createNewAnimalMessageUiState by remoteShelterViewModel.createNewAnimalMessageUiState.collectAsState()
    val shelterId = shelter?.id
    var reiacText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var reiacError by remember { mutableStateOf<String?>(null) }
    var animalNameError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var connectMessage by remember { mutableStateOf(false) }

    val TuonsBlue = Color(0xFF4285F4)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome Shelter ${shelter?.sheltername ?: "Guest"}")

        Text(text = "Create new animal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = reiacText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                    reiacText = newValue
                }
            },
            label = { Text("Reiac") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
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

        Button(onClick = {
            errorMessage = ""
            when {
                shelterId == null -> errorMessage = "Shelter ID is missing"
                reiacText.isEmpty() -> errorMessage = "Please enter a valid reiac"
                else -> {
                    // Validar reiac
                    val reiac = reiacText.toIntOrNull()
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
                                // Si todas las validaciones pasan, crear el animal
                                Log.d("CreateAnimal", "Attempting to create animal: reiac=$reiac, name=$name, shelterId=$shelterId")
                                remoteShelterViewModel.createNewAnimal(reiac, name, shelterId) {
                                    // Clear form
                                    reiacText = ""
                                    name = ""
                                    errorMessage = ""
                                    connectMessage = false
                                }
                                connectMessage = true
                            }
                        }
                    }
                }
            }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = TuonsBlue,
                contentColor = Color.White
            )
        ) {
            Text(text = "Create Animal")
        }

        when (createNewAnimalMessageUiState) {
            is CreateNewAnimalMessageUiState.Success -> {
                Text(
                    text = "Animal created successfully!",
                    color = Color.Green,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                val shelterJson = Gson().toJson(shelter)
                navController.navigate("ShelterHome/$shelterJson") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            is CreateNewAnimalMessageUiState.Error -> {
                errorMessage = (createNewAnimalMessageUiState as CreateNewAnimalMessageUiState.Error).message
            }
            is CreateNewAnimalMessageUiState.Loading -> {
                if (connectMessage) {
                    Text(
                        text = "Connecting...",
                        color = Color.Blue,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = if (errorMessage == "Animal created successfully!") Color.Green else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}