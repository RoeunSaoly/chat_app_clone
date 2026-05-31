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

data class EditMessageRequest(
    @SerializedName("content") val content: String
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

// API Response Models
data class ConversationsApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Conversation>? = null,
    @SerializedName("error") val error: String? = null
)

data class ConversationApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Conversation? = null,
    @SerializedName("error") val error: String? = null
)

data class MessagesApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Message>? = null,
    @SerializedName("pagination") val pagination: Pagination? = null,
    @SerializedName("error") val error: String? = null
)

data class SendMessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Message? = null,
    @SerializedName("notifiedUsers") val notifiedUsers: List<Long>? = null,
    @SerializedName("error") val error: String? = null
)

data class MarkSeenRequest(
    @SerializedName("conversationId") val conversationId: Long
)

data class MarkSeenResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MarkSeenData? = null,
    @SerializedName("error") val error: String? = null
)

data class MarkSeenData(
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("marked_as_seen") val markedAsSeen: List<Long>? = null
)

data class TypingRequest(
    @SerializedName("conversationId") val conversationId: Long,
    @SerializedName("isTyping") val isTyping: Boolean
)

data class TypingApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: TypingData? = null,
    @SerializedName("error") val error: String? = null
)

data class TypingData(
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("is_typing") val isTyping: Boolean,
    @SerializedName("typing_users") val typingUsers: List<TypingUser>? = null
)

data class ReactMessageRequest(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("reaction") val reaction: String? = null
)

data class CreateConversationRequest(
    @SerializedName("members") val members: List<Long>
)

data class CreatePrivateConversationRequest(
    @SerializedName("userId") val userId: Long
)

data class CreateGroupConversationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("members") val members: List<Long>
)
