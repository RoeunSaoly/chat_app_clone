package com.example.chat_app_clone.network

import android.util.Log
import com.example.chat_app_clone.network.model.AuthResponse
import com.example.chat_app_clone.network.model.LoginRequest
import com.example.chat_app_clone.network.model.RegisterRequest

class AuthService {
    private val authApi = RetrofitClient.authApi
    private val tag = "AuthService"
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return Result.success(
            AuthResponse(
                success = true,
                accessToken = "dummy_token",
                refreshToken = "dummy_refresh",
                user = com.example.chat_app_clone.data.SampleData.currentUser
            )
        )
    }
    
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return Result.success(
            AuthResponse(
                success = true,
                accessToken = "dummy_token",
                refreshToken = "dummy_refresh",
                user = com.example.chat_app_clone.data.SampleData.currentUser
            )
        )
    }
}
