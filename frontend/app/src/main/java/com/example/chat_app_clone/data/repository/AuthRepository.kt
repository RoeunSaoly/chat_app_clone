package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.network.AuthService
import com.example.chat_app_clone.network.model.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val authService: AuthService) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return authService.login(email, password)
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return authService.register(username, email, password)
    }
}
