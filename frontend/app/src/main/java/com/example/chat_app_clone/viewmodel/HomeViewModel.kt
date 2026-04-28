package com.example.chat_app_clone.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.data.repository.ChatRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _conversations = mutableStateListOf<Conversation>()
    val conversations: List<Conversation> = _conversations

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        collectSocketEvents()
        loadConversations()
    }

    private fun collectSocketEvents() {
        viewModelScope.launch {
            repository.newMessages.collectLatest { message ->
                // Update the conversation list with new message (only if conversationId is known)
                if (message.conversationId.isNotEmpty()) {
                    updateConversationWithNewMessage(message.conversationId, message.content, message.timestamp)
                }
            }
        }

        viewModelScope.launch {
            repository.userOnlineEvents.collectLatest { userId ->
                updateUserOnlineStatus(userId.toString(), true, "")
            }
        }

        viewModelScope.launch {
            repository.userOfflineEvents.collectLatest { (userId, lastSeen) ->
                updateUserOnlineStatus(userId.toString(), false, lastSeen ?: "")
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.fetchConversations()
                .onSuccess { fetchedConversations ->
                    _conversations.clear()
                    _conversations.addAll(fetchedConversations)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _isLoading.value = false
        }
    }

    private fun updateConversationWithNewMessage(conversationId: String, lastMessage: String, time: String) {
        val index = _conversations.indexOfFirst { it.id == conversationId }
        if (index != -1) {
            val conv = _conversations[index]
            _conversations[index] = conv.copy(
                lastMessage = lastMessage,
                lastMessageTime = time,
                unreadCount = conv.unreadCount + 1
            )
            // Move to top
            val updated = _conversations.removeAt(index)
            _conversations.add(0, updated)
        }
    }

    private fun updateUserOnlineStatus(userId: String, isOnline: Boolean, lastSeen: String) {
        val index = _conversations.indexOfFirst { it.otherUser.id == userId }
        if (index != -1) {
            val conv = _conversations[index]
            _conversations[index] = conv.copy(
                otherUser = conv.otherUser.copy(
                    isOnline = isOnline,
                    lastSeen = lastSeen
                )
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}
