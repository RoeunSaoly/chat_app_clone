package com.example.chat_app_clone.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.model.MessageStatus
import com.example.chat_app_clone.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository(),
    private val currentUserId: String
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _typingUsers = mutableStateOf<List<String>>(emptyList())
    val typingUsers: State<List<String>> = _typingUsers

    private var typingJob: Job? = null
    private var currentConversationId: String = ""

    init {
        collectSocketEvents()
    }

    private fun collectSocketEvents() {
        viewModelScope.launch {
            repository.newMessages.collectLatest { message ->
                // Only add if it's for the current conversation
                if (message.senderId != currentUserId) {
                    _messages.add(message)
                    // Auto-mark as seen
                    repository.markAllSeenViaSocket(currentConversationId)
                }
            }
        }

        viewModelScope.launch {
            repository.typingEvents.collectLatest { (conversationId, users) ->
                if (conversationId == currentConversationId) {
                    _typingUsers.value = users
                }
            }
        }

        viewModelScope.launch {
            repository.messageSeenEvents.collectLatest { (conversationId, messageId) ->
                if (conversationId == currentConversationId) {
                    updateMessageStatus(messageId.toString(), MessageStatus.READ)
                }
            }
        }
    }

    fun setConversationId(conversationId: String) {
        if (currentConversationId.isNotEmpty()) {
            repository.leaveConversation(currentConversationId)
        }
        currentConversationId = conversationId
        repository.joinConversation(conversationId)
        loadMessages()
        // Mark all as seen when entering conversation
        viewModelScope.launch {
            repository.markMessagesSeen(conversationId)
            repository.markAllSeenViaSocket(conversationId)
        }
    }

    fun loadMessages() {
        if (currentConversationId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.fetchMessages(currentConversationId)
                .onSuccess { fetchedMessages ->
                    _messages.clear()
                    _messages.addAll(fetchedMessages)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _isLoading.value = false
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || currentConversationId.isEmpty()) return

        // Optimistic UI update
        val tempId = System.currentTimeMillis().toString()
        val optimisticMessage = Message(
            id = tempId,
            senderId = currentUserId,
            conversationId = currentConversationId,
            content = content,
            timestamp = "Sending...",
            status = MessageStatus.SENT,
            type = com.example.chat_app_clone.data.model.MessageType.TEXT
        )
        _messages.add(optimisticMessage)

        // Send via socket for real-time
        repository.sendMessageViaSocket(currentConversationId, content)

        // Also send via REST as backup
        viewModelScope.launch {
            repository.sendMessage(currentConversationId, content)
                .onSuccess { sentMessage ->
                    // Replace optimistic message with actual
                    val index = _messages.indexOfFirst { it.id == tempId }
                    if (index != -1) {
                        _messages[index] = sentMessage
                    }
                }
                .onFailure {
                    // Mark as failed
                    val index = _messages.indexOfFirst { it.id == tempId }
                    if (index != -1) {
                        _messages[index] = _messages[index].copy(content = "${optimisticMessage.content} (failed)")
                    }
                }
        }
    }

    fun onTyping() {
        if (currentConversationId.isEmpty()) return

        repository.startTyping(currentConversationId)

        // Auto-stop typing after 3 seconds
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            delay(3000)
            repository.stopTyping(currentConversationId)
        }
    }

    fun onStopTyping() {
        typingJob?.cancel()
        if (currentConversationId.isNotEmpty()) {
            repository.stopTyping(currentConversationId)
        }
    }

    private fun updateMessageStatus(messageId: String, status: MessageStatus) {
        val index = _messages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            _messages[index] = _messages[index].copy(status = status)
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        if (currentConversationId.isNotEmpty()) {
            repository.leaveConversation(currentConversationId)
        }
        typingJob?.cancel()
    }
}
