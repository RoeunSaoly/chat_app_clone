package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.UserResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("api/profile/users")
    suspend fun getUsers(): Response<UsersApiResponse>
}

data class UsersApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<UserResponse>? = null,
    @SerializedName("error") val error: String? = null
)
