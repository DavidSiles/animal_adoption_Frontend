package com.example.animal_adoption.model
//Clase publica para tomar validaciones de campos
public  class FieldValidations {
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

        // Validación Reiac
        fun validateReiac(input: Int): String? {
            return when {
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
