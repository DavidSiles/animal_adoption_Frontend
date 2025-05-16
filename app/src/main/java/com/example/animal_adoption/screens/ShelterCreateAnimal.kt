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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    var errorMessage by remember { mutableStateOf("") }
    var connectMessage by remember { mutableStateOf(false) }

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

        Text(text = "Create New Animal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = reiacText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    reiacText = newValue
                }
            },
            label = { Text("Reiac") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            errorMessage = ""
            when {
                reiacText.isEmpty() -> errorMessage = "Please enter a valid reiac"
                name.isEmpty() -> errorMessage = "Please enter a name"
                else -> {
                    val reiac = reiacText.toIntOrNull()
                    if (reiac == null) {
                        errorMessage = "Reiac must be a valid number"
                    } else {
                        Log.d("REIAC", "Reiac value: $reiac")
                        remoteShelterViewModel.CreateNewAnimal(reiac, name, shelterId) {
                            navController.navigate("ShelterHome") {
                                popUpTo("ShelterCreateAnimal") { inclusive = true }
                            }
                        }
                        connectMessage = true
                    }
                }
            }
        }) {
            Text(text = "Create Animal")
        }

        when (createNewAnimalMessageUiState) {
            is CreateNewAnimalMessageUiState.Success -> {
                Text(text = "Create animal successful!", color = Color.Green, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
            }
            is CreateNewAnimalMessageUiState.Error -> {
                errorMessage = "Create animal failed. Please check the reiac or name."
            }
            is CreateNewAnimalMessageUiState.Loading -> {
                if (connectMessage) {
                    Text(text = "Connecting...", color = Color.Blue, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}