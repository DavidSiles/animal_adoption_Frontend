package com.example.animal_adoption.model


data class AdoptionRequestDTO(
    val id: Int,
    val userId: Int,
    val animalId: Int,
    val shelterId: Int,
    val status: String, // "PENDING", "ACCEPTED", "REJECTED"
    val shelterName: String,
    val animalName : String,
    val requestDate: String // O un formato manejable por frontend (ISO 8601)
)