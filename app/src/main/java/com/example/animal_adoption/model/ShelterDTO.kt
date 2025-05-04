package com.example.animal_adoption.model

data class ShelterDTO (
    val id: Integer,
    val sheltername: String,
    val password: String,
)

data class ShelterLoginRequest(
    val sheltername: String,
    val password: String
)

data class ShelterRegisterRequest(
    val sheltername: String,
    val password: String,
)