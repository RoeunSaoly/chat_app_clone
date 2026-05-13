package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val conversations: List<Conversation> = emptyList(),
    val typingStatuses: Map<Long, List<String>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
        collectRealtimeMessages()
        collectTypingEvents()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.fetchConversations()
                .onSuccess { conversations ->
                    _uiState.value = HomeUiState(conversations = conversations)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    private fun collectRealtimeMessages() {
        viewModelScope.launch {
            repository.newMessages.collect { message ->
                val conversations = _uiState.value.conversations
                val existing = conversations.find { it.id == message.conversationId }
                
                val updated = if (existing != null) {
                    conversations.map { conversation ->
                        if (conversation.id == message.conversationId) {
                            conversation.copy(
                                lastMessage = message.content,
                                updatedAt = message.createdAt,
                                unreadCount = conversation.unreadCount + 1
                            )
                        } else conversation
                    }
                } else {
                    // If it's a new conversation, we should ideally fetch the full object, 
                    // but for now we trigger a refresh to be safe and accurate.
                    loadConversations()
                    return@collect
                }

                _uiState.value = _uiState.value.copy(
                    conversations = updated.sortedByDescending { it.updatedAt.orEmpty() }
                )
            }
        }
    }

    private fun collectTypingEvents() {
        viewModelScope.launch {
            repository.typingEvents.collect { (id, names) ->
                val current = _uiState.value.typingStatuses.toMutableMap()
                if (names.isEmpty()) {
                    current.remove(id)
                } else {
                    current[id] = names
                }
                _uiState.value = _uiState.value.copy(typingStatuses = current)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
