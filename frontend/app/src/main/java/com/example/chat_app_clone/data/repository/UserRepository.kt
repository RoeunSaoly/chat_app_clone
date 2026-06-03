package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.network.NotificationsData
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.UpdateProfileRequest

class UserRepository {
    private val userApi = RetrofitClient.userApi

    suspend fun getNotifications(limit: Int = 20, offset: Int = 0): Result<NotificationsData> = runCatching {
        val response = userApi.getNotifications(limit, offset)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to load notifications")
        }
        body.data
    }

    suspend fun markNotificationRead(id: Long): Result<Unit> = runCatching {
        val response = userApi.markNotificationRead(id)
        if (!response.isSuccessful) throw Exception("Failed to mark notification as read")
    }

    suspend fun markAllNotificationsRead(): Result<Unit> = runCatching {
        val response = userApi.markAllNotificationsRead()
        if (!response.isSuccessful) throw Exception("Failed to mark all notifications as read")
    }

    suspend fun getUnreadNotificationCount(): Result<Int> = runCatching {
        val response = userApi.getUnreadNotificationCount()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to load unread count")
        }
        body.data.count
    }

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
        if (!response.isSuccessful || body == null || !body.success) {
            throw Exception(body?.error ?: "Failed to load profile")
        }
        body.data
    }

    suspend fun updateProfile(username: String?, avatar: String?): Result<User> = runCatching {
        val response = userApi.updateProfile(UpdateProfileRequest(username, avatar))
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success) {
            throw Exception(body?.error ?: "Failed to update profile")
        }
        body.data
    }

    suspend fun getRecommendedFriends(): Result<List<User>> = runCatching {
        val response = userApi.getRecommendedFriends()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to load recommended friends")
        }
        body.data.orEmpty()
    }

    suspend fun getFriendRequests(): Result<List<User>> = runCatching {
        val response = userApi.getFriendRequests()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to load friend requests")
        }
        body.data.orEmpty()
    }

    suspend fun getFriends(): Result<List<User>> = runCatching {
        val response = userApi.getFriends()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to load friends")
        }
        body.data.orEmpty()
    }

    suspend fun sendFriendRequest(userId: Long): Result<String> = runCatching {
        val response = userApi.sendFriendRequest(userId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to send friend request")
        }
        body.message ?: "Request sent"
    }

    suspend fun acceptFriendRequest(userId: Long): Result<String> = runCatching {
        val response = userApi.acceptFriendRequest(userId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to accept friend request")
        }
        body.message ?: "Request accepted"
    }

    suspend fun rejectFriendRequest(userId: Long): Result<String> = runCatching {
        val response = userApi.rejectFriendRequest(userId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to reject friend request")
        }
        body.message ?: "Request rejected"
    }

    suspend fun unfriend(userId: Long): Result<String> = runCatching {
        val response = userApi.unfriend(userId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to unfriend")
        }
        body.message ?: "Unfriended"
    }
}
