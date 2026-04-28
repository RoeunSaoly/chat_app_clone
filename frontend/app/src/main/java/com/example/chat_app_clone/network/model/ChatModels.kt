package com.example.chat_app_clone.network.model

import com.google.gson.annotations.SerializedName

data class ConversationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("last_message") val lastMessage: String? = null,
    @SerializedName("last_message_at") val lastMessageAt: String? = null,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("members") val members: List<ConversationMemberResponse>? = null,
    @SerializedName("other_user") val otherUser: UserResponse? = null
)

data class ConversationMemberResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("username") val username: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("is_online") val isOnline: Boolean = false,
    @SerializedName("last_seen") val lastSeen: String? = null,
    @SerializedName("role") val role: String = "member"
)

data class MessageResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("sender_id") val senderId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("status") val status: String = "sent",
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("sender_username") val senderUsername: String? = null,
    @SerializedName("sender_avatar") val senderAvatar: String? = null,
    @SerializedName("read_by") val readBy: List<MessageReadResponse>? = null
)

data class MessageReadResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("username") val username: String? = null,
    @SerializedName("seen_at") val seenAt: String? = null
)

data class SendMessageRequest(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("content") val content: String,
    @SerializedName("messageType") val messageType: String = "text"
)

data class UserResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("status_message") val statusMessage: String? = null,
    @SerializedName("is_online") val isOnline: Boolean = false,
    @SerializedName("last_seen") val lastSeen: String? = null
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
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("seen_at") val seenAt: String? = null
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

