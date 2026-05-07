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
                val updated = _uiState.value.conversations.map { conversation ->
                    if (conversation.id == message.conversationId) {
                        conversation.copy(
                            lastMessage = message.content,
                            updatedAt = message.createdAt,
                            unreadCount = conversation.unreadCount + 1
                        )
                    } else {
                        conversation
                    }
                }.sortedByDescending { it.updatedAt.orEmpty() }

                _uiState.value = _uiState.value.copy(conversations = updated)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
