package com.example.chat_app_clone.viewmodel

import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.data.repository.ChatRepository
import com.example.chat_app_clone.data.repository.UserRepository
import com.example.chat_app_clone.network.NotificationResponse
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<NotificationResponse> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val userRepository: UserRepository = UserRepository()
    private val chatRepository: ChatRepository = ChatRepository.getInstance()
    private val prefManager = PreferenceManager(application)
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        // Load persisted notifications first
        val cached = prefManager.getNotifications()
        if (cached.isNotEmpty()) {
            _uiState.update { it.copy(notifications = cached, unreadCount = cached.count { !it.isRead }) }
        }
        
        loadNotifications()
        observeNewNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.getNotifications()
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            notifications = data.notifications,
                            unreadCount = data.unreadCount,
                            isLoading = false
                        )
                    }
                    // Save to preferences
                    prefManager.saveNotifications(data.notifications)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun observeNewNotifications() {
        viewModelScope.launch {
            chatRepository.newNotifications.collect { notification ->
                // Check if this notification should be ignored (e.g., user is in that chat)
                if (notification.type == "message") {
                    val activeId = prefManager.getActiveConversationId()
                    if (activeId == notification.relatedId) {
                        return@collect
                    }
                }

                _uiState.update { state ->
                    val updatedList = listOf(notification) + state.notifications
                    // Save updated list to preferences
                    prefManager.saveNotifications(updatedList)
                    
                    state.copy(
                        notifications = updatedList,
                        unreadCount = state.unreadCount + 1
                    )
                }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            userRepository.markNotificationRead(notificationId).onSuccess {
                _uiState.update { state ->
                    val updatedList = state.notifications.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                    // Save updated list to preferences
                    prefManager.saveNotifications(updatedList)
                    
                    state.copy(
                        notifications = updatedList,
                        unreadCount = (state.unreadCount - 1).coerceAtLeast(0)
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            userRepository.markAllNotificationsRead().onSuccess {
                _uiState.update { state ->
                    val updatedList = state.notifications.map { it.copy(isRead = true) }
                    // Save updated list to preferences
                    prefManager.saveNotifications(updatedList)
                    
                    state.copy(
                        notifications = updatedList,
                        unreadCount = 0
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
