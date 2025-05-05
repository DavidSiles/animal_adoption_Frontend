package com.example.animal_adoption.model

class AnimalDTO (
    val id: Integer,
    val reiac: Int,
    val name: String,
)

data class CreateNewAnimalRequest(
    val reiac: Int,
    val name: String,
)