package com.example.animal_adoption.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.AnimalDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteAnimalInterface {
    @GET("shelters/{shelterId}")
    suspend fun getAnimalsByShelter(@Path("shelterId") shelterId: Int): List<AnimalDTO>

    @GET("animal/{reiac}")
    suspend fun getAnimalByReiac(@Path("reiac") reiac: Int): AnimalDTO

    @GET("animal/name/{name}")
    suspend fun getAnimalByName(@Path("name") name: String): AnimalDTO

    @GET("animal/list")
    suspend fun getAllAnimals(): List<AnimalDTO>

    @DELETE("animal/{animalId}")
    suspend fun deleteAnimal(@Path("animalId") animalId: Int): Response<Unit>
}

sealed interface AnimalUiState {
    data class Success(val animals: List<AnimalDTO>) : AnimalUiState
    data class OneAnimal(val animal: AnimalDTO) : AnimalUiState
    object Error : AnimalUiState
    object Loading : AnimalUiState
}

sealed interface DeleteAnimalMessageUiState {
    data class Success(val message: String) : DeleteAnimalMessageUiState
    data class Error(val message: String) : DeleteAnimalMessageUiState
    object Loading : DeleteAnimalMessageUiState
}

class RemoteAnimalViewModel : ViewModel() {

    private val _animalUiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Loading)
    val animalUiState: StateFlow<AnimalUiState> = _animalUiState

    private val _deleteAnimalMessageUiState = MutableStateFlow<DeleteAnimalMessageUiState>(DeleteAnimalMessageUiState.Loading)
    val deleteAnimalMessageUiState: StateFlow<DeleteAnimalMessageUiState> = _deleteAnimalMessageUiState.asStateFlow()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val animalService = retrofit.create(RemoteAnimalInterface::class.java)

    fun getAnimalsByShelter(shelterId: Int) {
        viewModelScope.launch {
            _animalUiState.value = AnimalUiState.Loading
            try {
                val animals = animalService.getAnimalsByShelter(shelterId)
                _animalUiState.value = AnimalUiState.Success(animals)
            } catch (e: Exception) {
                Log.e("AnimalViewModel", "Error fetching animals: ${e.message}")
                _animalUiState.value = AnimalUiState.Error
            }
        }
    }

    fun getAnimalByReiac(reiac: Int) {
        viewModelScope.launch {
            _animalUiState.value = AnimalUiState.Loading
            try {
                val animal = animalService.getAnimalByReiac(reiac)
                _animalUiState.value = AnimalUiState.OneAnimal(animal)
            } catch (e: Exception) {
                Log.e("AnimalViewModel", "Error fetching animal by REIAC: ${e.message}")
                _animalUiState.value = AnimalUiState.Error
            }
        }
    }

    fun getAnimalByName(name: String) {
        viewModelScope.launch {
            _animalUiState.value = AnimalUiState.Loading
            try {
                val animal = animalService.getAnimalByName(name)
                _animalUiState.value = AnimalUiState.OneAnimal(animal)
            } catch (e: Exception) {
                Log.e("AnimalViewModel", "Error fetching animal by name: ${e.message}")
                _animalUiState.value = AnimalUiState.Error
            }
        }
    }

    fun getAllAnimals() {
        viewModelScope.launch {
            _animalUiState.value = AnimalUiState.Loading
            try {
                val animals = animalService.getAllAnimals()
                _animalUiState.value = AnimalUiState.Success(animals)
            } catch (e: Exception) {
                Log.e("AnimalViewModel", "Error fetching all animals: ${e.message}")
                _animalUiState.value = AnimalUiState.Error
            }
        }
    }

    fun deleteAnimal(animalId: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Loading
            try {
                val response = animalService.deleteAnimal(animalId)
                if (response.isSuccessful) {
                    val successMessage = "Shelter deleted successfully"
                    Log.d("DeleteAnimal", successMessage)
                    _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Success(successMessage)
                    onSuccess(successMessage)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid animal ID"
                        404 -> "Animal not found"
                        else -> "Failed to delete animal: ${response.message()}"
                    }
                    Log.e("DeleteAnimal", "HTTP error during deletion: ${response.message()}, Code: ${response.code()}")
                    _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Error(errorMessage)
                    onFailure(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("DeleteAnimal", "Error during deletion: ${e.message}", e)
                val errorMessage = "Failed to delete animal: ${e.message}"
                _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }
}