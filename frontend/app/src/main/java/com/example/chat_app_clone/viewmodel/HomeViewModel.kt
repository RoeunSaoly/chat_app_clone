package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.data.repository.ChatRepository
import com.example.chat_app_clone.data.repository.UserRepository
import com.example.chat_app_clone.data.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val conversations: List<Conversation> = emptyList(),
    val friends: List<User> = emptyList(),
    val typingStatuses: Map<Long, List<String>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    fun getCurrentUserId(): Long = preferenceManager.getUserId()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
        loadFriends()
        collectRealtimeMessages()
        collectDeletedMessages()
        collectTypingEvents()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            chatRepository.fetchConversations()
                .onSuccess { conversations ->
                    _uiState.value = HomeUiState(
                        conversations = conversations,
                        friends = _uiState.value.friends
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun loadFriends() {
        viewModelScope.launch {
            userRepository.getFriends()
                .onSuccess { friends ->
                    _uiState.value = _uiState.value.copy(friends = friends)
                }
                .onFailure { error ->
                    // Don't show error for friends loading, just log it
                    _uiState.value = _uiState.value.copy(friends = emptyList())
                }
        }
    }

    private fun collectRealtimeMessages() {
        viewModelScope.launch {
            chatRepository.newMessages.collect { message ->
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

    private fun collectDeletedMessages() {
        viewModelScope.launch {
            chatRepository.deletedMessageEvents.collect { event ->
                if (event.deletedForEveryone) {
                    val updated = _uiState.value.conversations.map { conversation ->
                        if (conversation.id == event.conversationId) {
                            conversation.copy(lastMessage = "Message deleted")
                        } else conversation
                    }
                    _uiState.value = _uiState.value.copy(conversations = updated)
                }
            }
        }
    }

    private fun collectTypingEvents() {
        viewModelScope.launch {
            chatRepository.typingEvents.collect { (id, names) ->
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
