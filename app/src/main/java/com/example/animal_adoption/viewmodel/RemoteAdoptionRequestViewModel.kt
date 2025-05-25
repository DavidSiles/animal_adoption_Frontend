package com.example.animal_adoption.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.AdoptionRequestDTO
import com.example.animal_adoption.model.CreateAdoptionRequestDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


sealed interface AdoptionRequestUiState {
    data class Success(val requests: List<AdoptionRequestDTO>) : AdoptionRequestUiState
    data class OneRequest(val request: AdoptionRequestDTO) : AdoptionRequestUiState
    object Error : AdoptionRequestUiState
    object Loading : AdoptionRequestUiState
    object Idle : AdoptionRequestUiState
}

sealed interface CreateAdoptionRequestUiState {
    data class Success(val request: AdoptionRequestDTO) : CreateAdoptionRequestUiState
    data class Error(val message: String) : CreateAdoptionRequestUiState
    object Loading : CreateAdoptionRequestUiState
    object Idle : CreateAdoptionRequestUiState
}

sealed interface UpdateAdoptionRequestStatusUiState {
    data class Success(val request: AdoptionRequestDTO) : UpdateAdoptionRequestStatusUiState
    data class Error(val message: String) : UpdateAdoptionRequestStatusUiState
    object Loading : UpdateAdoptionRequestStatusUiState
    object Idle : UpdateAdoptionRequestStatusUiState
}

sealed interface DeleteAdoptionRequestUiState {
    data class Success(val message: String) : DeleteAdoptionRequestUiState
    data class Error(val message: String) : DeleteAdoptionRequestUiState
    object Loading : DeleteAdoptionRequestUiState
    object Idle : DeleteAdoptionRequestUiState
}


class RemoteAdoptionRequestViewModel(context: Context) : ViewModel() {

    private val _adoptionRequestUiState = MutableStateFlow<AdoptionRequestUiState>(AdoptionRequestUiState.Idle)
    val adoptionRequestUiState: StateFlow<AdoptionRequestUiState> = _adoptionRequestUiState.asStateFlow()

    private val _createAdoptionRequestUiState = MutableStateFlow<CreateAdoptionRequestUiState>(CreateAdoptionRequestUiState.Idle)
    val createAdoptionRequestUiState: StateFlow<CreateAdoptionRequestUiState> = _createAdoptionRequestUiState.asStateFlow()

    private val _updateAdoptionRequestStatusUiState = MutableStateFlow<UpdateAdoptionRequestStatusUiState>(UpdateAdoptionRequestStatusUiState.Idle)
    val updateAdoptionRequestStatusUiState: StateFlow<UpdateAdoptionRequestStatusUiState> = _updateAdoptionRequestStatusUiState.asStateFlow()

    private val _deleteAdoptionRequestUiState = MutableStateFlow<DeleteAdoptionRequestUiState>(DeleteAdoptionRequestUiState.Idle)
    val deleteAdoptionRequestUiState: StateFlow<DeleteAdoptionRequestUiState> = _deleteAdoptionRequestUiState.asStateFlow()

    private lateinit var adoptionRequestService: RemoteAdoptionRequestInterface

    private val _isServiceInitialized = MutableStateFlow(false)
    val isServiceInitialized: StateFlow<Boolean> = _isServiceInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                adoptionRequestService = NetworkModule.createService<RemoteAdoptionRequestInterface>(context)
                _isServiceInitialized.value = true
                Log.d("AdoptionRequestVM", "AdoptionRequestService inicializado correctamente.")
            } catch (e: Exception) {
                Log.e("AdoptionRequestVM", "Error al inicializar AdoptionRequestService: ${e.message}", e)
                _isServiceInitialized.value = false
            }
        }
    }


    /**
     * Crea una nueva solicitud de adopción.
     */
    fun createAdoptionRequest(userId: Int, animalId: Int) {
        viewModelScope.launch {
            _createAdoptionRequestUiState.value = CreateAdoptionRequestUiState.Loading
            try {
                val requestBody = CreateAdoptionRequestDTO(userId = userId, animalId = animalId)
                val newRequest = adoptionRequestService.createAdoptionRequest(requestBody)
                _createAdoptionRequestUiState.value = CreateAdoptionRequestUiState.Success(newRequest)
                Log.d("AdoptionRequestVM", "Solicitud de adopción creada: $newRequest")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = "Error HTTP al crear solicitud: ${e.code()} - ${errorBody ?: e.message()}"
                Log.e("AdoptionRequestVM", errorMessage, e)
                _createAdoptionRequestUiState.value = CreateAdoptionRequestUiState.Error(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error al crear solicitud de adopción: ${e.message}"
                Log.e("AdoptionRequestVM", errorMessage, e)
                _createAdoptionRequestUiState.value = CreateAdoptionRequestUiState.Error(errorMessage)
            }
        }
    }

    /**
     * Obtiene una solicitud de adopción por su ID.
     */
    fun getAdoptionRequestById(id: Int) {
        viewModelScope.launch {
            _adoptionRequestUiState.value = AdoptionRequestUiState.Loading
            try {
                val request = adoptionRequestService.getAdoptionRequestById(id)
                _adoptionRequestUiState.value = AdoptionRequestUiState.OneRequest(request)
            } catch (e: Exception) {
                Log.e("AdoptionRequestVM", "Error al obtener solicitud de adopción por ID: ${e.message}", e)
                _adoptionRequestUiState.value = AdoptionRequestUiState.Error
            }
        }
    }

    /**
     * Obtiene todas las solicitudes de adopción para un refugio específico.
     */
    fun getAdoptionRequestsByShelterId(shelterId: Int) {
        viewModelScope.launch {
            _adoptionRequestUiState.value = AdoptionRequestUiState.Loading
            try {
                Log.d("AdoptionRequestVM", "Solicitudes para refugio $shelterId")
                val requests = adoptionRequestService.getAdoptionRequestsByShelterId(shelterId)
                _adoptionRequestUiState.value = AdoptionRequestUiState.Success(requests)
                Log.d("AdoptionRequestVM", "Solicitudes para refugio $shelterId: $requests")
            } catch (e: Exception) {
                Log.e("AdoptionRequestVM", "Error al obtener solicitudes de adopción por ID de refugio: ${e.message}", e)
                _adoptionRequestUiState.value = AdoptionRequestUiState.Error
            }
        }
    }

    /**
     * Obtiene todas las solicitudes de adopción para un usuario específico.
     */
    fun getAdoptionRequestsByUserId(userId: Int) {
        viewModelScope.launch {
            _adoptionRequestUiState.value = AdoptionRequestUiState.Loading
            try {
                val requests = adoptionRequestService.getAdoptionRequestsByUserId(userId)
                _adoptionRequestUiState.value = AdoptionRequestUiState.Success(requests)
                Log.d("AdoptionRequestVM", "Solicitudes para usuario $userId: $requests")
            } catch (e: Exception) {
                Log.e("AdoptionRequestVM", "Error al obtener solicitudes de adopción por ID de usuario: ${e.message}", e)
                _adoptionRequestUiState.value = AdoptionRequestUiState.Error
            }
        }
    }

    /**
     * Actualiza el estado de una solicitud de adopción.
     */
    fun updateAdoptionRequestStatus(requestId: Int, newStatus: String) {
        viewModelScope.launch {
            _updateAdoptionRequestStatusUiState.value = UpdateAdoptionRequestStatusUiState.Loading
            try {
                Log.d("AdoptionRequestVM", "Estado de solicitud $requestId actualizado a $newStatus")
                val updatedRequest = adoptionRequestService.updateAdoptionRequestStatus(newStatus = newStatus, requestId = requestId)
                _updateAdoptionRequestStatusUiState.value = UpdateAdoptionRequestStatusUiState.Success(updatedRequest)
                Log.d("AdoptionRequestVM", "Estado de solicitud $requestId actualizado a $newStatus: $updatedRequest")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = "Error HTTP al actualizar estado: ${e.code()} - ${errorBody ?: e.message()}"
                Log.e("AdoptionRequestVM", errorMessage, e)
                _updateAdoptionRequestStatusUiState.value = UpdateAdoptionRequestStatusUiState.Error(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error al actualizar estado de solicitud de adopción: ${e.message}"
                Log.e("AdoptionRequestVM", errorMessage, e)
                _updateAdoptionRequestStatusUiState.value = UpdateAdoptionRequestStatusUiState.Error(errorMessage)
            }
        }
    }

    /**
     * Elimina una solicitud de adopción.
     */
    fun deleteAdoptionRequest(id: Int) {
        viewModelScope.launch {
            _deleteAdoptionRequestUiState.value = DeleteAdoptionRequestUiState.Loading
            try {
                Log.d("AdoptionRequestVM", "Eliminando solicitud de adopción con ID: $id")
                val response = adoptionRequestService.deleteAdoptionRequest(id)
                if (response.isSuccessful) {
                    val successMessage = "Solicitud de adopción eliminada con éxito."
                    Log.d("AdoptionRequestVM", successMessage)
                    _deleteAdoptionRequestUiState.value = DeleteAdoptionRequestUiState.Success(successMessage)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "ID de solicitud inválido."
                        404 -> "Solicitud no encontrada."
                        else -> "Fallo al eliminar solicitud: ${response.message()}"
                    }
                    Log.e("AdoptionRequestVM", "Error HTTP al eliminar solicitud: ${response.message()}, Código: ${response.code()}")
                    _deleteAdoptionRequestUiState.value = DeleteAdoptionRequestUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AdoptionRequestVM", "Error al eliminar solicitud de adopción: ${e.message}", e)
                _deleteAdoptionRequestUiState.value = DeleteAdoptionRequestUiState.Error("Fallo al eliminar solicitud: ${e.message}")
            }
        }
    }

    fun resetCreateAdoptionRequestUiState() {
        _createAdoptionRequestUiState.value = CreateAdoptionRequestUiState.Idle
    }
    fun resetUpdateAdoptionRequestStatusUiState() {
        _updateAdoptionRequestStatusUiState.value = UpdateAdoptionRequestStatusUiState.Idle
    }
    fun resetDeleteAdoptionRequestUiState() {
        _deleteAdoptionRequestUiState.value = DeleteAdoptionRequestUiState.Idle
    }
    fun resetAdoptionRequestUiState() {
        _adoptionRequestUiState.value = AdoptionRequestUiState.Idle
    }
}