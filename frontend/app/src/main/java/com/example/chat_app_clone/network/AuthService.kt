package com.example.chat_app_clone.network

import android.util.Log
import com.example.chat_app_clone.network.model.AuthResponse
import com.example.chat_app_clone.network.model.LoginRequest
import com.example.chat_app_clone.network.model.RegisterRequest

class AuthService {
    private val authApi = RetrofitClient.authApi
    private val tag = "AuthService"
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            Log.d(tag, "login -> HTTP ${response.code()}: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Login failed"))
            }
        } catch (e: Exception) {
            Log.e(tag, "login failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(username, email, password))
            Log.d(tag, "register -> HTTP ${response.code()}: ${response.body() ?: response.errorBody()?.string()}")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Log.e(tag, "register failed", e)
            Result.failure(e)
        }
    }
}
