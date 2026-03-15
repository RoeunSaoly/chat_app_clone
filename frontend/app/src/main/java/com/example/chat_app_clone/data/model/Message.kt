package com.example.chat_app_clone.data.model

enum class MessageType { TEXT, IMAGE, EMOJI, AUDIO, VIDEO, STICKER }
enum class MessageStatus { SENT, DELIVERED, READ }

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: String,
    val status: MessageStatus = MessageStatus.DELIVERED,
    val type: MessageType = MessageType.TEXT,
    val reactionEmoji: String? = null
)
