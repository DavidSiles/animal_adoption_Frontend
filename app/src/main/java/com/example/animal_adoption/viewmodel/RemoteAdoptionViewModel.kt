package com.example.animal_adoption.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animal_adoption.model.AdoptionRequestDTO
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
import com.example.animal_adoption.model.ShelterDTO
import com.example.animal_adoption.model.UserDTO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


interface RemoteAdoptionInterface {

//    //endpoint
    @GET("adoption_requests/users/{userId}")
    suspend fun getUserAdoptionRequests(@Path("userId") userId: Int): List<AdoptionRequestDTO>

    @GET("adoption_requests/shelters/{shelterId}")
    suspend fun getShelterAdoptionRequests(@Path("shelterId") shelterId: Int): List<AdoptionRequestDTO>

    @GET("animal/name/{name}")
    suspend fun findAnimalByName(@Path("name") name: String): AnimalDTO

    @GET("animal/shelter/{shelterId}")
    suspend fun findAnimalsByShelter(@Path("shelterId") shelterId: Int): List<AnimalDTO>

}

    sealed interface AdoptionRequestUiState {
        data class Success(val requests: List<AdoptionRequestDTO>) : AdoptionRequestUiState
        object Error : AdoptionRequestUiState
        object Loading : AdoptionRequestUiState
    }
    sealed interface AnimalSearchUiState {
        data class Success(val animals: List<AnimalDTO>) : AnimalSearchUiState
        object Error : AnimalSearchUiState
        object Loading : AnimalSearchUiState
    }


    //implementacion
    class RemoteAdoptionViewModel(context: Context) : ViewModel() {

        private val _adoptionUiState = MutableStateFlow<AdoptionRequestUiState>(AdoptionRequestUiState.Loading)
        val adoptionUiState: StateFlow<AdoptionRequestUiState> = _adoptionUiState

        private lateinit var adoptionService: RemoteAdoptionInterface

        private val _isServiceInitialized = MutableStateFlow(false)
        val isServiceInitialized: StateFlow<Boolean> = _isServiceInitialized.asStateFlow()

        private val _statusFilter = MutableStateFlow<String?>(null)
        val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery

        private var currentUser: UserDTO? = null
        private var currentShelter: ShelterDTO? = null

        private val _animalSearchState = MutableStateFlow<AnimalSearchUiState>(AnimalSearchUiState.Loading)
        val animalSearchState: StateFlow<AnimalSearchUiState> = _animalSearchState




        init {
            viewModelScope.launch {
                try {
                    adoptionService = NetworkModule.createService<RemoteAdoptionInterface>(context)
                    _isServiceInitialized.value = true
                    Log.d("RemoteAdoptionVM", "Service initialized")
                } catch (e: Exception) {
                    Log.e("RemoteAdoptionVM", "Error initializing service: ${e.message}")
                    _isServiceInitialized.value = false
                }
            }
        }

        fun getUserRequests(userId: Int) {
            viewModelScope.launch {
                _adoptionUiState.value = AdoptionRequestUiState.Loading
                try {
                    val result = adoptionService.getUserAdoptionRequests(userId)
                    _adoptionUiState.value = AdoptionRequestUiState.Success(result)
                } catch (e: Exception) {
                    Log.e("RemoteAdoptionVM", "Error fetching: ${e.message}")
                    _adoptionUiState.value = AdoptionRequestUiState.Error
                }
            }
        }


        fun getShelterRequests(shelterId: Int) {
            viewModelScope.launch {
                _adoptionUiState.value = AdoptionRequestUiState.Loading
                try {
                    val result = adoptionService.getShelterAdoptionRequests(shelterId)
                    _adoptionUiState.value = AdoptionRequestUiState.Success(result)
                } catch (e: Exception) {
                    Log.e("RemoteAdoptionVM", "Error fetching for shelter: ${e.message}")
                    _adoptionUiState.value = AdoptionRequestUiState.Error
                }
            }
        }


        fun getUserRequestsFiltered(userId: Int, status: String) {
            viewModelScope.launch {
                _adoptionUiState.value = AdoptionRequestUiState.Loading
                try {
                    val allRequests = adoptionService.getUserAdoptionRequests(userId)
                    val filtered = allRequests.filter { it.status.equals(status, ignoreCase = true) }
                    _adoptionUiState.value = AdoptionRequestUiState.Success(filtered)
                } catch (e: Exception) {
                    _adoptionUiState.value = AdoptionRequestUiState.Error
                }
            }
        }

        fun getShelterRequestsFiltered(shelterId: Int, status: String) {
            viewModelScope.launch {
                _adoptionUiState.value = AdoptionRequestUiState.Loading
                try {
                    val allRequests = adoptionService.getShelterAdoptionRequests(shelterId)
                    val filtered = allRequests.filter { it.status.equals(status, ignoreCase = true) }
                    _adoptionUiState.value = AdoptionRequestUiState.Success(filtered)
                } catch (e: Exception) {
                    Log.e("RemoteAdoptionVM", "Error filtering for shelter: ${e.message}")
                    _adoptionUiState.value = AdoptionRequestUiState.Error
                }
            }
        }


        fun setStatusFilter(user: UserDTO?, shelter: ShelterDTO?, status: String?) {
            _statusFilter.value = status
            currentUser = user
            currentShelter = shelter

            when {
                user != null -> {
                    if (status == null || status == "all") {
                        getUserRequests(user.id)
                    } else {
                        getUserRequestsFiltered(user.id, status)
                    }
                }
                shelter != null -> {
                    if (status == null || status == "all") {
                        getShelterRequests(shelter.id)
                    } else {
                        getShelterRequestsFiltered(shelter.id, status)
                    }
                }
                else -> {
                    Log.e("RemoteAdoptionVM", "Neither user nor shelter provided")
                }
            }
        }

//        fun setSearchQuery(query: String) {
//            _searchQuery.value = query
//            loadAndFilterRequests(currentUser, currentShelter)
//        }
//
//        fun loadAndFilterRequests(user: UserDTO?, shelter: ShelterDTO?) {
//            viewModelScope.launch {
//                _adoptionUiState.value = AdoptionRequestUiState.Loading
//                try {
//                    val allRequests = when {
//                        user != null -> adoptionService.getUserAdoptionRequests(user.id)
//                        shelter != null -> adoptionService.getShelterAdoptionRequests(shelter.id)
//                        else -> emptyList()
//                    }
//
//                    val status = _statusFilter.value
//                    val query = _searchQuery.value.lowercase()
//
//                    val filtered = allRequests.filter { request ->
//                        (status == null || status == "all" || request.status.equals(status, ignoreCase = true)) &&
//                                (query.isBlank() || request.animalId.toString().contains(query))
//                    }
//
//
//                    _adoptionUiState.value = AdoptionRequestUiState.Success(filtered)
//                } catch (e: Exception) {
//                    Log.e("RemoteAdoptionVM", "Error in loadAndFilter: ${e.message}")
//                    _adoptionUiState.value = AdoptionRequestUiState.Error
//                }
//            }
//        }

        fun setSearchQuery(query: String) {
            _searchQuery.value = query
            searchAnimal(query)
        }


        fun searchAnimal(query: String) {
            viewModelScope.launch {
                _animalSearchState.value = AnimalSearchUiState.Loading
                try {
                    if (query.toIntOrNull() != null) {
                        // Si es un número => lo interpretamos como shelterId
                        val result = adoptionService.findAnimalsByShelter(query.toInt())
                        _animalSearchState.value = AnimalSearchUiState.Success(result)
                    } else {
                        // Si es texto => lo interpretamos como nombre de animal
                        val animal = adoptionService.findAnimalByName(query)
                        _animalSearchState.value = AnimalSearchUiState.Success(listOf(animal))
                    }
                } catch (e: Exception) {
                    Log.e("RemoteAdoptionVM", "Error buscando animal: ${e.message}")
                    _animalSearchState.value = AnimalSearchUiState.Error
                }
            }
        }







    }



