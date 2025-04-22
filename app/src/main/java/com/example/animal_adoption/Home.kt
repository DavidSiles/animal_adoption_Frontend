package com.example.animal_adoption

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Home(navController: NavHostController, id: Int?) {
    // Comprobamos si el userId es null
    if (id == null) {
        Log.e("Home", "User ID is null, can't navigate or display user data.")
        return
    }

    // Navegar a la pantalla correcta cuando se entra al HomeScreen
    LaunchedEffect(id) {
        try {
            navController.navigate("Home/$id")
        } catch (e: Exception) {
            Log.e("Login", "Navigation failed: ${e.message}")
        }
    }

    // Layout de la pantalla de inicio
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de bienvenida
        Text(
            text = "Welcome again!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 16.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        )

        // Mostrar información del enfermero (si tienes alguna para mostrar)
        // Esto podría incluir el nombre del enfermero o datos adicionales.
        Text(
            text = "Your User ID: $id",
            style = TextStyle(
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spacer
        Spacer(modifier = Modifier.height(16.dp))

        /*
        // Botón para cerrar sesión
        Button(onClick = { navController.navigate("NurseLoginScreen") }) {
            Text(
                text = stringResource(id = R.string.logoutButton),
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }
        */

    }
}