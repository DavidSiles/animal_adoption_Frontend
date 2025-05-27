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
    // Handle null user data
    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading user data",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // State for editable fields
    var currentUsername by remember { mutableStateOf(user.username) }
    var newUsername by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val updateUserMessageUiState by remoteUserViewModel.updateUserMessageUiState.collectAsState()
    val updatedUser by remoteUserViewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update ${updatedUser?.username ?: user.username} details") },
                navigationIcon = {
                    IconButton(onClick = {
                        val userJson = Gson().toJson(updatedUser ?: user)
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
                            contentDescription = "Back to user profile"
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

            // Current Username field (read-only)
            OutlinedTextField(
                value = currentUsername,
                onValueChange = { currentUsername = it },
                label = { Text("Current User Name") },
                enabled = false, // Make it read-only
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Username field
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("New User Name") },
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
                    if (newUsername.isNotBlank()) {
                        val updatedUserDTO = UserDTO(
                            id = user.id,
                            username = newUsername,
                            password = user.password ?: "" // Preserve existing password
                        )
                        remoteUserViewModel.updatedUser(
                            updatedUser = updatedUserDTO,
                            user = user,
                            onSuccess = { newUpdatedUser ->
                                newUsername = newUpdatedUser.username // Update local state
                                val userJson = Gson().toJson(newUpdatedUser)
                                val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("UserProfile/$encodedUserJson") {
                                    popUpTo("UserProfile/{user}") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onFailure = { error ->
                                errorMessage = error
                            }
                        )
                    } else {
                        errorMessage = "New username cannot be empty"
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
    when (updateUserMessageUiState) {
        is UpdateUserMessageUiState.Success -> {
            LaunchedEffect(updateUserMessageUiState) {
                val newUpdatedUser = (updateUserMessageUiState as UpdateUserMessageUiState.Success).user
                newUsername = newUpdatedUser.username // Update local state
                val userJson = Gson().toJson(newUpdatedUser)
                val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                navController.navigate("UserProfile/$encodedUserJson") {
                    popUpTo("UserProfile/{user}") {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        is UpdateUserMessageUiState.Error -> {
            errorMessage = (updateUserMessageUiState as UpdateUserMessageUiState.Error).message
        }
        is UpdateUserMessageUiState.Loading -> {
            // Optionally show a loading indicator
        }
    }
}