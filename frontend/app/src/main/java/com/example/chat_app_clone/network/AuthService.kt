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
            val loginRequest = LoginRequest(email, password)
            val response = authApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "Login successful for user: ${body.user?.username}")
                    Result.success(body)
                } else {
                    Log.e(tag, "Login response body is null")
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Login failed with code ${response.code()}: $errorBody")
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Login exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val registerRequest = RegisterRequest(username, email, password)
            val response = authApi.register(registerRequest)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "Registration successful for user: ${body.user?.username}")
                    Result.success(body)
                } else {
                    Log.e(tag, "Register response body is null")
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Registration failed with code ${response.code()}: $errorBody")
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Register exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
