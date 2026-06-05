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

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val preferenceManager: PreferenceManager,
    private val socketManager: SocketManager,
    private val userApi: UserApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            repository.register(username, email, password)
                .onSuccess { response ->
                    if (response.success) {
                        // Auto-login after successful registration
                        repository.login(email, password)
                            .onSuccess { loginResponse ->
                                if (loginResponse.success && loginResponse.accessToken != null) {
                                    // Save tokens and user info
                                    preferenceManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken ?: "")
                                    loginResponse.user?.let { user ->
                                        preferenceManager.saveUser(user.id, user.username ?: "")
                                    }
                                    
                                    // Connect socket
                                    socketManager.connectSocket(loginResponse.accessToken)
                                    
                                    // Handle FCM Token
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val token = task.result
                                            preferenceManager.saveFcmToken(token)
                                            viewModelScope.launch {
                                                try {
                                                    userApi.updateProfile(UpdateProfileRequest(fcmToken = token))
                                                } catch (e: Exception) {
                                                    Log.e("RegisterViewModel", "Failed to update FCM token", e)
                                                }
                                            }
                                        }
                                    }
                                    
                                    _uiState.value = RegisterUiState(isSuccess = true)
                                } else {
                                    _uiState.value = RegisterUiState(error = loginResponse.error ?: "Login failed")
                                }
                            }
                            .onFailure { exception ->
                                _uiState.value = RegisterUiState(error = exception.message ?: "Login failed")
                            }
                    } else {
                        _uiState.value = RegisterUiState(error = response.error ?: "Registration failed")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = RegisterUiState(error = exception.message ?: "An error occurred")
                }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
