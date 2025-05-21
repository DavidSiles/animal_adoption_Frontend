package com.example.animal_adoption.screens.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AnimalCard(animal: AnimalDTO, shelter: ShelterDTO, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                val animalJson = Gson().toJson(animal)
                val shelterJson = Gson().toJson(shelter)
                val encodedAnimalJson = URLEncoder.encode(animalJson, StandardCharsets.UTF_8.toString())
                val encodedShelterJson = URLEncoder.encode(shelterJson, StandardCharsets.UTF_8.toString())
                navController.navigate("ShelterAnimalView/$encodedAnimalJson/$encodedShelterJson")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Name: ${animal.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "REIAC: ${animal.reiac ?: "N/A"}")
        }
    }
}