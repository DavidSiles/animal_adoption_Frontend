package com.example.animal_adoption.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.viewmodel.CreateAdoptionRequestUiState
import com.example.animal_adoption.viewmodel.RemoteAdoptionRequestViewModel
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
    shelterViewModel: RemoteShelterViewModel,
    adoptionRequestViewModel: RemoteAdoptionRequestViewModel
) {
    val context = LocalContext.current
    val primaryOrange = Color(0xFFFF7043)

    val createAdoptionRequestState by adoptionRequestViewModel.createAdoptionRequestUiState.collectAsState()

    LaunchedEffect(createAdoptionRequestState) {
        when (createAdoptionRequestState) {
            is CreateAdoptionRequestUiState.Success -> {
                val request = (createAdoptionRequestState as CreateAdoptionRequestUiState.Success).request
                Toast.makeText(context, "¡Solicitud de adopción enviada con éxito! ID: ${request.id}", Toast.LENGTH_LONG).show()
                adoptionRequestViewModel.resetCreateAdoptionRequestUiState()
            }
            is CreateAdoptionRequestUiState.Error -> {
                val message = (createAdoptionRequestState as CreateAdoptionRequestUiState.Error).message
                Toast.makeText(context, "Error al enviar solicitud: $message", Toast.LENGTH_LONG).show()
                adoptionRequestViewModel.resetCreateAdoptionRequestUiState()
            }
            CreateAdoptionRequestUiState.Loading -> {
                // El botón mostrará un indicador de carga, no se necesita Toast aquí
            }
            CreateAdoptionRequestUiState.Idle -> { /* No hacer nada en estado inicial/idle */ }
        }
    }

    if (animal == null || user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error al cargar datos del animal o usuario.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
            ) {
                Text("Volver", color = Color.White)
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
                title = {
                    Text(
                        animal.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val userJson = Gson().toJson(user)
                        val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("UserHome/$encodedUserJson") {
                            popUpTo("UserHome/{encodedUserJson}") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to animals list",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryOrange,
                    titleContentColor = Color.White
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        Log.d("UserAnimalView", "Botón Adoptar presionado para ${animal.name}")
                        if (user != null && animal != null) {
                            adoptionRequestViewModel.createAdoptionRequest(
                                userId = user.id,
                                animalId = animal.id
                            )
                        } else {
                            Toast.makeText(context, "Datos de usuario o animal no disponibles para la solicitud.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = createAdoptionRequestState !is CreateAdoptionRequestUiState.Loading
                ) {
                    if (createAdoptionRequestState is CreateAdoptionRequestUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Solicitando...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Adoptar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = animal.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ID REIAC: ${animal.reiac}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Refugio: ${shelterMap[animal.shelterId] ?: "Desconocido"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Acerca de ${animal.name}:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Un animal cariñoso en busca de un hogar. Le encanta jugar y necesita mucho amor. Compatible con niños y otras mascotas. ¡Ven a conocerle!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}