package com.example.animal_adoption.model

class AnimalDTO (
    val id: Integer,
    val reiac: Integer?,
    val name: String,
)

data class CreateNewAnimalRequest(
    val reiac: Integer?,
    val name: String,
)