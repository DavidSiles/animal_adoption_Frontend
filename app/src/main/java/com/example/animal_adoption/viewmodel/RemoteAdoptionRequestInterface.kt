// com.example.animal_adoption.viewmodel.RemoteAdoptionRequestInterface.kt
package com.example.animal_adoption.viewmodel

import com.example.animal_adoption.model.AdoptionRequestDTO
import com.example.animal_adoption.model.CreateAdoptionRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

interface RemoteAdoptionRequestInterface {

    @POST("adoption_requests")
    suspend fun createAdoptionRequest(@Body request: CreateAdoptionRequestDTO): AdoptionRequestDTO

    @GET("adoption_requests/{id}")
    suspend fun getAdoptionRequestById(@Path("id") id: Int): AdoptionRequestDTO

    @GET("adoption_requests/shelters/{shelterId}")
    suspend fun getAdoptionRequestsByShelterId(@Path("shelterId") shelterId: Int): List<AdoptionRequestDTO>

    @GET("adoption_requests/users/{userId}")
    suspend fun getAdoptionRequestsByUserId(@Path("userId") userId: Int): List<AdoptionRequestDTO>

    @PUT("adoption_requests/{newStatus}")
    suspend fun updateAdoptionRequestStatus(
        @Path("newStatus") newStatus: String,
        @Body requestId: Int
    ): AdoptionRequestDTO

    @DELETE("adoption_requests/{id}")
    suspend fun deleteAdoptionRequest(@Path("id") id: Int): Response<Unit>
}