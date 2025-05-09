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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.example.animal_adoption.viewmodel.ShelterLoginMessageUiState


@Composable
fun ShelterRegister(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel
) {
    val loginMessageUiState by remoteShelterViewModel.loginMessageUiState.collectAsState()
    var sheltername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                imageVector = Icons.Default.ArrowBack,
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
        Text(text = "Welcome to Shelter Register", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = sheltername,
            onValueChange = { sheltername = it },
            label = { Text(text = "Sheltername") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            errorMessage = ""
            remoteShelterViewModel.ShelterRegister(sheltername, password) { shelter ->
                Log.e("Navigating to ShelterHome with ID", "Navigating to ShelterHome with ID: $shelter.id")
                println("Navigating to ShelterHome with ID: $shelter.id")
                navController.navigate("ShelterHome/$shelter")
            }
            connectMessage = true
        }) {
            Text(text = "Register")
        }

        when (loginMessageUiState) {
            is ShelterLoginMessageUiState.Success -> {
                Text(text = "Registration successful!", color = Color.Green, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
            }
            is ShelterLoginMessageUiState.Error -> {
                errorMessage = "Register failed. Please check your username or password."
            }
            is ShelterLoginMessageUiState.Loading -> {
                if (connectMessage) {
                    Text(text = "Connecting...", color = Color.Blue, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "If you have an account click below to Login")

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { navController.navigate("ShelterLogin") }) {
            Text(text = "Go to Login")
        }
    }
}