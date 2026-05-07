package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.UpdateProfileRequest

class UserRepository {
    private val userApi = RetrofitClient.userApi

    suspend fun searchUsers(query: String): Result<List<User>> = runCatching {
        val response = userApi.searchUsers(query.takeIf { it.isNotBlank() })
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to search users")
        }
        body.data.orEmpty()
    }

    suspend fun getProfile(): Result<User> = runCatching {
        val response = userApi.getProfile()
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            throw Exception("Failed to load profile")
        }
        body
    }

    suspend fun updateProfile(username: String?, avatar: String?): Result<User> = runCatching {
        val response = userApi.updateProfile(UpdateProfileRequest(username, avatar))
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            throw Exception("Failed to update profile")
        }
        body
    }
}
