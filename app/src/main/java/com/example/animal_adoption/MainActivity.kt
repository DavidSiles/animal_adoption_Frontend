package com.example.animal_adoption

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.FirstScreen
import com.example.animal_adoption.screens.ShelterAnimalView
import com.example.animal_adoption.screens.ShelterConfiguration
import com.example.animal_adoption.screens.ShelterCreateAnimal
import com.example.animal_adoption.screens.ShelterHome
import com.example.animal_adoption.screens.ShelterListAnimals
import com.example.animal_adoption.screens.ShelterLogin
import com.example.animal_adoption.screens.ShelterRegister
import com.example.animal_adoption.screens.ShelterProfile
import com.example.animal_adoption.screens.ShelterUpdateAnimal
import com.example.animal_adoption.screens.UserUpdateData
import com.example.animal_adoption.screens.ShelterUpdateData
import com.example.animal_adoption.screens.UserHome
import com.example.animal_adoption.screens.UserLogin
import com.example.animal_adoption.screens.UserProfile
import com.example.animal_adoption.screens.UserRegister
import com.example.animal_adoption.viewmodel.RemoteAnimalViewModel
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.example.animal_adoption.viewmodel.RemoteUserViewModel
import com.example.animal_adoption.screens.ShelterProfile
import com.example.animal_adoption.screens.ShelterUpdateAnimal
import com.example.animal_adoption.screens.UserConfiguration
import com.example.animal_adoption.screens.ShelterUpdateData
import com.example.animal_adoption.screens.UserAnimalView

import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "FirstScreen") {
                    composable("FirstScreen") {
                        val userFactory = RemoteUserViewModelFactory(applicationContext)
                        val shelterFactory = RemoteShelterViewModelFactory(applicationContext)
                        val animalFactory = RemoteAnimalViewModelFactory(applicationContext)
                        FirstScreen(
                            navController = navController,
                            remoteUserViewModel = viewModel(factory = userFactory),
                            remoteShelterViewModel = viewModel(factory = shelterFactory),
                            remoteAnimalViewModel = viewModel(factory = animalFactory)
                        )
                    }

                    composable("UserLogin") {
                        val factory = RemoteUserViewModelFactory(applicationContext)
                        UserLogin(
                            navController = navController,
                            remoteUserViewModel = viewModel(factory = factory)
                        )
                    }

                    composable("UserHome/{user}",
                        arguments = listOf(
                            navArgument("user") { type = NavType.StringType} //defino user explícitamente como string
                        )
                    ) { backStackEntry ->
                        val userString = backStackEntry.arguments?.getString("user")
                        val user = userString?.let {
                            try {
                                // Decodificación necesaria porque estamos usando URLEncoder al navegar
                                val decodedUserJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Gson().fromJson(decodedUserJson, UserDTO::class.java)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Error deserializing UserDTO for UserHome: ${e.message}", e)
                                null
                            }
                        }
                        if (user == null) {
                            LaunchedEffect(Unit) {
                                Log.e("NAV_DEBUG", "NavGraph - UserHome: UserDTO is null after deserialization. Redirecting to Login.")
                                navController.navigate("UserLogin") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        } else {
                            val factoryShelter = RemoteShelterViewModelFactory(applicationContext)
                            val factoryAnimal = RemoteAnimalViewModelFactory(applicationContext)
                            UserHome(
                                navController = navController,
                                user = user,
                                animalViewModel = viewModel(factory = factoryAnimal),
                                shelterViewModel = viewModel(factory = factoryShelter)
                            )
                        }
                    }

                    composable("UserProfile/{user}") { backStackEntry ->
                        val userString = backStackEntry.arguments?.getString("user")
                        val user = userString?.let {
                            try {
                                val decodedUserJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Gson().fromJson(decodedUserJson, UserDTO::class.java)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Error deserializing UserDTO for UserProfile: ${e.message}", e)
                                null
                            }
                        }
                        UserProfile(navController = navController, user = user)
                    }

                    composable("UserRegister") {
                        val factory = RemoteUserViewModelFactory(applicationContext)
                        UserRegister(
                            navController = navController,
                            remoteUserViewModel = viewModel(factory = factory)
                        )
                    }

                    composable("UserConfiguration/{user}") { backStackEntry ->
                        val userString = backStackEntry.arguments?.getString("user")
                        val user = userString?.let {
                            try {
                                val decodedUserJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Gson().fromJson(decodedUserJson, UserDTO::class.java)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Error deserializing UserDTO for UserConfiguration: ${e.message}", e)
                                null
                            }
                        }
                        val factory = RemoteUserViewModelFactory(applicationContext)
                        UserConfiguration(
                            navController = navController,
                            remoteUserViewModel = viewModel(factory = factory),
                            user = user
                        )
                    }


                    composable("UserUpdateData/{user}") { backStackEntry ->
                        val user = deserializeUser(backStackEntry)
                        val factory = RemoteUserViewModelFactory(applicationContext)
                        UserUpdateData(
                            navController = navController,
                            remoteUserViewModel = viewModel(factory = factory),
                            user = user
                        )
                    }

                    // --- UserAnimalView Route (Mantiene 'user' y 'animal' pero decodifica internamente) ---
                    composable(route = "UserAnimalView/{animal}/{user}",
                        arguments = listOf(
                            navArgument("animal") { type = NavType.StringType },
                            navArgument("user") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val animalString = backStackEntry.arguments?.getString("animal")
                        val userString = backStackEntry.arguments?.getString("user")

                        val animal = animalString?.let {
                            try {
                                val decodedAnimalJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Gson().fromJson(decodedAnimalJson, AnimalDTO::class.java)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Error deserializing AnimalDTO for UserAnimalView: ${e.message}", e)
                                null
                            }
                        }

                        val user = userString?.let {
                            try {
                                val decodedUserJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                                Gson().fromJson(decodedUserJson, UserDTO::class.java)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Error deserializing UserDTO for UserAnimalView: ${e.message}", e)
                                null
                            }
                        }
                        val shelterFactory = RemoteShelterViewModelFactory(applicationContext)
                        UserAnimalView(navController = navController, animal = animal, user = user, shelterViewModel = viewModel(factory = shelterFactory))

                    }

                    composable("ShelterLogin") {
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterLogin(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory)
                        )
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
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterRegister(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory)
                        )
                    }

                    composable("ShelterCreateAnimal/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterCreateAnimal(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory),
                            shelter = shelter
                        )
                    }

                    composable("ShelterListAnimals/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterListAnimals(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory),
                            shelter = shelter
                        )
                    }

                    composable("ShelterConfiguration/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterConfiguration(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory),
                            shelter = shelter
                        )
                    }

                    composable("ShelterAnimalView/{animal}/{shelter}") { backStackEntry ->
                        val animal = deserializeAnimal(backStackEntry)
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteAnimalViewModelFactory(applicationContext)
                        ShelterAnimalView(
                            navController = navController,
                            remoteAnimalViewModel = viewModel(factory = factory),
                            animal = animal,
                            shelter = shelter
                        )
                    }

                    composable("ShelterUpdateAnimal/{animal}/{shelter}") { backStackEntry ->
                        val animal = deserializeAnimal(backStackEntry)
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteAnimalViewModelFactory(applicationContext)
                        ShelterUpdateAnimal(
                            navController = navController,
                            remoteAnimalViewModel = viewModel(factory = factory),
                            animal = animal,
                            shelter = shelter
                        )
                    }

                    composable("ShelterUpdateData/{shelter}") { backStackEntry ->
                        val shelter = deserializeShelter(backStackEntry)
                        val factory = RemoteShelterViewModelFactory(applicationContext)
                        ShelterUpdateData(
                            navController = navController,
                            remoteShelterViewModel = viewModel(factory = factory),
                            shelter = shelter
                        )
                    }
                }
            }
        }
    }

    private fun deserializeUser(backStackEntry: NavBackStackEntry): UserDTO? {
        val userJson = backStackEntry.arguments?.getString("user")
        return try {
            val decodedUserJson = userJson?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            decodedUserJson?.let { Gson().fromJson(it, UserDTO::class.java) }
        } catch (e: Exception) {
            Log.e("Navigation", "Error deserializing UserDTO: ${e.message}", e)
            null
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
        val animalJson = backStackEntry.arguments?.getString("animal")
        return try {
            val decodedAnimalJson = animalJson?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            decodedAnimalJson?.let { Gson().fromJson(it, AnimalDTO::class.java) }
        } catch (e: Exception) {
            Log.e("Navigation", "Error deserializing AnimalDTO: ${e.message}", e)
            null
        }
    }
}

class RemoteUserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteUserViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RemoteShelterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteShelterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteShelterViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RemoteAnimalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteAnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteAnimalViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}