package com.example.animal_adoption.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.CreateNewAnimalRequest
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.ShelterLoginRequest
import com.example.animal_adoption.model.ShelterRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

sealed interface ShelterRemoteMessageUiState {
    data class Success(val remoteMessage: List<ShelterDTO>) : ShelterRemoteMessageUiState
    object Error : ShelterRemoteMessageUiState
    object Loading : ShelterRemoteMessageUiState
}

sealed interface ShelterLoginMessageUiState {
    data class Success(val loginMessage: ShelterDTO?) : ShelterLoginMessageUiState
    object Error : ShelterLoginMessageUiState
    object Loading : ShelterLoginMessageUiState
}

sealed interface CreateNewAnimalMessageUiState {
    data class Success(val crateNewAnimalMessage: AnimalDTO?) : CreateNewAnimalMessageUiState
    object Error : CreateNewAnimalMessageUiState
    object Loading : CreateNewAnimalMessageUiState
}

sealed interface GetShelterAnimalsListMessageUiState {
    data class Success(val getShelterAnimalsListMessage: List<AnimalDTO?>?) : GetShelterAnimalsListMessageUiState
    object Error : GetShelterAnimalsListMessageUiState
    object Loading : GetShelterAnimalsListMessageUiState
}

interface RemoteShelterInterface {
    @GET("shelters/index")
    suspend fun getRemoteShelter(): List<ShelterDTO>

    @GET("animal/shelterList/{shelterId}")
    suspend fun getShelterListAnimals(@Path("shelterId") shelterId: Integer): List<AnimalDTO>?

    @POST("shelters/login")
    suspend fun ShelterLogin(@Body loginRequest: ShelterLoginRequest): ShelterDTO

    @POST("shelters/create")
    suspend fun ShelterRegister(@Body registerRequest: ShelterRegisterRequest): ShelterDTO

    @POST("/new")
    suspend fun ShelterCreateNewAnimal(@Body createNewAnimalRequest: CreateNewAnimalRequest): AnimalDTO
}

class RemoteShelterViewModel : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<ShelterRemoteMessageUiState>(ShelterRemoteMessageUiState.Loading)
    val remoteMessageUiState: StateFlow<ShelterRemoteMessageUiState> = _remoteMessageUiState.asStateFlow()

    private val _loginMessageUiState = MutableStateFlow<ShelterLoginMessageUiState>(ShelterLoginMessageUiState.Loading)
    val loginMessageUiState: StateFlow<ShelterLoginMessageUiState> = _loginMessageUiState.asStateFlow()

    private val _createNewAnimalMessageUiState = MutableStateFlow<CreateNewAnimalMessageUiState>(CreateNewAnimalMessageUiState.Loading)
    val createNewAnimalMessageUiState: StateFlow<CreateNewAnimalMessageUiState> = _createNewAnimalMessageUiState.asStateFlow()

    private val _getShelterAnimalsListMessage = MutableStateFlow<GetShelterAnimalsListMessageUiState>(GetShelterAnimalsListMessageUiState.Loading)
    val getShelterAnimalsListMessage: StateFlow<GetShelterAnimalsListMessageUiState> = _getShelterAnimalsListMessage.asStateFlow()

    private val _shelter = MutableStateFlow<ShelterDTO?>(null)
    val shelter: StateFlow<ShelterDTO?> = _shelter.asStateFlow()

    private val _shelterId = MutableStateFlow<Integer?>(null)
    val shelterId: StateFlow<Integer?> = _shelterId.asStateFlow()

    private val _animal = MutableStateFlow<AnimalDTO?>(null)
    val animal: StateFlow<AnimalDTO?> = _animal.asStateFlow()

    private val _animalList = MutableStateFlow<List<AnimalDTO>>(emptyList())
    val animalList: StateFlow<List<AnimalDTO>> = _animalList.asStateFlow()

    val connection = Retrofit.Builder()
        .baseUrl("http://10.0.22.100:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val remoteService = connection.create(RemoteShelterInterface::class.java)

    fun getRemoteShelter() {
        viewModelScope.launch {
            _remoteMessageUiState.value = ShelterRemoteMessageUiState.Loading
            try {
                Log.d("GetShelter", "Fetching shelter from server...")
                val response = remoteService.getRemoteShelter()
                Log.d("GetShelter", "Successfully fetched shelter: $response")
                _remoteMessageUiState.value = ShelterRemoteMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("GetShelter", "Error fetching shelter: ${e.message}", e)
                _remoteMessageUiState.value = ShelterRemoteMessageUiState.Error
            }
        }
    }

    fun ShelterLogin(sheltername: String, password: String, onSuccess: (ShelterDTO?) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val loginRequest = ShelterLoginRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterLogin(loginRequest)
                _shelter.value = shelter
                Log.d("ShelterLogin", "Shelter after login: ${_shelter.value}")
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)
                onSuccess(shelter)
            } catch (e: Exception) {
                Log.e("Login", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error
            }
        }
    }

    fun ShelterRegister(sheltername: String, password: String, onSuccess: (ShelterDTO?) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val registerRequest = ShelterRegisterRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterRegister(registerRequest)
                _shelter.value = shelter
                Log.d("ShelterLogin", "Shelter after login: ${_shelter.value}")
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)
                onSuccess(shelter)
            } catch (e: Exception) {
                Log.e("Register", "Error during registration: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error
            }
        }
    }

    fun getShelterAnimals(shelterId: Integer, onSuccess: (List<AnimalDTO>?) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Loading
            try {
                Log.d("GetShelterAnimals", "Fetching animals for shelterId: $shelterId")
                val animalList = remoteService.getShelterListAnimals(shelterId) ?: emptyList()
                Log.d("GetShelterAnimals", "Fetched ${animalList.size} animals: ${animalList[0].reiac}")
                _animalList.value = animalList
                _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Success(animalList)
                onSuccess(animalList)
            } catch (e: Exception) {
                Log.e("GetShelterAnimals", "Error fetching animals: ${e.message}", e)
                _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Error
                onFailure()
            }
        }
    }

    fun CreateNewAnimal(reiac: Int, name: String, shelterId: Integer?, onSuccess: (AnimalDTO?) -> Unit) {
        viewModelScope.launch {
            _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Loading
            try {
                Log.d("Create animal", "$reiac / $name / $shelterId")
                val registerRequest = CreateNewAnimalRequest(reiac = reiac, name = name, shelterId = shelterId)
                val animal = remoteService.ShelterCreateNewAnimal(registerRequest)
                Log.d("Create animal", "Animal created successfully: $animal")
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Success(animal)
                onSuccess(animal)
            } catch (e: Exception) {
                Log.e("Create animal", "Error during animal creation: ${e.message}", e)
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Error
            }
        }
    }
}