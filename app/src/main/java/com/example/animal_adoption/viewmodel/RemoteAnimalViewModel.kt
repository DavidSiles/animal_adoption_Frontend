package com.example.animal_adoption.viewmodel

import android.content.Context
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
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

    @PUT("animal/{updatedAnimal}")
    suspend fun updateAnimal(@Body updatedAnimal: AnimalDTO): AnimalDTO

    @DELETE("animal/{animalId}")
    suspend fun deleteAnimal(@Path("animalId") animalId: Int): Response<Unit>
}

sealed interface AnimalUiState {
    data class Success(val animals: List<AnimalDTO>) : AnimalUiState
    data class OneAnimal(val animal: AnimalDTO) : AnimalUiState
    object Error : AnimalUiState
    object Loading : AnimalUiState
}

sealed interface UpdateAnimalMessageUiState {
    data class Success(val animal: AnimalDTO) : UpdateAnimalMessageUiState
    object Error : UpdateAnimalMessageUiState
    object Loading : UpdateAnimalMessageUiState
}

sealed interface DeleteAnimalMessageUiState {
    data class Success(val message: String) : DeleteAnimalMessageUiState
    data class Error(val message: String) : DeleteAnimalMessageUiState
    object Loading : DeleteAnimalMessageUiState
}

class RemoteAnimalViewModel(context: Context) : ViewModel() {

    private val _animalUiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Loading)
    val animalUiState: StateFlow<AnimalUiState> = _animalUiState

    private val _updateAnimalMessageUiState = MutableStateFlow<UpdateAnimalMessageUiState>(UpdateAnimalMessageUiState.Loading)
    val updateAnimalMessageUiState: StateFlow<UpdateAnimalMessageUiState> = _updateAnimalMessageUiState.asStateFlow()

    private val _deleteAnimalMessageUiState = MutableStateFlow<DeleteAnimalMessageUiState>(DeleteAnimalMessageUiState.Loading)
    val deleteAnimalMessageUiState: StateFlow<DeleteAnimalMessageUiState> = _deleteAnimalMessageUiState.asStateFlow()
/*
    //ip del emulador 10.0.2.2.
    //ip del movil DavidSiles 10.0.22.100
    //ip del movil FioMoncayo 10.118.3.231
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.22.100:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val animalService = retrofit.create(RemoteAnimalInterface::class.java)
*/
    //RemoteConnection
    private lateinit var animalService: RemoteAnimalInterface

    private val _isServiceInitialized = MutableStateFlow(false)
    val isServiceInitialized: StateFlow<Boolean> = _isServiceInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                animalService = NetworkModule.createService<RemoteAnimalInterface>(context)
                _isServiceInitialized.value = true
                Log.d("RemoteAnimalViewModel", "Service initialized")
            } catch (e: Exception) {
                Log.e("RemoteAnimalViewModel", "Failed to initialize service: ${e.message}", e)
                _isServiceInitialized.value = false
            }
        }
    }

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

    fun updateAnimal(
        updatedAnimal: AnimalDTO,
        onSuccess: (AnimalDTO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _updateAnimalMessageUiState .value = UpdateAnimalMessageUiState .Loading
            try {
                val animal= animalService.updateAnimal(updatedAnimal)
                _updateAnimalMessageUiState.value = UpdateAnimalMessageUiState.Success(animal)
                onSuccess(animal)
            } catch (e: Exception) {
                Log.e("AnimalViewModel", "Error fetching all animals: ${e.message}")
                _updateAnimalMessageUiState.value = UpdateAnimalMessageUiState.Error
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
                        else -> "Failed to delete animal"
                    }
                    Log.e("DeleteAnimal", "HTTP error during deletion: ${response.message()}, Code: ${response.code()}")
                    _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Error(errorMessage)
                    onFailure(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("DeleteAnimal", "Error during deletion: ${e.message}", e)
                val errorMessage = "Failed to delete animal"
                _deleteAnimalMessageUiState.value = DeleteAnimalMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }
}