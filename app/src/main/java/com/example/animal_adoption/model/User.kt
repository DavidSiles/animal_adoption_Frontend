package com.example.animal_adoption.model

data class User(
    val id: Integer,
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