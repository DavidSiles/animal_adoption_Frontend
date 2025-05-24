package com.example.animal_adoption.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import com.example.animal_adoption.model.UserLoginRequest
import com.example.animal_adoption.model.UserRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<UserDTO>) : RemoteMessageUiState
    object Error : RemoteMessageUiState
    object Loading : RemoteMessageUiState
}

sealed interface UserUiState {
    data class Success(val users: List<UserDTO>) : UserUiState
    data class OneUser(val user: UserDTO) : UserUiState
    object Error : UserUiState
    object Loading : UserUiState
}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: UserDTO) : LoginMessageUiState

    object Error : LoginMessageUiState
    object Loading : LoginMessageUiState
}

sealed interface DeleteUserMessageUiState {
    data class Success(val message: String) : DeleteUserMessageUiState
    data class Error(val message: String) : DeleteUserMessageUiState
    object Loading : DeleteUserMessageUiState
}

sealed interface UpdateUserMessageUiState {
    data class Success(val user: UserDTO) : UpdateUserMessageUiState
    data class Error(val message: String) : UpdateUserMessageUiState
    object Loading : UpdateUserMessageUiState
}

interface RemoteUserInterface {

    @GET("users/index")
    suspend fun getRemoteUser(): List<UserDTO>

    @GET("users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): UserDTO

    @POST("users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): UserDTO

    @POST("users/create")
    suspend fun register(@Body registerRequest: UserRegisterRequest): UserDTO

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    @PUT("users/{username}")
    suspend fun updateUser(@Path("username") username: String, @Body user: UserDTO): UserDTO
}

class RemoteUserViewModel(context: Context) : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<com.example.animal_adoption.viewmodel.RemoteMessageUiState>(
        com.example.animal_adoption.viewmodel.RemoteMessageUiState.Loading)
    var remoteMessageUiState: StateFlow<com.example.animal_adoption.viewmodel.RemoteMessageUiState> = _remoteMessageUiState

    private val _deleteUserMessageUiState = MutableStateFlow<DeleteUserMessageUiState>(DeleteUserMessageUiState.Loading)
    val deleteUserMessageUiState: StateFlow<DeleteUserMessageUiState> = _deleteUserMessageUiState.asStateFlow()

    private val _loginMessageUiState = MutableStateFlow<com.example.animal_adoption.viewmodel.LoginMessageUiState>(
        com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading)
    var loginMessageUiState: StateFlow<com.example.animal_adoption.viewmodel.LoginMessageUiState> = _loginMessageUiState

    private val _updateUserMessageUiState = MutableStateFlow<UpdateUserMessageUiState>(UpdateUserMessageUiState.Loading)
    val updateUserMessageUiState: StateFlow<UpdateUserMessageUiState> = _updateUserMessageUiState.asStateFlow()

    private val _user = MutableStateFlow<UserDTO?>(null)
    val user: StateFlow<UserDTO?> = _user.asStateFlow()
/*
    //ip del emulador 10.0.2.2.
    //ip del movil DavidSiles 10.0.22.100
    //ip del movil FioMoncayo 10.118.3.231
    val connection = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //private val remoteService = connection.create(com.example.animal_adoption.viewmodel.RemoteUserInterface::class.java)
*/
    //RemoteConnection

    private lateinit var remoteService: RemoteUserInterface

    private val _isServiceInitialized = MutableStateFlow(false)
    val isServiceInitialized: StateFlow<Boolean> = _isServiceInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                remoteService = NetworkModule.createService<RemoteUserInterface>(context)
                _isServiceInitialized.value = true
                Log.d("RemoteUserViewModel", "Service initialized")
            } catch (e: Exception) {
                Log.e("RemoteUserViewModel", "Failed to initialize service: ${e.message}", e)
                _isServiceInitialized.value = false
            }
        }
    }

    private val _id = MutableStateFlow<Int?>(null)
    val id: StateFlow<Int?> = _id

    // Get all users
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
    fun login(username: String, password: String, onSuccess: (UserDTO) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading
            try {
                val loginRequest = UserLoginRequest(username = username, password = password)
                val user = remoteService.login(loginRequest)
                _id.value = user.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Success(user)

                // Pasamos el nurseId al callback de éxito
                onSuccess(user)
            } catch (e: Exception) {
                Log.e("Login", "Error during login: ${e.message}", e)
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Error
            }
        }
    }

    // Registro
    fun register(username: String, password: String, onSuccess: (UserDTO) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Loading
            try {
                val registerRequest = UserRegisterRequest(username = username, password = password)
                val user = remoteService.register(registerRequest)
                _id.value = user.id  // Guardamos el ID del enfermero
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Success(user)

                // Pasamos el nurseId al callback de éxito
                onSuccess(user)
            } catch (e: Exception) {
                Log.e("Register", "Error during registration: ${e.message}", e)
                _loginMessageUiState.value = com.example.animal_adoption.viewmodel.LoginMessageUiState.Error
            }
        }
    }

    // Función para obtener los detalles del enfermero por ID
    fun getUserByUsername(username: String, onSuccess: (UserDTO) -> Unit, onFailure: () -> Unit) {
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

//    fun updatedUser(
//        updatedUser: UserDTO,
//        user: UserDTO,
//        onSuccess: (UserDTO) -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            _updateUserMessageUiState.value = UpdateUserMessageUiState.Loading
//            try {
//                val newUpdatedUser = remoteService.updateUser(
//                    username = updatedUser.username,
//                    user = updatedUser
//                )
//                _updateUserMessageUiState.value = UpdateUserMessageUiState.Success(newUpdatedUser)
//                _user.value = newUpdatedUser // Update the shelter state correctly
//                onSuccess(newUpdatedUser)
//            } catch (e: HttpException) {
//                Log.e("UpdateUser", "HTTP error during update: ${e.message}", e)
//                val errorMessage = when (e.code()) {
//                    400 -> "Invalid user data  ${updatedUser?.username}"
//                    401 -> "Unauthorized: Invalid credentials ${updatedUser?.username}"
//                    404 -> "User not found  ${updatedUser?.username} Id: ${user?.id ?: "N/A"}"
//                    else -> "Failed to update user: ${e.message}"
//                }
//                _updateUserMessageUiState.value = UpdateUserMessageUiState.Error(errorMessage)
//                onFailure(errorMessage)
//            } catch (e: Exception) {
//                Log.e("UpdateUser", "Error updating user: ${e.message}", e)
//                val errorMessage = "Failed to update user: ${e.message}"
//                _updateUserMessageUiState.value = UpdateUserMessageUiState.Error(errorMessage)
//                onFailure(errorMessage)
//            }
//        }
//    }
fun updatedUser(
    updatedUser: UserDTO,
    user: UserDTO, // Original user with the existing username
    onSuccess: (UserDTO) -> Unit,
    onFailure: (String) -> Unit
) {
    viewModelScope.launch {
        _updateUserMessageUiState.value = UpdateUserMessageUiState.Loading
        try {
            val newUpdatedUser = remoteService.updateUser(
                username = user.username, // Use the original username
                user = updatedUser // Send the updated user data
            )
            _updateUserMessageUiState.value = UpdateUserMessageUiState.Success(newUpdatedUser)
            _user.value = newUpdatedUser // Update the user state
            onSuccess(newUpdatedUser)
        } catch (e: HttpException) {
            Log.e("UpdateUser", "HTTP error during update: ${e.message}", e)
            val errorMessage = when (e.code()) {
                400 -> "Invalid user data ${updatedUser?.username}"
                401 -> "Unauthorized: Invalid credentials ${updatedUser?.username}"
                404 -> "User not found ${user.username} Id: ${user?.id ?: "N/A"}"
                else -> "Failed to update user: ${e.message}"
            }
            _updateUserMessageUiState.value = UpdateUserMessageUiState.Error(errorMessage)
            onFailure(errorMessage)
        } catch (e: Exception) {
            Log.e("UpdateUser", "Error updating user: ${e.message}", e)
            val errorMessage = "Failed to update user: ${e.message}"
            _updateUserMessageUiState.value = UpdateUserMessageUiState.Error(errorMessage)
            onFailure(errorMessage)
        }
    }
}

    fun deleteUser(userId: Int, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _deleteUserMessageUiState.value = DeleteUserMessageUiState.Loading
            try {
                Log.d("DeleteUser", "Deleting user with ID: $userId")
                val response = remoteService.deleteUser(userId)
                if (response.isSuccessful) {
                    val successMessage = "User deleted successfully"
                    Log.d("DeleteUser", successMessage)
                    _deleteUserMessageUiState.value = DeleteUserMessageUiState.Success(successMessage)
                    onSuccess(successMessage)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid user ID"
                        404 -> "User not found"
                        else -> "Failed to delete user: ${response.message()}"
                    }
                    Log.e("DeleteUser", "HTTP error during deletion: ${response.message()}, Code: ${response.code()}")
                    _deleteUserMessageUiState.value = DeleteUserMessageUiState.Error(errorMessage)
                    onFailure(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("DeleteShelter", "Error during deletion: ${e.message}", e)
                val errorMessage = "Failed to delete user: ${e.message}"
                _deleteUserMessageUiState.value = DeleteUserMessageUiState.Error(errorMessage)
                onFailure(errorMessage)
            }
        }
    }

}

