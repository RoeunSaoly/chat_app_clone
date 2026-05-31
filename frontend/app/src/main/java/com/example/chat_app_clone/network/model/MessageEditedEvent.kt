package com.example.chat_app_clone.network.model

import com.google.gson.annotations.SerializedName

data class MessageEditedEvent(
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("content") val content: String
)
