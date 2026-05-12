package com.example.chat_app_clone.network.model

import com.google.gson.annotations.SerializedName
import com.example.chat_app_clone.data.model.User

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("accessToken")
    val accessToken: String? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("error")
    val error: String? = null
)

data class RefreshRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
