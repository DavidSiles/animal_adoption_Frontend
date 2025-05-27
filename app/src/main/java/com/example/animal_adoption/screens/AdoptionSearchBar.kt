package com.example.animal_adoption.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.animal_adoption.model.AdoptionRequestDTO
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.screens.widgets.UserBottomBar
import com.example.animal_adoption.viewmodel.AdoptionRequestUiState
import com.example.animal_adoption.viewmodel.AnimalSearchUiState
import com.example.animal_adoption.viewmodel.RemoteAdoptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionSearchBar(
    navController: NavHostController,
    viewModel: RemoteAdoptionViewModel,
    user: UserDTO? = null,
    shelter: ShelterDTO? = null
) {
    val adoptionUiState by viewModel.adoptionUiState.collectAsState()
    val animalSearchState by viewModel.animalSearchState.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    val TuonsOrange = Color(0xFFFF7043)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Adoption requests & search",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TuonsOrange.copy(alpha = 0.85f),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            UserBottomBar(navController = navController, user = user)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.setSearchQuery(it)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Find animal...", fontFamily = FontFamily.Default) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    Icons.Filled.Clear,
                                    contentDescription = "Limpiar búsqueda"
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TuonsOrange,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = TuonsOrange,
                        cursorColor = TuonsOrange
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Default)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TuonsOrange,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filtrar por estado")
                        Spacer(Modifier.width(4.dp))
                        Text("Filtrar", fontFamily = FontFamily.Default)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        listOf("ALL", "PENDING", "ACCEPTED", "REJECTED").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(
                                    status,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = if (status == statusFilter) FontWeight.Bold else FontWeight.Normal,
                                    color = if (status == statusFilter) TuonsOrange else MaterialTheme.colorScheme.onSurface
                                ) },
                                onClick = {
                                    expanded = false
                                    viewModel.setStatusFilter(
                                        user = user,
                                        shelter = shelter,
                                        status = if (status == "ALL") null else status
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                when (val state = animalSearchState) {
                    is AnimalSearchUiState.Success -> {
                        if (state.animals.isEmpty()) {
                            Text("No se encontraron animales.")
                        } else {
                            LazyColumn {
                                items(state.animals) { animal ->
                                    AnimalSearchItem(animal)
                                }
                            }
                        }
                    }

                    is AnimalSearchUiState.Error -> {
                        Text("Error buscando animal.")
                    }
                    else -> {}


                }
            } else {
                when (val state = adoptionUiState) {
                    is AdoptionRequestUiState.Success -> {
                        LazyColumn {
                            items(state.requests) { request ->
                                AdoptionRequestItem(request)
                            }
                        }
                    }

                    is AdoptionRequestUiState.Error -> {
                        Text("Error cargando solicitudes.")
                    }

                   else -> {}
                }
            }
        }
    }
}

@Composable
fun AnimalSearchItem(animal: AnimalDTO) {
    val TuonsOrange = Color(0xFFFF7043)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = animal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = TuonsOrange
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${animal.id}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Refugio ID: ${animal.shelterId}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdoptionRequestItem(request: AdoptionRequestDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Animal ID: ${request.animalId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))

            val statusColor = when (request.status) {
                "PENDING" -> Color(0xFFFF7043)
                "ACCEPTED" -> Color(0xFF4CAF50)
                "REJECTED" -> Color(0xFFF44336)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text("State: ${request.status}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default,
                color = statusColor)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Shelter ID: ${request.shelterId}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))

            request.requestDate?.let { dateMillis ->
                Text("Date: ${request.requestDate}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }

            Text("Date: ${request.requestDate}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Default)
        }
    }
}
