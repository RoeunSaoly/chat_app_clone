package com.example.chat_app_clone.network.model

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.ConversationMember
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageRead
import com.example.chat_app_clone.data.model.User
import com.google.gson.annotations.SerializedName

typealias ConversationResponse = Conversation
typealias ConversationMemberResponse = ConversationMember
typealias MessageResponse = Message
typealias MessageReadResponse = MessageRead
typealias UserResponse = User

data class SendMessageRequest(
    @SerializedName("conversationId") val conversationId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("messageType") val messageType: String = "text"
)

data class TypingEvent(
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("typing_users") val typingUsers: List<TypingUser>? = null
)

data class TypingUser(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("username") val username: String? = null
)

data class MessageSeenEvent(
    @SerializedName("message_id") val messageId: Long? = null,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("seen_at") val seenAt: String? = null,
    @SerializedName("message_ids") val messageIds: List<Long>? = null
)

data class MessagesSeenEvent(
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("message_ids") val messageIds: List<Long>
)

data class UserOnlineEvent(
    @SerializedName("user_id") val userId: Long
)

data class UserOfflineEvent(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("last_seen") val lastSeen: String? = null
)

data class Pagination(
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("count") val count: Int
)

