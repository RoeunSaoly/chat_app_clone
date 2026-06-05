package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app_clone.data.repository.AuthRepository
import com.example.chat_app_clone.network.model.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.network.UserApi
import com.example.chat_app_clone.network.UpdateProfileRequest
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val preferenceManager: PreferenceManager,
    private val socketManager: SocketManager,
    private val userApi: UserApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            repository.login(email, password)
                .onSuccess { response ->
                    if (response.success && response.accessToken != null) {
                        // Save tokens and user info
                        preferenceManager.saveTokens(response.accessToken, response.refreshToken ?: "")
                        response.user?.let { user ->
                            preferenceManager.saveUser(user.id, user.username ?: "")
                        }
                        
                        // Connect socket
                        socketManager.connectSocket(response.accessToken)
                        
                        // Handle FCM Token
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                preferenceManager.saveFcmToken(token)
                                viewModelScope.launch {
                                    try {
                                        userApi.updateProfile(UpdateProfileRequest(fcmToken = token))
                                    } catch (e: Exception) {
                                        Log.e("LoginViewModel", "Failed to update FCM token", e)
                                    }
                                }
                            }
                        }
                        
                        _uiState.value = LoginUiState(isSuccess = true)
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
