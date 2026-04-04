package com.example.chat_app_clone.ui.Screens // Fixed case sensitivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.chat_app_clone.ui.components.BottomMenu

@androidx.compose.runtime.Composable
fun MainScreen() {
    var selectedIndex by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }

    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomMenu(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { padding ->
        // Fill the whole screen and center the content
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> androidx.compose.material3.Text("Chats Screen", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                1 -> androidx.compose.material3.Text("Contacts Screen", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                2 -> androidx.compose.material3.Text("Settings Screen", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            }
        }
    }
}