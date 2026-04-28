package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.ConversationResponse
import com.example.chat_app_clone.network.model.MessageResponse
import com.example.chat_app_clone.network.model.SendMessageRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {
    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<ConversationsApiResponse>

    @GET("api/chat/conversations/{id}")
    suspend fun getConversation(@Path("id") id: String): Response<ConversationApiResponse>

    @POST("api/chat/conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): Response<ConversationApiResponse>

    @GET("api/chat/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<MessagesApiResponse>

    @POST("api/chat/message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

    @PATCH("api/chat/messages/seen")
    suspend fun markMessagesSeen(@Body request: MarkSeenRequest): Response<MarkSeenResponse>
}

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
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("marked_as_seen") val markedAsSeen: List<Long>
)

data class CreateConversationRequest(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("member_ids") val memberIds: List<Long>
)

data class MarkSeenRequest(
    @SerializedName("conversationId") val conversationId: String
)

data class Pagination(
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("count") val count: Int
)
