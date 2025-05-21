package com.example.animal_adoption.model

import com.google.gson.annotations.SerializedName

data class AnimalDTO(
    @SerializedName("id") val id: Integer?,
    @SerializedName("reaic") val reiac: Int,
    @SerializedName("name") val name: String,
    @SerializedName("shelter_id") val shelterId: Integer?
)

data class newAnimal(
    val reiac: Int,
    val name: String,
    val shelterId: Integer?
)
