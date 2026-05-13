package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Message
import com.example.chat_app_clone.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val typingUsers: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository(),
    private val currentUserId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var conversationId: Long? = null
    private var typingJob: Job? = null

    init {
        collectRealtime()
    }

    fun openConversation(id: Long) {
        conversationId?.let(repository::leaveConversation)
        conversationId = id
        repository.joinConversation(id)
        loadMessages()
        markSeen()
    }

    fun loadMessages() {
        val id = conversationId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.fetchMessages(id)
                .onSuccess { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun sendMessage(content: String) {
        val id = conversationId ?: return
        if (content.isBlank()) return

        val tempMessage = Message(
            id = System.currentTimeMillis() * -1,
            conversationId = id,
            senderId = currentUserId,
            content = content.trim(),
            messageType = "text",
            status = "sent",
            createdAt = "Sending..."
        )
        _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + tempMessage)

        viewModelScope.launch {
            repository.sendMessage(id, content)
                .onSuccess { sent ->
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages.map { if (it.id == tempMessage.id) sent else it }
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun onTyping() {
        val id = conversationId ?: return
        typingJob?.cancel()
        viewModelScope.launch { repository.updateTyping(id, true) }
        typingJob = viewModelScope.launch {
            delay(1200)
            repository.updateTyping(id, false)
        }
    }

    fun stopTyping() {
        val id = conversationId ?: return
        typingJob?.cancel()
        viewModelScope.launch { repository.updateTyping(id, false) }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun markSeen() {
        val id = conversationId ?: return
        viewModelScope.launch { repository.markMessagesSeen(id) }
    }

    fun deleteMessage(messageId: Long, forEveryone: Boolean) {
        viewModelScope.launch {
            repository.deleteMessage(messageId, forEveryone)
                .onSuccess {
                    if (!forEveryone) {
                        _uiState.value = _uiState.value.copy(
                            messages = _uiState.value.messages.filter { it.id != messageId }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    private fun collectRealtime() {
        viewModelScope.launch {
            repository.newMessages.collect { message ->
                if (message.conversationId == conversationId && _uiState.value.messages.none { it.id == message.id }) {
                    _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + message)
                    if (message.senderId != currentUserId) markSeen()
                }
            }
        }

        viewModelScope.launch {
            repository.deletedMessageEvents.collect { event ->
                if (event.conversationId == conversationId) {
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages.map {
                            if (it.id == event.messageId) it.copy(deletedForEveryone = true, content = "This message was deleted") else it
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.typingEvents.collect { (id, names) ->
                if (id == conversationId) {
                    _uiState.value = _uiState.value.copy(typingUsers = names)
                }
            }
        }

        viewModelScope.launch {
            repository.messageSeenEvents.collect { (id, messageIds) ->
                if (id == conversationId) {
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages.map {
                            if (it.id in messageIds) it.copy(status = "seen") else it
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        conversationId?.let(repository::leaveConversation)
        typingJob?.cancel()
        super.onCleared()
    }
}
