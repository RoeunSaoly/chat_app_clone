package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.Conversation
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.data.repository.ChatRepository
import com.example.chat_app_clone.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserSearchUiState(
    val query: String = "",
    val users: List<User> = emptyList(),
    val selectedUserIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdConversation: Conversation? = null
)

class UserSearchViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSearchUiState(isLoading = true))
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        search("")
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250)
            search(query)
        }
    }

    fun startPrivateChat(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            chatRepository.startPrivateChat(user.id)
                .onSuccess { conversation ->
                    _uiState.value = _uiState.value.copy(isLoading = false, createdConversation = conversation)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun toggleUser(userId: Long) {
        val selected = _uiState.value.selectedUserIds
        _uiState.value = _uiState.value.copy(
            selectedUserIds = if (userId in selected) selected - userId else selected + userId
        )
    }

    fun createGroup(name: String) {
        val memberIds = _uiState.value.selectedUserIds.toList()
        if (name.isBlank() || memberIds.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Group name and at least one member are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            chatRepository.createGroup(name.trim(), memberIds)
                .onSuccess { conversation ->
                    _uiState.value = _uiState.value.copy(isLoading = false, createdConversation = conversation)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun consumeCreatedConversation() {
        _uiState.value = _uiState.value.copy(createdConversation = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.searchUsers(query)
                .onSuccess { users ->
                    _uiState.value = _uiState.value.copy(users = users, isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }
}
