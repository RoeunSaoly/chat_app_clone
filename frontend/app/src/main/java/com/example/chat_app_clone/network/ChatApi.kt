package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.*
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {
    @GET("api/conversations")
    suspend fun getConversations(): Response<ConversationsApiResponse>

    @GET("api/conversations/{id}")
    suspend fun getConversation(@Path("id") id: Long): Response<ConversationApiResponse>

    @POST("api/conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): Response<ConversationApiResponse>

    @POST("api/conversations/private")
    suspend fun createPrivateConversation(@Body request: CreatePrivateConversationRequest): Response<ConversationApiResponse>

    @POST("api/conversations/group")
    suspend fun createGroupConversation(@Body request: CreateGroupConversationRequest): Response<ConversationApiResponse>

    @GET("api/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<MessagesApiResponse>

    @POST("api/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

    @POST("api/messages/seen")
    suspend fun markMessagesSeen(@Body request: MarkSeenRequest): Response<MarkSeenResponse>

    @POST("api/typing")
    suspend fun updateTyping(@Body request: TypingRequest): Response<TypingApiResponse>

    @POST("api/chat/messages/react")
    suspend fun reactToMessage(@Body request: ReactMessageRequest): Response<GenericApiResponse>

    @DELETE("api/chat/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("messageId") messageId: String,
        @Query("type") type: String
    ): Response<GenericApiResponse>
}

data class MessageDeletedEvent(
    @SerializedName("message_id") val messageId: Long,
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("deleted_for_everyone") val deletedForEveryone: Boolean
)

data class ConversationsApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<ConversationResponse>? = null,
    @SerializedName("error") val error: String? = null
)

data class ConversationApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ConversationResponse? = null,
    @SerializedName("error") val error: String? = null
)

data class MessagesApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<MessageResponse>? = null,
    @SerializedName("pagination") val pagination: Pagination? = null,
    @SerializedName("error") val error: String? = null
)

data class SendMessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MessageResponse? = null,
    @SerializedName("error") val error: String? = null
)

data class MarkSeenResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MarkSeenData? = null,
    @SerializedName("error") val error: String? = null
)

data class MarkSeenData(
    @SerializedName("conversation_id") val conversationId: Long,
    @SerializedName("marked_as_seen") val markedAsSeen: List<Long>
)

data class CreateConversationRequest(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("member_ids") val memberIds: List<Long>
)

data class MarkSeenRequest(
    @SerializedName("conversationId") val conversationId: Long
)

data class CreatePrivateConversationRequest(
    @SerializedName("userId") val userId: Long
)

data class CreateGroupConversationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("member_ids") val memberIds: List<Long>,
    @SerializedName("avatar") val avatar: String? = null
)

data class TypingRequest(
    @SerializedName("conversationId") val conversationId: Long,
    @SerializedName("isTyping") val isTyping: Boolean
)

data class ReactMessageRequest(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("reaction") val reaction: String? = null
)

data class TypingApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String? = null
)

data class GenericApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String? = null
)
