package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    fun refreshSync(@Body request: RefreshRequest): retrofit2.Call<AuthResponse>
}
