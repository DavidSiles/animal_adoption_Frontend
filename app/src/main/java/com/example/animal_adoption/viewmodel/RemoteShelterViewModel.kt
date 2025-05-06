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
    data class Success(val loginMessage: ShelterDTO) : ShelterLoginMessageUiState

    object Error : ShelterLoginMessageUiState
    object Loading : ShelterLoginMessageUiState
}

sealed interface CreateNewAnimalMessageUiState {
    data class Success(val crateNewAnimalMessage: AnimalDTO) : CreateNewAnimalMessageUiState

    object Error : CreateNewAnimalMessageUiState
    object Loading : CreateNewAnimalMessageUiState
}

interface RemoteShelterInterface {
    @GET("shelters/index")
    suspend fun getRemoteShelter(): List<ShelterDTO>

    @GET("shelters/{username}")
    suspend fun getShelterBySheltername(@Path("username") username: String): ShelterDTO

    @POST("shelters/login")
    suspend fun ShelterLogin(@Body loginRequest: ShelterLoginRequest): ShelterDTO

    @POST("shelters/create")
    suspend fun ShelterRegister(@Body registerRequest: ShelterRegisterRequest): ShelterDTO

    @POST("animal/new")
    suspend fun ShelterCreateNewAnimal(@Body createNewAnimalRequest: CreateNewAnimalRequest): AnimalDTO
}

class RemoteShelterViewModel : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<ShelterRemoteMessageUiState>(
        ShelterRemoteMessageUiState.Loading)
    var remoteMessageUiState: StateFlow<ShelterRemoteMessageUiState> = _remoteMessageUiState

    private val _loginMessageUiState = MutableStateFlow<ShelterLoginMessageUiState>(
        ShelterLoginMessageUiState.Loading)
    var loginMessageUiState: StateFlow<ShelterLoginMessageUiState> = _loginMessageUiState

    private val _createNewAnimalMessageUiState = MutableStateFlow<CreateNewAnimalMessageUiState>(
        CreateNewAnimalMessageUiState.Loading)
    var createNewAnimalMessageUiState: StateFlow<CreateNewAnimalMessageUiState> = _createNewAnimalMessageUiState

    //ip del emulador 10.0.0.2.
    //ip del movil DavidSiles 10.0.22.100
    val connection = Retrofit.Builder()
        .baseUrl("http://10.0.22.100:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val remoteService = connection.create(RemoteShelterInterface::class.java)

    private val _id = MutableStateFlow<Integer?>(null)
    val id: StateFlow<Integer?> = _id

    // Get all sheter
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

    // Login
    fun ShelterLogin(sheltername: String, password: String, onSuccess: (Integer) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val loginRequest = ShelterLoginRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterLogin(loginRequest)
                _id.value = shelter.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)

                // Pasamos el nurseId al callback de éxito
                onSuccess(shelter.id)
            } catch (e: Exception) {
                Log.e("Login", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error
            }
        }
    }

    // Registro
    fun ShelterRegister(sheltername: String, password: String, onSuccess: (Integer) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val registerRequest = ShelterRegisterRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterRegister(registerRequest)
                _id.value = shelter.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)

                // Pasamos el nurseId al callback de éxito
                onSuccess(shelter.id)
            } catch (e: Exception) {
                Log.e("Register", "Error during registration: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error
            }
        }
    }

    // Función para obtener los detalles del enfermero por ID
    fun getShelterBySheltername(sheltername: String, onSuccess: (ShelterDTO) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val shelter = remoteService.getShelterBySheltername(sheltername)  // Realizamos la petición GET
                onSuccess(shelter)  // Pasamos el objeto Nurse a la UI
            } catch (e: Exception) {
                Log.e("GetShelterById", "Error fetching nurse: ${e.message}", e)
                onFailure()  // Llamamos al callback de error si ocurre un fallo
            }
        }
    }

    // Create new animal with shelter
    fun CreateNewAnimal(reiac: Int, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Loading
            try {
                Log.e("Create animal", "Error during Creation Animal")
                val registerRequest = CreateNewAnimalRequest(reiac = reiac, name = name)
                Log.e("Create animal", "Error during Creation Animal")
                val animal = remoteService.ShelterCreateNewAnimal(registerRequest)
                Log.e("Create animal", "Error during Creation Animal")
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Success(animal)
                Log.e("Create animal", "Error during Creation Animal")
                // Pasamos el animal al callback de éxito
                onSuccess()
            } catch (e: Exception) {
                Log.e("Create animal", "Error during Creation Animal: ${e.message}", e)
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Error
            }
        }
    }
}