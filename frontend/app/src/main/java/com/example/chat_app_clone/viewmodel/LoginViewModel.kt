package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.repository.AuthRepository
import com.example.chat_app_clone.network.model.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val authResponse: AuthResponse? = null
)

class LoginViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            repository.login(email, password)
                .onSuccess { response ->
                    if (response.success) {
                        _uiState.value = LoginUiState(authResponse = response)
                    } else {
                        _uiState.value = LoginUiState(error = response.error ?: "Login failed")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = LoginUiState(error = exception.message ?: "An error occurred")
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
