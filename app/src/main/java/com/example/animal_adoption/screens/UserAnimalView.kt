package com.example.animal_adoption.screens

import android.util.Log
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.viewmodel.RemoteShelterViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAnimalView(
    navController: NavHostController,
    animal: AnimalDTO?,
    user: UserDTO?,
    shelterViewModel: RemoteShelterViewModel
) {
    if (animal == null || user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error al cargar datos del animal o usuario.",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }
    val shelterMap by shelterViewModel.shelterMap.collectAsState(initial = emptyMap())

    LaunchedEffect(Unit) {
        shelterViewModel.loadShelters()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(animal.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navegar de vuelta a UserHome. Aseguramos que se pasa el userJson.
                        val userJson = Gson().toJson(user)
                        val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("UserHome/$encodedUserJson") {
                            // Limpia el back stack hasta UserHome para evitar múltiples instancias
                            popUpTo("UserHome/{encodedUserJson}") {
                                inclusive = true // Incluye UserHome para limpiar todo detrás si es necesario
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to animals list"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Un poco de padding horizontal
                    onClick = {
                        // **Aquí iría la lógica para iniciar el proceso de adopción.**
                        // Por ejemplo, podrías:
                        // 1. Mostrar un diálogo de confirmación.
                        // 2. Navegar a una pantalla de formulario de adopción (ej: "AdoptAnimalForm").
                        // 3. Llamar a un metodo en un ViewModel para registrar el interés de adopción.
                        Log.d("UserAnimalView", "Botón Adoptar presionado para ${animal.name}")
                        // Ejemplo: Si quieres mostrar un diálogo simple para confirmar:
                        // showAdoptionConfirmationDialog = true // Necesitarías un `remember { mutableStateOf(false) }` para esto.
                        // O navegar a un formulario de adopción:
                        // val animalJson = URLEncoder.encode(Gson().toJson(animal), StandardCharsets.UTF_8.toString())
                        // val userJson = URLEncoder.encode(Gson().toJson(user), StandardCharsets.UTF_8.toString())
                        // navController.navigate("AdoptAnimalForm/$animalJson/$userJson")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Adoptar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Nombre: ${animal.name}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REIAC: ${animal.reiac}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Refugio: ${shelterMap[animal.shelterId]}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Descripción: Un animal cariñoso en busca de un hogar. Le encanta jugar y necesita mucho amor.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}