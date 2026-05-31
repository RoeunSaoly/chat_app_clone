package com.example.chat_app_clone.network.model

import com.google.gson.annotations.SerializedName

data class MessageDeletedEvent(
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("deleted_for_everyone") val deletedForEveryone: Boolean
)
