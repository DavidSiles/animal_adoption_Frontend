package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.viewmodel.LoginMessageUiState
import com.example.animal_adoption.viewmodel.RemoteUserViewModel

@Composable
fun UserRegister(
    navController: NavHostController,
    remoteViewModel: RemoteUserViewModel
) {
    val loginMessageUiState by remoteViewModel.loginMessageUiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var connectMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Register", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username") },
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
            remoteViewModel.register(username, password) { id ->
                navController.navigate("UserHome/$id")
            }
            connectMessage = true
        }) {
            Text(text = "Register")
        }

        when (loginMessageUiState) {
            is LoginMessageUiState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate("UserHome")
                }
            }
            is LoginMessageUiState.Error -> {
                errorMessage = "Register failed. Please check your username or password."
            }
            is LoginMessageUiState.Loading -> {
                if (connectMessage) {
                    Text(text = "Connecting...", color = Color.Blue, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Don't have an account yet? Click below to register")

        Spacer(modifier = Modifier.height(10.dp))

        /*
        Button(onClick = { navController.navigate("registerScreen") }) {
            Text(text = stringResource(id = R.string.registerButton))
        }
        */
    }
}