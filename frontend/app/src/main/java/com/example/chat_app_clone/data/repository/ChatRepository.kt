package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.network.*
import com.example.chat_app_clone.network.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRepository private constructor() {

    companion object {
        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(): ChatRepository {
            return instance ?: synchronized(this) {
                instance ?: ChatRepository().also { instance = it }
            }
        }
    }

    private val chatApi = RetrofitClient.chatApi
    private val socketManager = SocketManager.getInstance()

    private val _newMessages = MutableSharedFlow<Message>(extraBufferCapacity = 16)
    val newMessages: SharedFlow<Message> = _newMessages.asSharedFlow()

    private val _typingEvents = MutableSharedFlow<Pair<Long, List<String>>>(extraBufferCapacity = 16)
    val typingEvents: SharedFlow<Pair<Long, List<String>>> = _typingEvents.asSharedFlow()

    private val _messageSeenEvents = MutableSharedFlow<Pair<Long, List<Long>>>(extraBufferCapacity = 16)
    val messageSeenEvents: SharedFlow<Pair<Long, List<Long>>> = _messageSeenEvents.asSharedFlow()

    private val _deletedMessageEvents = MutableSharedFlow<MessageDeletedEvent>(extraBufferCapacity = 16)
    val deletedMessageEvents: SharedFlow<MessageDeletedEvent> = _deletedMessageEvents.asSharedFlow()

    private val _messageEditedEvents = MutableSharedFlow<MessageEditedEvent>(extraBufferCapacity = 16)
    val messageEditedEvents: SharedFlow<MessageEditedEvent> = _messageEditedEvents.asSharedFlow()

    init {
        socketManager.onNewMessage { response ->
            val message = Message(
                id = response.id,
                conversationId = response.conversationId,
                senderId = response.senderId,
                content = response.content ?: "",
                messageType = response.messageType ?: "text",
                status = response.status ?: "sent",
                createdAt = response.createdAt ?: "",
                senderUsername = response.senderUsername ?: "",
                senderAvatar = response.senderAvatar,
                deletedForEveryone = response.deletedForEveryone,
                deletedForMe = response.deletedForMe,
                isEdited = response.isEdited
            )
            _newMessages.tryEmit(message)
        }

        socketManager.onMessageDeleted { event ->
            _deletedMessageEvents.tryEmit(event)
        }

        socketManager.onMessageEdited { event ->
            _messageEditedEvents.tryEmit(event)
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

    suspend fun deleteMessage(messageId: Long, forEveryone: Boolean): Result<Unit> = runCatching {
        val type = if (forEveryone) "everyone" else "me"
        val response = chatApi.deleteMessage(messageId.toString(), type)
        if (!response.isSuccessful) throw Exception("Failed to delete message")
    }

    suspend fun editMessage(messageId: Long, content: String): Result<Message> = runCatching {
        val response = chatApi.editMessage(messageId.toString(), EditMessageRequest(content))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true || body.data == null) {
            throw Exception(body?.error ?: "Failed to edit message")
        }
        body.data
    }

    suspend fun fetchConversations(): Result<List<Conversation>> = runCatching {
        val response = chatApi.getConversations()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to fetch conversations")
        }
        body.data ?: emptyList()
    }

    suspend fun fetchMessages(conversationId: Long): Result<List<Message>> = runCatching {
        val response = chatApi.getMessages(conversationId.toString())
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw Exception(body?.error ?: "Failed to fetch messages")
        }
        body.data ?: emptyList()
    }

    suspend fun startOrGetPrivateConversation(userId: Long): Result<Conversation> = runCatching {
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

    suspend  fun startPrivateChat(userId: Long): Result<Conversation> {
        return startOrGetPrivateConversation(userId)
    }
}
