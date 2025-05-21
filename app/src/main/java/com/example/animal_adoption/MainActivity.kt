package com.example.animal_adoption

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.screens.FirstScreen
import com.example.animal_adoption.screens.ShelterAnimalView
import com.example.animal_adoption.screens.ShelterConfiguration
import com.example.animal_adoption.screens.ShelterCreateAnimal
import com.example.animal_adoption.screens.ShelterHome
import com.example.animal_adoption.screens.ShelterListAnimals
import com.example.animal_adoption.screens.ShelterLogin
import com.example.animal_adoption.screens.ShelterRegister
import com.example.animal_adoption.screens.UserHome
import com.example.animal_adoption.screens.UserLogin
import com.example.animal_adoption.screens.UserProfile
import com.example.animal_adoption.screens.UserRegister
import com.example.animal_adoption.screens.ShelterProfile
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "FirstScreen") {
                    composable("FirstScreen") {
                        FirstScreen(navController = navController)
                    }

                    composable("UserLogin") {
                        UserLogin(navController = navController, remoteUserViewModel = viewModel())
                    }
                    composable("UserHome/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        UserHome(navController = navController, id = id)
                    }
                    composable("UserProfile/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        UserProfile(navController = navController, id = id)
                    }
                    composable("UserRegister") {
                        UserRegister(navController = navController, remoteUserViewModel = viewModel())
                    }

                    composable("ShelterLogin") {
                        ShelterLogin(navController = navController, remoteShelterViewModel = viewModel())
                    }
                    composable("ShelterHome/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterHome(navController = navController, shelter = shelter)
                    }
                    composable("ShelterProfile/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterProfile(navController = navController, shelter = shelter)
                    }
                    composable("ShelterRegister") {
                        ShelterRegister(navController = navController, remoteShelterViewModel = viewModel())
                    }
                    composable("ShelterCreateAnimal/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterCreateAnimal(navController = navController, remoteShelterViewModel = viewModel(), shelter = shelter)
                    }
                    composable("ShelterListAnimals/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterListAnimals(navController = navController, remoteShelterViewModel = viewModel(), shelter = shelter)
                    }
                    composable("ShelterConfiguration/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterConfiguration(navController = navController, remoteShelterViewModel = viewModel(), shelter = shelter)
                    }
                    composable("ShelterAnimalView/{animal}/{shelter}") { backStackEntry ->
                        val animal = deserializeAnimal(backStackEntry)
                        val shelter = deserializeShelter(backStackEntry)
                        ShelterAnimalView(
                            navController = navController,
                            remoteShelterViewModel = viewModel(),
                            animal = animal,
                            shelter = shelter
                        )
                    }
                }
            }
        }
    }

    private fun deserializeShelter(backStackEntry: NavBackStackEntry): ShelterDTO? {
        val shelterJson = backStackEntry.arguments?.getString("shelter")
        return try {
            shelterJson?.let { Gson().fromJson(it, ShelterDTO::class.java) }
        } catch (e: Exception) {
            Log.e("Navigation", "Error deserializing ShelterDTO: ${e.message}", e)
            null
        }
    }

    private fun deserializeAnimal(backStackEntry: NavBackStackEntry): AnimalDTO? {
        val animalJson = backStackEntry.arguments?.getString("shelter")
        return try {
            animalJson?.let { Gson().fromJson(it, AnimalDTO::class.java) }
        } catch (e: Exception) {
            Log.e("Navigation", "Error deserializing ShelterDTO: ${e.message}", e)
            null
        }
    }
}