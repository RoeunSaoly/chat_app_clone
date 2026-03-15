package com.example.chat_app_clone.data.model

data class Conversation(
    val id: String,
    val otherUser: User,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isGroup: Boolean = false,
    val groupName: String = "",
    val isMuted: Boolean = false,
    val lastMessageSenderId: String = ""
)
