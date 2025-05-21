package com.example.animal_adoption.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ShelterAnimalView(
    navController: NavController,
    remoteShelterViewModel: RemoteShelterViewModel,
    animal: AnimalDTO?,
    shelter: ShelterDTO?
) {
    if (animal == null || shelter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading animal or shelter data",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    // Log animal details to debug REIAC
    Log.d("ShelterAnimalView", "Animal: ${animal.name}, REIAC: ${animal.reiac}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Animal Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        val shelterJson = Gson().toJson(shelter)
                        navController.navigate("ShelterListAnimals/$shelterJson") {
                            popUpTo("ShelterListAnimals/{shelterJson}") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to shelter animals"
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
            Text(
                text = "Animal Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Name: ${animal.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REIAC: ${animal.reiac ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}