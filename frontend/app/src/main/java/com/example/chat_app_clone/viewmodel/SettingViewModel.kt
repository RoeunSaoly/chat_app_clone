package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.model.User
import com.example.chat_app_clone.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.network.SocketManager

data class SettingUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: UserRepository,
    private val preferenceManager: PreferenceManager,
    private val socketManager: SocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun logout() {
        preferenceManager.clear()
        socketManager.disconnect()
        _uiState.value = _uiState.value.copy(isLoggedOut = true)
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getProfile()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
