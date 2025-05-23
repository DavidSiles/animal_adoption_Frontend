package com.example.animal_adoption.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.AnimalDTO
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.ShelterLoginRequest
import com.example.animal_adoption.model.ShelterRegisterRequest
import com.example.animal_adoption.model.newAnimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.google.gson.Gson
import retrofit2.Response
import retrofit2.http.PUT

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
    data class Error(val message: String) : ShelterLoginMessageUiState
    object Loading : ShelterLoginMessageUiState
}

sealed interface ShelterRegisterMessageUiState {
    data class Success(val registerMessage: ShelterDTO?) : ShelterRegisterMessageUiState
    data class Error(val message: String) : ShelterRegisterMessageUiState
    object Loading : ShelterRegisterMessageUiState
}

sealed interface CreateNewAnimalMessageUiState {
    data class Success(val createNewAnimalMessage: AnimalDTO?) : CreateNewAnimalMessageUiState
    data class Error(val message: String) : CreateNewAnimalMessageUiState
    object Loading : CreateNewAnimalMessageUiState
}

sealed interface GetShelterAnimalsListMessageUiState {
    data class Success(val getShelterAnimalsListMessage: List<AnimalDTO?>?) : GetShelterAnimalsListMessageUiState
    data class Error(val message: String) : GetShelterAnimalsListMessageUiState
    object Loading : GetShelterAnimalsListMessageUiState
}

sealed interface UpdateShelterMessageUiState {
    data class Success(val shelter: ShelterDTO) : UpdateShelterMessageUiState
    data class Error(val message: String) : UpdateShelterMessageUiState
    object Loading : UpdateShelterMessageUiState
}

sealed interface DeleteShelterMessageUiState {
    data class Success(val message: String) : DeleteShelterMessageUiState
    data class Error(val message: String) : DeleteShelterMessageUiState
    object Loading : DeleteShelterMessageUiState
}

interface RemoteShelterInterface {
    @GET("shelters/index")
    suspend fun getRemoteShelter(): List<ShelterDTO>

    @GET("animal/shelterList/{shelterId}")
    suspend fun getShelterListAnimals(@Path("shelterId") shelterId: Int): List<AnimalDTO>

    @POST("shelters/login")
    suspend fun shelterLogin(@Body loginRequest: ShelterLoginRequest): ShelterDTO

    @POST("shelters/create")
    suspend fun shelterRegister(@Body registerRequest: ShelterRegisterRequest): ShelterDTO

    @POST("animal/new")
    suspend fun shelterCreateNewAnimal(@Body animalDTO: newAnimal): AnimalDTO

    @GET("shelters/list")
    suspend fun getAllShelters(): List<ShelterDTO>

    @PUT("shelters/{sheltername}")
    suspend fun updateShelter(@Path("sheltername") sheltername: String, @Body shelter: ShelterDTO): ShelterDTO

    @DELETE("shelters/{shelterId}")
    suspend fun deleteShelter(@Path("shelterId") shelterId: Int): Response<Unit>
}

class RemoteShelterViewModel(context: Context) : ViewModel() {

    private val _shelterUiState = MutableStateFlow<ShelterUiState>(
        ShelterUiState.Loading)
    val shelterUiState: StateFlow<ShelterUiState> = _shelterUiState

    private val _remoteMessageUiState = MutableStateFlow<ShelterRemoteMessageUiState>(
        ShelterRemoteMessageUiState.Loading)
    var remoteMessageUiState: StateFlow<ShelterRemoteMessageUiState> = _remoteMessageUiState.asStateFlow()

    private val _loginMessageUiState = MutableStateFlow<ShelterLoginMessageUiState>(ShelterLoginMessageUiState.Loading)
    val loginMessageUiState: StateFlow<ShelterLoginMessageUiState> = _loginMessageUiState.asStateFlow()

    private val _registerMessageUiState = MutableStateFlow<ShelterRegisterMessageUiState>(ShelterRegisterMessageUiState.Loading)
    val registerMessageUiState: StateFlow<ShelterRegisterMessageUiState> = _registerMessageUiState.asStateFlow()

    private val _createNewAnimalMessageUiState = MutableStateFlow<CreateNewAnimalMessageUiState>(CreateNewAnimalMessageUiState.Loading)
    val createNewAnimalMessageUiState: StateFlow<CreateNewAnimalMessageUiState> = _createNewAnimalMessageUiState.asStateFlow()

    private val _getShelterAnimalsListMessage = MutableStateFlow<GetShelterAnimalsListMessageUiState>(GetShelterAnimalsListMessageUiState.Loading)
    val getShelterAnimalsListMessage: StateFlow<GetShelterAnimalsListMessageUiState> = _getShelterAnimalsListMessage.asStateFlow()

    private val _updateShelterMessageUiState = MutableStateFlow<UpdateShelterMessageUiState>(UpdateShelterMessageUiState.Loading)
    val updateShelterMessageUiState: StateFlow<UpdateShelterMessageUiState> = _updateShelterMessageUiState.asStateFlow()

    private val _deleteShelterMessageUiState = MutableStateFlow<DeleteShelterMessageUiState>(DeleteShelterMessageUiState.Loading)
    val deleteShelterMessageUiState: StateFlow<DeleteShelterMessageUiState> = _deleteShelterMessageUiState.asStateFlow()

    private val _shelter = MutableStateFlow<ShelterDTO?>(null)
    val shelter: StateFlow<ShelterDTO?> = _shelter.asStateFlow()

    private val _animal = MutableStateFlow<AnimalDTO?>(null)
    val animal: StateFlow<AnimalDTO?> = _animal.asStateFlow()

    private val _animalList = MutableStateFlow<List<AnimalDTO>>(emptyList())
    val animalList: StateFlow<List<AnimalDTO>> = _animalList.asStateFlow()
/*
    //ip del emulador 10.0.2.2
    //ip del movil DavidSiles 10.0.22.100
    //ip del movil FioMoncayo 10.118.3.231
    /*val connection = Retrofit.Builder()
        .baseUrl("http://10.0.22.100:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()*/

    private val remoteService = connection.create(RemoteShelterInterface::class.java)
*/
    //RemoteConnection
    private lateinit var remoteService: RemoteShelterInterface

    private val _isServiceInitialized = MutableStateFlow(false)
    val isServiceInitialized: StateFlow<Boolean> = _isServiceInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                remoteService = NetworkModule.createService<RemoteShelterInterface>(context)
                _isServiceInitialized.value = true
                Log.d("RemoteShelterViewModel", "Service initialized")
            } catch (e: Exception) {
                Log.e("RemoteShelterViewModel", "Failed to initialize service: ${e.message}", e)
                _isServiceInitialized.value = false
            }
        }
    }


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

    fun shelterLogin(sheltername: String, password: String, onSuccess: (ShelterDTO?) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = ShelterLoginMessageUiState.Loading
            try {
                val loginRequest = ShelterLoginRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.shelterLogin(loginRequest)
                _shelter.value = shelter
                Log.d("ShelterLogin", "Shelter after login: ${_shelter.value}")
                _loginMessageUiState.value = ShelterLoginMessageUiState.Success(shelter)
                onSuccess(shelter)
            } catch (e: HttpException) {
                Log.e("ShelterLogin", "HTTP error during login: ${e.message}", e)
                val errorMessage = if (e.code() == 400 || e.code() == 401) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    Log.d("ShelterLogin", "Error response: $errorResponse")
                    val errorShelter = errorResponse?.let { Gson().fromJson(it, ShelterDTO::class.java) }
                    errorShelter?.sheltername ?: "Invalid shelter name or password"
                } else {
                    "Failed to login: ${e.message}"
                }
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error(errorMessage)
            } catch (e: Exception) {
                Log.e("ShelterLogin", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = ShelterLoginMessageUiState.Error("Failed to login: ${e.message}")
            }
        }
    }

    fun shelterRegister(sheltername: String, password: String, onSuccess: (ShelterDTO?) -> Unit) {
        viewModelScope.launch {
            _registerMessageUiState.value = ShelterRegisterMessageUiState.Loading
            try {
                val registerRequest = ShelterRegisterRequest(sheltername = sheltername, password = password)
                val shelter = remoteService.shelterRegister(registerRequest)
                _shelter.value = shelter
                Log.d("ShelterRegister", "Shelter after registration: ${_shelter.value}")
                _registerMessageUiState.value = ShelterRegisterMessageUiState.Success(shelter)
                onSuccess(shelter)
            } catch (e: HttpException) {
                Log.e("ShelterRegister", "HTTP error during registration: ${e.message}", e)
                val errorMessage = if (e.code() == 400) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    Log.d("ShelterRegister", "Error response: $errorResponse")
                    val errorShelter = errorResponse?.let { Gson().fromJson(it, ShelterDTO::class.java) }
                    errorShelter?.sheltername ?: "Shelter name already exists"
                } else {
                    "Failed to register: ${e.message}"
                }
                _registerMessageUiState.value = ShelterRegisterMessageUiState.Error(errorMessage)
            } catch (e: Exception) {
                Log.e("ShelterRegister", "Error during registration: ${e.message}", e)
                _registerMessageUiState.value = ShelterRegisterMessageUiState.Error("Failed to register: ${e.message}")
            }
        }
    }

    fun getShelterAnimals(shelterId: Int, onSuccess: (List<AnimalDTO>?) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Loading
            try {
                Log.d("GetShelterAnimals", "Fetching animals for shelterId: $shelterId")
                val animalList = remoteService.getShelterListAnimals(shelterId)
                Log.d("GetShelterAnimals", "Response JSON: ${Gson().toJson(animalList)}")
                Log.d("GetShelterAnimals", "Fetched ${animalList.size} animals")
                _animalList.value = animalList
                _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Success(animalList)
                onSuccess(animalList)
            } catch (e: HttpException) {
                Log.e("GetShelterAnimals", "HTTP error fetching animals: ${e.message}", e)
                val errorMessage = if (e.code() == 400) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    Log.d("GetShelterAnimals", "Error response: $errorResponse")
                    "Invalid shelter ID"
                } else {
                    "Failed to fetch animals: ${e.message}"
                }
                _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            } catch (e: Exception) {
                Log.e("GetShelterAnimals", "Error fetching animals: ${e.message}", e)
                val errorMessage = "Failed to fetch animals: ${e.message}"
                _getShelterAnimalsListMessage.value = GetShelterAnimalsListMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }

    fun createNewAnimal(reiac: Int, name: String, shelterId: Int?, onSuccess: (AnimalDTO?) -> Unit) {
        viewModelScope.launch {
            _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Loading
            try {
                Log.d("CreateAnimal", "Animal data: $reiac  $name  $shelterId")
                val animalDTO = newAnimal(
                  reiac = reiac, 
                  name = name, 
                  shelterId = shelterId)
                Log.d("CreateAnimal", "Animal data: ${animalDTO.name}  ${animalDTO.reiac}")
                val animal = remoteService.shelterCreateNewAnimal(animalDTO)
                Log.d("CreateAnimal", "Animal created successfully: $animal")
                _animal.value = animal
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Success(animal)
                onSuccess(animal)
            } catch (e: HttpException) {
                Log.e("CreateAnimal", "HTTP error during animal creation: ${e.message}", e)
                val errorResponse = e.response()?.errorBody()?.string()
                Log.d("CreateAnimal", "Error response: $errorResponse")
                val errorMessage = if (e.code() == 400) {
                    try {
                        val errorAnimal = errorResponse?.let { Gson().fromJson(it, AnimalDTO::class.java) }
                        errorAnimal?.name ?: errorResponse ?: "Invalid shelter ID or duplicate reiac"
                    } catch (jsonException: Exception) {
                        errorResponse ?: "Invalid request format"
                    }
                } else {
                    "Failed to create animal: ${e.message}"
                }
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Error(errorMessage)
            } catch (e: Exception) {
                Log.e("CreateAnimal", "Error during animal creation: ${e.message}", e)
                _createNewAnimalMessageUiState.value = CreateNewAnimalMessageUiState.Error("Failed to create animal: ${e.message}")
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

    fun updateShelter(
        updatedShelter: ShelterDTO,
        shelter: ShelterDTO,
        onSuccess: (ShelterDTO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _updateShelterMessageUiState.value = UpdateShelterMessageUiState.Loading
            try {
                val newUpdatedShelter = remoteService.updateShelter(
                    sheltername = updatedShelter.sheltername,
                    shelter = updatedShelter
                )
                _updateShelterMessageUiState.value = UpdateShelterMessageUiState.Success(newUpdatedShelter)
                _shelter.value = newUpdatedShelter // Update the shelter state correctly
                onSuccess(newUpdatedShelter)
            } catch (e: HttpException) {
                Log.e("UpdateShelter", "HTTP error during update: ${e.message}", e)
                val errorMessage = when (e.code()) {
                    400 -> "Invalid shelter data"
                    401 -> "Unauthorized: Invalid credentials"
                    404 -> "Shelter not found"
                    else -> "Failed to update shelter: ${e.message}"
                }
                _updateShelterMessageUiState.value = UpdateShelterMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            } catch (e: Exception) {
                Log.e("UpdateShelter", "Error updating shelter: ${e.message}", e)
                val errorMessage = "Failed to update shelter: ${e.message}"
                _updateShelterMessageUiState.value = UpdateShelterMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }

    fun deleteShelter(shelterId: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _deleteShelterMessageUiState.value = DeleteShelterMessageUiState.Loading
            try {
                Log.d("DeleteShelter", "Deleting shelter with ID: $shelterId")
                val response = remoteService.deleteShelter(shelterId)
                if (response.isSuccessful) {
                    val successMessage = "Shelter deleted successfully"
                    Log.d("DeleteShelter", successMessage)
                    _shelter.value = null
                    _deleteShelterMessageUiState.value = DeleteShelterMessageUiState.Success(successMessage)
                    onSuccess(successMessage)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid shelter ID"
                        404 -> "Shelter not found"
                        else -> "Failed to delete shelter: ${response.message()}"
                    }
                    Log.e("DeleteShelter", "HTTP error during deletion: ${response.message()}, Code: ${response.code()}")
                    _deleteShelterMessageUiState.value = DeleteShelterMessageUiState.Error(errorMessage)
                    onFailure(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("DeleteShelter", "Error during deletion: ${e.message}", e)
                val errorMessage = "Failed to delete shelter: ${e.message}"
                _deleteShelterMessageUiState.value = DeleteShelterMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }
}