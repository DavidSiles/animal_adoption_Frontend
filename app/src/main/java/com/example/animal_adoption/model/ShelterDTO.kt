package com.example.animal_adoption.model

data class ShelterDTO (
    val id: Int,
    val sheltername: String,
    @Transient
    val password: String,
    val email: String?,
    val phone: String?
)

data class ShelterLoginRequest(
    val sheltername: String,
    val password: String,
)

data class ShelterRegisterRequest(
    val sheltername: String,
    val password: String,
    val email: String?,
    val phone: String?
)