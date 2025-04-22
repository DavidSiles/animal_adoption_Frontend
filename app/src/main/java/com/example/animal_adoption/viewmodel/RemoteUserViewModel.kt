package com.example.animal_adoption.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.User
import com.example.animal_adoption.model.UserLoginRequest
import com.example.animal_adoption.model.UserRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<User>) : RemoteMessageUiState

    object Error : RemoteMessageUiState
    object Loading : RemoteMessageUiState
}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: User) : LoginMessageUiState

    object Error : LoginMessageUiState
    object Loading : LoginMessageUiState
}

interface RemoteUserInterface {
    @GET("user/index")
    suspend fun getRemoteUser(): List<User>

    @GET("user/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): User

    @POST("user/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): User

    @POST("user/new")
    suspend fun register(@Body registerRequest: UserRegisterRequest): User
}

class RemoteUserViewModel : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<com.example.animal_adoption.viewmodel.RemoteMessageUiState>(
        com.example.animal_adoption.viewmodel.RemoteMessageUiState.Loading)
    var remoteMessageUiState: StateFlow<com.example.animal_adoption.viewmodel.RemoteMessageUiState> = _remoteMessageUiState

    private val _loginMessageUiState = MutableStateFlow<com.example.animal_adoption.viewmodel.LoginMessageUiState>(
        com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading)
    var loginMessageUiState: StateFlow<com.example.animal_adoption.viewmodel.LoginMessageUiState> = _loginMessageUiState

    val connection = Retrofit.Builder()
        .baseUrl("http://192.168.2.1:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val remoteService = connection.create(com.example.animal_adoption.viewmodel.RemoteUserInterface::class.java)

    private val _id = MutableStateFlow<Integer?>(null)
    val id: StateFlow<Integer?> = _id

    // Get all Nurses
    fun getRemoteUser() {
        viewModelScope.launch {
            _remoteMessageUiState.value = com.example.animal_adoption.viewmodel.RemoteMessageUiState.Loading
            try {
                Log.d("GetUser", "Fetching nurses from server...")
                val response = remoteService.getRemoteUser()
                Log.d("GetUser", "Successfully fetched nurses: $response")
                _remoteMessageUiState.value = com.example.animal_adoption.viewmodel.RemoteMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("GetUser", "Error fetching nurses: ${e.message}", e)
                _remoteMessageUiState.value = com.example.animal_adoption.viewmodel.RemoteMessageUiState.Error
            }
        }
    }

    // Login
    fun login(username: String, password: String, onSuccess: (Integer) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading
            try {
                val loginRequest = UserLoginRequest(username = username, password = password)
                val user = remoteService.login(loginRequest)
                _id.value = user.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Success(user)

                // Pasamos el nurseId al callback de éxito
                onSuccess(user.id)
            } catch (e: Exception) {
                Log.e("Login", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Error
            }
        }
    }

    // Registro
    fun register(username: String, password: String, onSuccess: (Integer) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading
            try {
                val registerRequest = UserRegisterRequest(username = username, password = password)
                val nurse = remoteService.register(registerRequest)
                _id.value = nurse.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Success(nurse)

                // Pasamos el nurseId al callback de éxito
                onSuccess(nurse.id)
            } catch (e: Exception) {
                Log.e("Register", "Error during registration: ${e.message}", e)
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Error
            }
        }
    }

    // Función para obtener los detalles del enfermero por ID
    fun getUserByUsername(username: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val nurse = remoteService.getUserByUsername(username)  // Realizamos la petición GET
                onSuccess(nurse)  // Pasamos el objeto Nurse a la UI
            } catch (e: Exception) {
                Log.e("GetNurseById", "Error fetching nurse: ${e.message}", e)
                onFailure()  // Llamamos al callback de error si ocurre un fallo
            }
        }
    }
}