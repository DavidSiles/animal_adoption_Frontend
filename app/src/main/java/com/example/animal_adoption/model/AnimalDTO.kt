package com.example.animal_adoption.model


class AnimalDTO (
    val id: Int,
    val reiac: Int,
    val name: String,
    val shelterId: Int
)

data class newAnimal(
    val reiac: Int,
    val name: String,
    val shelterId: Int?
)

