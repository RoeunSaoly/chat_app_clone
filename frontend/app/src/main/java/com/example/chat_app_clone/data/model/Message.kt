package com.example.chat_app_clone.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: Long,
    @SerializedName("conversation_id")
    val conversationId: Long,
    @SerializedName("sender_id")
    val senderId: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("message_type")
    val messageType: String = "text",
    @SerializedName("status")
    val status: String = "sent",
    @SerializedName("deleted_for_everyone")
    val deletedForEveryone: Boolean = false,
    @SerializedName("deleted_for_me")
    val deletedForMe: Boolean = false,
    @SerializedName("is_edited")
    val isEdited: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("sender_username")
    val senderUsername: String? = null,
    @SerializedName("sender_avatar")
    val senderAvatar: String? = null,
    @SerializedName("read_by")
    val readBy: List<MessageRead> = emptyList(),
    @SerializedName("reactions")
    val reactions: List<MessageReaction> = emptyList()
)

data class MessageRead(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("seen_at")
    val seenAt: String? = null
)

data class MessageReaction(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("reaction")
    val reaction: String? = null
)
