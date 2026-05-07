package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.network.MarkSeenRequest
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.network.TypingRequest
import com.example.chat_app_clone.network.CreateGroupConversationRequest
import com.example.chat_app_clone.network.CreatePrivateConversationRequest
import com.example.chat_app_clone.network.model.SendMessageRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRepository {

    private val chatApi = RetrofitClient.chatApi
    private val socketManager = SocketManager.getInstance()

    private val _newMessages = MutableSharedFlow<Message>(extraBufferCapacity = 16)
    val newMessages: SharedFlow<Message> = _newMessages.asSharedFlow()

    private val _typingEvents = MutableSharedFlow<Pair<Long, List<String>>>(extraBufferCapacity = 16)
    val typingEvents: SharedFlow<Pair<Long, List<String>>> = _typingEvents.asSharedFlow()

    private val _messageSeenEvents = MutableSharedFlow<Pair<Long, List<Long>>>(extraBufferCapacity = 16)
    val messageSeenEvents: SharedFlow<Pair<Long, List<Long>>> = _messageSeenEvents.asSharedFlow()

    init {
        socketManager.onNewMessage { message ->
            _newMessages.tryEmit(message)
        }

        socketManager.onTyping { event ->
            val names = event.typingUsers?.mapNotNull { it.username } ?: emptyList()
            _typingEvents.tryEmit(event.conversationId to names)
        }

        socketManager.onMessageSeen { event ->
            val ids = event.messageIds ?: event.messageId?.let { listOf(it) } ?: emptyList()
            _messageSeenEvents.tryEmit(event.conversationId to ids)
        }
    }

    suspend fun fetchConversations(): Result<List<Conversation>> = runCatching {
        val response = chatApi.getConversations()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to fetch conversations")
        }
        body.data.orEmpty()
    }

    suspend fun fetchMessages(conversationId: Long): Result<List<Message>> = runCatching {
        val response = chatApi.getMessages(conversationId.toString())
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to fetch messages")
        }
        body.data.orEmpty()
    }

    suspend fun startPrivateChat(userId: Long): Result<Conversation> = runCatching {
        val response = chatApi.createPrivateConversation(CreatePrivateConversationRequest(userId))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to start chat")
        }
        body.data
    }

    suspend fun createGroup(name: String, memberIds: List<Long>): Result<Conversation> = runCatching {
        val response = chatApi.createGroupConversation(CreateGroupConversationRequest(name, memberIds))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to create group")
        }
        body.data
    }

    suspend fun sendMessage(conversationId: Long, content: String): Result<Message> = runCatching {
        val response = chatApi.sendMessage(SendMessageRequest(conversationId, content.trim()))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to send message")
        }
        body.data
    }

    suspend fun markMessagesSeen(conversationId: Long): Result<List<Long>> = runCatching {
        val response = chatApi.markMessagesSeen(MarkSeenRequest(conversationId))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to mark messages as seen")
        }
        body.data?.markedAsSeen.orEmpty()
    }

    suspend fun updateTyping(conversationId: Long, isTyping: Boolean): Result<Unit> = runCatching {
        val response = chatApi.updateTyping(TypingRequest(conversationId, isTyping))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to update typing status")
        }
    }

    fun joinConversation(conversationId: Long) {
        socketManager.joinConversation(conversationId.toString())
    }

    fun leaveConversation(conversationId: Long) {
        socketManager.leaveConversation(conversationId.toString())
    }
}
