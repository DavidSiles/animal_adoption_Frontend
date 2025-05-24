package com.example.animal_adoption.screens.widgets

import kotlin.text.contains

//Clase publica para tomar validaciones de campos
public class FieldValidations {
    companion object {
        // Validación para sheltername
        fun validateName(input: String): String? {
            return when {
                input.isEmpty() -> "Name field empty"
                input.length < 3 -> "Name must be at least 3 characters long"
                input.length > 20 -> "Name must not exceed 20 characters"
                !input.matches("^(?=.*[A-Z]).*$".toRegex()) ->
                    "Shelter name must contain at least one uppercase and lowercase letter"

                else -> null
            }
        }

        // Validación para password
        fun validatePassword(input: String): String? {
            return when {
                input.isEmpty() -> "Password field empty"
                input.length < 6 -> "Password must be at least 6 characters long"
                input.length > 30 -> "Password must not exceed 30 characters"
                input.contains(" ") -> "Password cannot contain spaces"
                !input.matches("^(?=.*[A-Z]).*$".toRegex()) ->
                    "Password must contain at least one uppercase"

                else -> null
            }
        }

        // Validación para email
        fun validateEmail(input: String): String? {
            return when {
                input.isBlank() -> null
                input.length > 40 -> "Email must not exceed 40 characters"
                input.contains(" ") -> "Email cannot contain spaces"
                !input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()) ->
                    "Wrong Email format"

                else -> null
            }
        }

        // Validación Phone
        fun validatePhone(input: String): String? {
            return when {
                input.isBlank() -> null
                input.contains(" ") -> "Email cannot contain spaces"
                input.toInt() < 0 -> "Number can't be negative"
                input.length < 3 -> "Number must have minimum 3 digits"
                input.length > 12 -> "Number must have maximum 12 digits"
                else -> null
            }
        }

        // Validación Reiac
        fun validateReiac(input: Int): String? {
            return when {
                input.toString().contains(" ") -> "Email cannot contain spaces"
                input.toInt() < 0 -> "Number can't be negative"
                input.toString().length != 9 -> "Number must have exactly 9 digits"
                else -> null
            }
        }

        // Validación Reiac
        fun validateAnimalName(input: String): String? {
            return when {
                input.isEmpty() -> "Name field empty"
                input.length < 3 -> "Name must be at least 3 characters long"
                input.length > 20 -> "Name must not exceed 20 characters"
                !input.matches("^[A-Za-z]+$".toRegex()) -> "Shelter name must contain only letters"
                else -> null
            }
        }

    }
}