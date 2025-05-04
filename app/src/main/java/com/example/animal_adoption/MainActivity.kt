package com.example.animal_adoption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.animal_adoption.screens.FirsScreen
import com.example.animal_adoption.screens.ShelterLogin
import com.example.animal_adoption.screens.ShelterRegister
import com.example.animal_adoption.screens.ShelterHome
import com.example.animal_adoption.screens.ShelterProfile
import com.example.animal_adoption.screens.UserLogin
import com.example.animal_adoption.screens.UserHome
import com.example.animal_adoption.screens.UserRegister
import com.example.animal_adoption.screens.UserProfile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "FirstScreen") {
                    composable("FirstScreen") {
                        FirsScreen(navController = navController)
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
                    composable("UserRegister") { backStackEntry ->
                        UserRegister(navController = navController, remoteUserViewModel = viewModel())
                    }

                    composable("ShelterLogin") {
                        ShelterLogin(navController = navController, remoteShelterViewModel = viewModel())
                    }
                    composable("ShelterHome/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        ShelterHome(navController = navController, id = id)
                    }
                    composable("ShelterProfile/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        ShelterProfile(navController = navController, id = id)
                    }
                    composable("ShelterRegister") { backStackEntry ->
                        ShelterRegister(navController = navController, remoteShelterViewModel = viewModel())
                    }
                }
            }
        }
    }

}

