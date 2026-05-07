package com.example.chat_app_clone.data.model

import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("id")
    val id: Long,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("last_message")
    val lastMessage: String? = null,
    @SerializedName(value = "updated_at", alternate = ["last_message_at", "created_at"])
    val updatedAt: String? = null,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("members")
    val members: List<ConversationMember> = emptyList(),
    @SerializedName("other_user")
    val otherUser: ConversationMember? = null
) {
    fun displayName(currentUserId: Long): String {
        if (type == "group") return name?.takeIf { it.isNotBlank() } ?: "Group chat"
        return otherUser?.username
            ?: members.firstOrNull { it.userId != currentUserId }?.username
            ?: name
            ?: "Private chat"
    }

    fun displayAvatar(currentUserId: Long): String? {
        return if (type == "group") avatar else otherUser?.avatar
            ?: members.firstOrNull { it.userId != currentUserId }?.avatar
    }

    fun isOtherUserOnline(currentUserId: Long): Boolean {
        return otherUser?.isOnline
            ?: members.firstOrNull { it.userId != currentUserId }?.isOnline
            ?: false
    }
}

data class ConversationMember(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    @SerializedName("last_seen")
    val lastSeen: String? = null,
    @SerializedName("role")
    val role: String = "member"
)
