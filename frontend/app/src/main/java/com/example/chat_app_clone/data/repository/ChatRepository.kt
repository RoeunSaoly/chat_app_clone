package com.example.chat_app_clone.data.repository

import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageStatus
import com.example.chat_app_clone.data.model.MessageType
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.network.model.ConversationResponse
import com.example.chat_app_clone.network.model.MessageResponse
import com.example.chat_app_clone.network.model.SendMessageRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRepository {

    private val chatApi = RetrofitClient.chatApi
    private val socketManager = SocketManager.getInstance()

    // Shared flows for real-time events
    private val _newMessages = MutableSharedFlow<Message>(extraBufferCapacity = 10)
    val newMessages: SharedFlow<Message> = _newMessages.asSharedFlow()

    private val _typingEvents = MutableSharedFlow<Pair<String, List<String>>>(extraBufferCapacity = 10)
    val typingEvents: SharedFlow<Pair<String, List<String>>> = _typingEvents.asSharedFlow()

    private val _messageSeenEvents = MutableSharedFlow<Pair<String, Long>>(extraBufferCapacity = 10)
    val messageSeenEvents: SharedFlow<Pair<String, Long>> = _messageSeenEvents.asSharedFlow()

    private val _userOnlineEvents = MutableSharedFlow<Long>(extraBufferCapacity = 10)
    val userOnlineEvents: SharedFlow<Long> = _userOnlineEvents.asSharedFlow()

    private val _userOfflineEvents = MutableSharedFlow<Pair<Long, String?>>(extraBufferCapacity = 10)
    val userOfflineEvents: SharedFlow<Pair<Long, String?>> = _userOfflineEvents.asSharedFlow()

    init {
        setupSocketListeners()
    }

    private fun setupSocketListeners() {
        socketManager.onNewMessage { messageResponse ->
            _newMessages.tryEmit(messageResponse.toDomainModel())
        }

        socketManager.onTyping { event ->
            val names = event.typingUsers?.mapNotNull { it.username } ?: emptyList()
            _typingEvents.tryEmit(event.conversationId.toString() to names)
        }

        socketManager.onStopTyping { event ->
            _typingEvents.tryEmit(event.conversationId.toString() to emptyList())
        }

        socketManager.onMessageSeen { event ->
            _messageSeenEvents.tryEmit(event.conversationId.toString() to event.messageId)
        }

        socketManager.onMessagesSeen { event ->
            event.messageIds.forEach { messageId ->
                _messageSeenEvents.tryEmit(event.conversationId.toString() to messageId)
            }
        }

        socketManager.onUserOnline { event ->
            _userOnlineEvents.tryEmit(event.userId)
        }

        socketManager.onUserOffline { event ->
            _userOfflineEvents.tryEmit(event.userId to event.lastSeen)
        }
    }

    // --- REST API calls ---

    suspend fun fetchConversations(): Result<List<Conversation>> {
        return try {
            val response = chatApi.getConversations()
            if (response.isSuccessful && response.body()?.success == true) {
                val conversations = response.body()?.data?.map { it.toDomainModel() } ?: emptyList()
                Result.success(conversations)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch conversations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMessages(conversationId: String, limit: Int = 50, offset: Int = 0): Result<List<Message>> {
        return try {
            val response = chatApi.getMessages(conversationId, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                val messages = response.body()?.data?.map { it.toDomainModel() } ?: emptyList()
                Result.success(messages)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(conversationId: String, content: String): Result<Message> {
        return try {
            val response = chatApi.sendMessage(
                SendMessageRequest(conversationId, content, "text")
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val message = response.body()?.data?.toDomainModel()
                message?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markMessagesSeen(conversationId: String): Result<List<Long>> {
        return try {
            val response = chatApi.markMessagesSeen(
                com.example.chat_app_clone.network.MarkSeenRequest(conversationId)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val ids = response.body()?.data?.markedAsSeen ?: emptyList()
                Result.success(ids)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to mark seen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Socket operations ---

    fun joinConversation(conversationId: String) {
        socketManager.joinConversation(conversationId)
    }

    fun leaveConversation(conversationId: String) {
        socketManager.leaveConversation(conversationId)
    }

    fun sendMessageViaSocket(conversationId: String, content: String) {
        socketManager.sendMessage(conversationId, content)
    }

    fun startTyping(conversationId: String) {
        socketManager.startTyping(conversationId)
    }

    fun stopTyping(conversationId: String) {
        socketManager.stopTyping(conversationId)
    }

    fun markAllSeenViaSocket(conversationId: String) {
        socketManager.markAllSeen(conversationId)
    }

    // --- Mappers ---

    private fun ConversationResponse.toDomainModel(): Conversation {
        val other = this.otherUser
        return Conversation(
            id = this.id.toString(),
            otherUser = other?.toDomainModel() ?: User("0", "Unknown"),
            lastMessage = this.lastMessage ?: "",
            lastMessageTime = this.lastMessageAt?.let { formatTimestamp(it) } ?: "",
            unreadCount = this.unreadCount,
            isGroup = this.type == "group",
            groupName = this.name ?: "",
            isMuted = false,
            lastMessageSenderId = ""
        )
    }

    private fun com.example.chat_app_clone.network.model.UserResponse.toDomainModel(): User {
        return User(
            id = this.id.toString(),
            name = this.username,
            avatarUrl = this.avatar ?: "",
            isOnline = this.isOnline,
            lastSeen = this.lastSeen ?: "",
            username = this.username
        )
    }

    private fun MessageResponse.toDomainModel(): Message {
        val status = when (this.status.lowercase()) {
            "sent" -> MessageStatus.SENT
            "delivered" -> MessageStatus.DELIVERED
            "seen" -> MessageStatus.READ
            else -> MessageStatus.DELIVERED
        }
        val type = when (this.messageType.lowercase()) {
            "text" -> MessageType.TEXT
            "image" -> MessageType.IMAGE
            "audio" -> MessageType.AUDIO
            "video" -> MessageType.VIDEO
            else -> MessageType.TEXT
        }
        return Message(
            id = this.id.toString(),
            senderId = this.senderId.toString(),
            conversationId = this.conversationId.toString(),
            content = this.content,
            timestamp = formatTimestamp(this.createdAt),
            status = status,
            type = type
        )
    }

    private fun formatTimestamp(isoString: String): String {
        // Simple formatting - in production use a proper date formatter
        return try {
            val parts = isoString.split("T")
            if (parts.size >= 2) {
                val timePart = parts[1].substring(0, 5)
                timePart
            } else {
                isoString
            }
        } catch (e: Exception) {
            isoString
        }
    }
}
