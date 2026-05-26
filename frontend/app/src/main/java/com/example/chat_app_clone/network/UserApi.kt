package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.GenericApiResponse
import com.example.chat_app_clone.network.model.UserResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/users")
    suspend fun searchUsers(@Query("search") search: String? = null): Response<UsersApiResponse>

    @GET("api/users/profile")
    suspend fun getProfile(): Response<UserResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserResponse>

    // Friend related endpoints
    @GET("api/friends/recommended")
    suspend fun getRecommendedFriends(): Response<UsersApiResponse>

    @GET("api/friends/requests")
    suspend fun getFriendRequests(): Response<UsersApiResponse>

    @GET("api/friends")
    suspend fun getFriends(): Response<UsersApiResponse>

    @POST("api/friends/request/{userId}")
    suspend fun sendFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @POST("api/friends/accept/{userId}")
    suspend fun acceptFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @POST("api/friends/reject/{userId}")
    suspend fun rejectFriendRequest(@Path("userId") userId: Long): Response<GenericApiResponse>

    @DELETE("api/friends/{userId}")
    suspend fun unfriend(@Path("userId") userId: Long): Response<GenericApiResponse>
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
