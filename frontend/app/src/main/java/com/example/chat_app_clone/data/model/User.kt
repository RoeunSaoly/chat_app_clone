package com.example.chat_app_clone.data.model

data class User(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val username: String = ""
)
