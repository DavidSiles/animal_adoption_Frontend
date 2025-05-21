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

sealed interface ShelterUiState {
    data class Success(val shelters: List<ShelterDTO>) : ShelterUiState
    data class OneShelter(val shelter: ShelterDTO) : ShelterUiState
    object Error : ShelterUiState
    object Loading : ShelterUiState
}

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

    @GET("shelters/list")
    suspend fun getAllShelters(): List<ShelterDTO>
}

class RemoteShelterViewModel : ViewModel() {

    private val _shelterUiState = MutableStateFlow<ShelterUiState>(
        ShelterUiState.Loading)
    val shelterUiState: StateFlow<ShelterUiState> = _shelterUiState

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
    //ip del móvil de FiorellaMoncayo 10.118.3.231:8080
    val connection = Retrofit.Builder()
        .baseUrl("http://10.118.3.231:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val remoteService = connection.create(RemoteShelterInterface::class.java)

    private val _shelter = MutableStateFlow<ShelterDTO?>(null)
    val shelter: StateFlow<ShelterDTO?> = _shelter.asStateFlow()

    private val _animal = MutableStateFlow<AnimalDTO?>(null)
    val animal: StateFlow<AnimalDTO?> = _animal

    //private val shelterService = connection.create(RemoteShelterInterface::class.java)

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
    fun ShelterLogin(sheltername: String, password: String, onSuccess: (ShelterDTO) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val loginRequest = ShelterLoginRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterLogin(loginRequest)
                _shelter.value = shelter
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)
                onSuccess(shelter)
            } catch (e: Exception) {
                Log.e("Login", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error
            }
        }
    }

    // Registro
    fun ShelterRegister(sheltername: String, password: String, onSuccess: (ShelterDTO?) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val registerRequest = ShelterRegisterRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.ShelterRegister(registerRequest)
                _shelter.value = shelter
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(_shelter.value)
                onSuccess(_shelter.value)
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
    fun CreateNewAnimal(reiac: Int, name: String, shelter: ShelterDTO?, onSuccess: (AnimalDTO?) -> Unit) {
        viewModelScope.launch {
            _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Loading
            try {
                val registerRequest = CreateNewAnimalRequest(
                    reiac = reiac,
                    name = name,
                    shelterId = shelter?.id
                )
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

    fun getAllShelters() {
        viewModelScope.launch {
            _shelterUiState.value = ShelterUiState.Loading
            try {
                val shelters = remoteService.getAllShelters()
                _shelterUiState.value = ShelterUiState.Success(shelters)
            } catch (e: Exception) {
                Log.e("ShelterViewModel", "Error fetching all shelters: ${e.message}")
                _shelterUiState.value = ShelterUiState.Error
            }
        }
    }






    private val _shelterMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val shelterMap: StateFlow<Map<Int, String>> = _shelterMap

    fun loadShelters() {
        viewModelScope.launch {
            try {
                val shelters = remoteService.getAllShelters()
                _shelterMap.value = shelters.associateBy({ it.id }, { it.sheltername })
            } catch (e: Exception) {
                Log.e("ShelterViewModel", "Error loading shelters: ${e.message}")
            }
        }
    }




}