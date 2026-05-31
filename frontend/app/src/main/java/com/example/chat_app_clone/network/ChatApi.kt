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

    @PATCH("api/messages/{messageId}")
    suspend fun editMessage(
        @Path("messageId") messageId: String,
        @Body request: EditMessageRequest
    ): Response<SendMessageResponse>
}
