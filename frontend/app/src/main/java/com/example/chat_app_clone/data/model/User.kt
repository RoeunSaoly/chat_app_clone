package com.example.chat_app_clone.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName(value = "id", alternate = ["user_id"])
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    @SerializedName("last_seen")
    val lastSeen: String? = null
) {
    val displayName: String
        get() = username.ifBlank { "User $id" }
}
