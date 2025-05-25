package com.example.animal_adoption.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusDropdownFilter(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("PENDING", "ACCEPTED", "REJECTED", null)
    val displayText = selectedStatus ?: "Todos"

    Box {
        Button(onClick = { expanded = true }) {
            Text("Estado: $displayText")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status ?: "Todos") },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
