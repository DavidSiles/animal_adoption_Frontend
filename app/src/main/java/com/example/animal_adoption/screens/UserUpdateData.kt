package com.example.animal_adoption.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.viewmodel.RemoteUserViewModel
import com.example.animal_adoption.viewmodel.UpdateUserMessageUiState
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUpdateData(
    navController: NavController,
    remoteUserViewModel: RemoteUserViewModel,
    user: UserDTO?
) {
    val primaryOrange = Color(0xFFFF7043)

    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error: User data not found.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
            ) {
                Text("Go Back", color = Color.White)
            }
        }
        return
    }

    var currentUsername by remember { mutableStateOf(user.username) }
    var newUsername by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val updateUserMessageUiState by remoteUserViewModel.updateUserMessageUiState.collectAsState()
    val updatedUser by remoteUserViewModel.user.collectAsState()

    LaunchedEffect(updateUserMessageUiState) {
        when (updateUserMessageUiState) {
            is UpdateUserMessageUiState.Success -> {
                val newUpdatedUser = (updateUserMessageUiState as UpdateUserMessageUiState.Success).user
                currentUsername = newUpdatedUser.username
                newUsername = ""
                errorMessage = ""

                val userJson = Gson().toJson(newUpdatedUser)
                val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                navController.navigate("UserProfile/$encodedUserJson") {
                    popUpTo("UserProfile/{user}") {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
            is UpdateUserMessageUiState.Error -> {
                errorMessage = (updateUserMessageUiState as UpdateUserMessageUiState.Error).message
            }
            is UpdateUserMessageUiState.Loading -> {
                errorMessage = ""
            }
            else -> { /* El ViewModel podría estar en otro estado inicial */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Update User Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val userToPassBack = updatedUser ?: user
                        val userJson = Gson().toJson(userToPassBack)
                        val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("UserProfile/$encodedUserJson") {
                            popUpTo("UserProfile/{user}") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to user profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryOrange,
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = currentUsername,
                onValueChange = { /* No editable */ },
                label = { Text("Current User Name") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("New User Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    if (newUsername.isNotBlank()) {
                        val updatedUserDTO = UserDTO(
                            id = user.id,
                            username = newUsername,
                            password = user.password ?: ""
                        )
                        remoteUserViewModel.updatedUser(
                            updatedUser = updatedUserDTO,
                            user = user,
                            onSuccess = { /* Handled in LaunchedEffect */ },
                            onFailure = { error ->
                                errorMessage = error
                            }
                        )
                    } else {
                        errorMessage = "New username cannot be empty"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Save Changes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
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