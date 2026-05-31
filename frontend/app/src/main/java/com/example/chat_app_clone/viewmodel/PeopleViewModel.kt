package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PeopleUiState(
    val recommendedFriends: List<User> = emptyList(),
    val friendRequests: List<User> = emptyList(),
    val friends: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PeopleViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recommended = repository.getRecommendedFriends().getOrNull() ?: emptyList()
                val requests = repository.getFriendRequests().getOrNull() ?: emptyList()
                val friends = repository.getFriends().getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        recommendedFriends = recommended.take(20),
                        friendRequests = requests,
                        friends = friends,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun sendFriendRequest(userId: Long) {
        viewModelScope.launch {
            repository.sendFriendRequest(userId).onSuccess {
                // Optionally refresh or update local state
                loadAll()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun acceptFriendRequest(userId: Long) {
        viewModelScope.launch {
            repository.acceptFriendRequest(userId).onSuccess {
                loadAll()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun rejectFriendRequest(userId: Long) {
        viewModelScope.launch {
            repository.rejectFriendRequest(userId).onSuccess {
                loadAll()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun unfriend(userId: Long) {
        viewModelScope.launch {
            repository.unfriend(userId).onSuccess {
                loadAll()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
