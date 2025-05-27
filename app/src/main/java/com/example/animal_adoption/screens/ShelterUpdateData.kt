package com.example.animal_adoption.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.transform.CircleCropTransformation
import com.example.animal_adoption.screens.widgets.FieldValidations
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
    val TuonsBlue = Color(0xFF4285F4)

    if (shelter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error loading shelter data",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = TuonsBlue)
            ) {
                Text("Go Back", color = Color.White)
            }
        }
        return
    }

    var sheltername by remember { mutableStateOf(shelter.sheltername) }
    var email by remember { mutableStateOf(shelter.email ?: "") }
    var phone by remember { mutableStateOf(shelter.phone ?: "") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var shelternameError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val updateShelterMessageUiState by remoteShelterViewModel.updateShelterMessageUiState.collectAsState()
    val updatedShelter by remoteShelterViewModel.shelter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Update Shelter Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val shelterJson = Gson().toJson(updatedShelter ?: shelter)
                        val encoded = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("ShelterProfile/$encoded") {
                            popUpTo("ShelterProfile/{shelter}") { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TuonsBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = sheltername,
                onValueChange = { sheltername = it },
                label = { Text("Shelter Name") },
                isError = shelternameError != null,
                supportingText = {
                    shelternameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = FieldValidations.validateEmail(email)
                },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = FieldValidations.validatePhone(it)
                },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = phoneError != null,
                supportingText = {
                    phoneError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    shelternameError = FieldValidations.validateName(sheltername)
                    emailError = FieldValidations.validateEmail(email)
                    phoneError = FieldValidations.validatePhone(phone)

                    if (shelternameError == null && emailError == null && phoneError == null) {
                        val updatedShelterDTO = ShelterDTO(
                            id = shelter.id,
                            sheltername = sheltername,
                            password = "",
                            email = email,
                            phone = phone
                        )

                        remoteShelterViewModel.updateShelter(
                            updatedShelterDTO,
                            shelter,
                            onSuccess = { updated ->
                                sheltername = updated.sheltername
                                val shelterJson = Gson().toJson(updated)
                                val encoded = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("ShelterProfile/$encoded") {
                                    popUpTo("ShelterProfile/{shelter}") { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onFailure = { error ->
                                errorMessage = error
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TuonsBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Save Changes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
