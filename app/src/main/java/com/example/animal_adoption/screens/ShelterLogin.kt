package com.example.animal_adoption.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.R
import com.example.animal_adoption.viewmodel.NetworkModule.WithServiceInitialization
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.example.animal_adoption.viewmodel.ShelterLoginMessageUiState
import com.google.gson.Gson

@Composable
fun ShelterLogin(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel
) {

    val shelterLoginMessageUiState by remoteShelterViewModel.loginMessageUiState.collectAsState()
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
        IconButton(onClick = {
            navController.navigate("FirstScreen") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to home"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logotuons2),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Shelter Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = sheltername,
            onValueChange = { sheltername = it },
            label = { Text("Shelter Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                errorMessage = ""
                when {
                    sheltername.isEmpty() -> errorMessage = "Please enter a shelter name"
                    password.isEmpty() -> errorMessage = "Please enter a password"
                    else -> {
                        remoteShelterViewModel.shelterLogin(sheltername, password) { shelter ->
                            val shelterJson = Gson().toJson(shelter)
                            navController.navigate("ShelterHome/$shelterJson")
                        }
                        connectMessage = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4),
                contentColor = Color.White
            )
        ) {
            Text("Login", fontSize = 18.sp)
        }

        when (shelterLoginMessageUiState) {
            is ShelterLoginMessageUiState.Success -> {
                Text(
                    text = "Login successful!",
                    color = Color(0xFF2ECC71),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            is ShelterLoginMessageUiState.Error -> {
                errorMessage = (shelterLoginMessageUiState as ShelterLoginMessageUiState.Error).message
            }
            is ShelterLoginMessageUiState.Loading -> {
                if (connectMessage) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 16.dp),
                        color = Color(0xFF4285F4)
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFE74C3C),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account? ", color = Color(0xFF666666))
            TextButton(
                onClick = { navController.navigate("ShelterRegister") }
            ) {
                Text(
                    text = "Register",
                    color = Color(0xFF4285F4),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}