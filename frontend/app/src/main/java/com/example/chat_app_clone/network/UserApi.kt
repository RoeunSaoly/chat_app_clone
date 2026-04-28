package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.UserResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserApi {
    @GET("api/users")
    suspend fun searchUsers(@Query("search") search: String? = null): Response<UsersApiResponse>

    @GET("api/users/profile")
    suspend fun getProfile(): Response<UserResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserResponse>
}

data class UsersApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<UserResponse>? = null,
    @SerializedName("error") val error: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("username") val username: String? = null,
    @SerializedName("avatar") val avatar: String? = null
)
