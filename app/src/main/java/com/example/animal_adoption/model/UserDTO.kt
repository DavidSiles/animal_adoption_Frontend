package com.example.animal_adoption.model

data class UserDTO(
    val id: Int,
    val username: String,
    val password: String,
)

data class UserLoginRequest(
    val username: String,
    val password: String
)

data class UserRegisterRequest(
    val username: String,
    val password: String,
)