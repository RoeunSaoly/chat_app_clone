package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.AuthResponse
import com.example.chat_app_clone.network.model.LoginRequest
import com.example.chat_app_clone.network.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}
