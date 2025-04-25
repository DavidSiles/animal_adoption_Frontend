package com.example.animal_adoption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.animal_adoption.screens.UserLogin
import com.example.animal_adoption.screens.UserHome
import com.example.animal_adoption.screens.UserRegister


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                NavHost(navController = navController, startDestination = "UserRegister") {
                    composable("UserLogin") {
                        UserLogin(navController = navController, remoteUserViewModel = viewModel())
                    }
                    composable("UserHome/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        UserHome(navController = navController, id = id)
                    }
                    composable("UserRegister") { backStackEntry ->
                        UserRegister(navController = navController, remoteUserViewModel = viewModel())
                    }
                }
            }
        }
    }
}

