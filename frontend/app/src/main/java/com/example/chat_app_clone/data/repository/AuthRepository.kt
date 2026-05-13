package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.network.AuthService
import com.example.chat_app_clone.network.model.AuthResponse

class AuthRepository(private val authService: AuthService = AuthService()) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return authService.login(email, password)
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return authService.register(username, email, password)
    }
}
