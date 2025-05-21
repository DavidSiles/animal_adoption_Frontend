package com.example.animal_adoption.model

class AnimalDTO (
    val id: Integer,
    val reiac: Int,
    val name: String,
    val shelterId: Int
)

data class CreateNewAnimalRequest(
    val reiac: Int,
    val name: String,
    val shelterId: Int?
)