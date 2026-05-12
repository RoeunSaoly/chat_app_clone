package com.example.chat_app_clone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app_clone.data.repository.ChatRepository

class ChatViewModelFactory(private val currentUserId: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(currentUserId = currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
