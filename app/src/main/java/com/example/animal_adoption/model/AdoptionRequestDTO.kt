package com.example.animal_adoption.model


data class AdoptionRequestDTO(
    val id: Int,
    val userId: Int,
    val animalId: Int,
    val shelterId: Int,
    val status: String, // "PENDING", "ACCEPTED", "REJECTED"
    val requestDate: String 
)


data class CreateAdoptionRequestDTO(
    val userId: Int,
    val animalId: Int
)

