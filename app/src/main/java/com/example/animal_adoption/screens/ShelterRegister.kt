package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
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
import com.example.animal_adoption.screens.widgets.FieldValidations
import com.example.animal_adoption.viewmodel.NetworkModule.WithServiceInitialization
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.example.animal_adoption.viewmodel.ShelterRegisterMessageUiState
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun ShelterRegister(
    navController: NavHostController,
    remoteShelterViewModel: RemoteShelterViewModel
) {
    val registerMessageUiState by remoteShelterViewModel.registerMessageUiState.collectAsState()
    var sheltername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var shelternameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var serverError by remember { mutableStateOf<String?>(null) }
    var connectMessage by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp), // Reduced top padding
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
            .padding(horizontal = 24.dp, vertical = 10.dp), // Reduced padding
        verticalArrangement = Arrangement.spacedBy(5.dp), // Tighter spacing between elements
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logotuons2),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp) // Slightly smaller logo
                .padding(bottom = 5.dp), // Reduced bottom padding
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Create Shelter Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = sheltername,
            onValueChange = { newValue ->
                sheltername = newValue
                shelternameError = FieldValidations.validateName(newValue)
            },
            label = { Text("Shelter Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            isError = shelternameError != null,
            supportingText = {
                shelternameError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordError = FieldValidations.validatePassword(newValue)
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            isError = passwordError != null,
            supportingText = {
                passwordError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
                emailError = FieldValidations.validateEmail(newValue)
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                phone = newValue
                phoneError = FieldValidations.validatePhone(newValue)
            },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            isError = phoneError != null,
            supportingText = {
                phoneError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        Button(
            onClick = {
                shelternameError = FieldValidations.validateName(sheltername)
                passwordError = FieldValidations.validatePassword(password)
                emailError = FieldValidations.validateEmail(email)
                phoneError = FieldValidations.validatePhone(phone)
                serverError = null

                if (shelternameError == null && passwordError == null && emailError == null && phoneError == null) {
                    val finalEmail = if (email.isEmpty()) "" else email
                    val finalPhone = if (phone.isEmpty()) "" else phone

                    remoteShelterViewModel.shelterRegister(
                        sheltername,
                        password,
                        finalEmail,
                        finalPhone
                    ) { shelter ->
                        val shelterJson = Gson().toJson(shelter)
                        navController.navigate("ShelterHome/$shelterJson")
                    }
                    connectMessage = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4),
                contentColor = Color.White
            )
        ) {
            Text("Register", fontSize = 16.sp)
        }

        when (registerMessageUiState) {
            is ShelterRegisterMessageUiState.Success -> {
                Text(
                    text = "Registration successful!",
                    color = Color(0xFF2ECC71),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            is ShelterRegisterMessageUiState.Error -> {
                serverError =
                    (registerMessageUiState as ShelterRegisterMessageUiState.Error).message
            }

            is ShelterRegisterMessageUiState.Loading -> {
                if (connectMessage) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color(0xFF4285F4)
                    )
                }
            }
        }

        serverError?.let {
            Text(
                text = it,
                color = Color(0xFFE74C3C),
                fontSize = 12.sp, // Smaller font
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp), // Reduced padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = Color(0xFF666666))
            TextButton(
                onClick = { navController.navigate("ShelterLogin") }
            ) {
                Text(
                    text = "Sign In",
                    color = Color(0xFF4285F4),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}