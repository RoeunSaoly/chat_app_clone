package com.example.chat_app_clone

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chat_app_clone.network.RetrofitClient
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.ui.theme.Chat_app_cloneTheme

import com.example.chat_app_clone.navigation.NavGraph

class MainActivity : ComponentActivity() {

    private val socketManager = SocketManager.getInstance()
    private val prefManager by lazy { com.example.chat_app_clone.data.PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Networking with Token Refresh Support
        RetrofitClient.init(
            tokenProvider = { prefManager.getRefreshToken() },
            refreshListener = { access, refresh ->
                prefManager.saveTokens(access, refresh)
                socketManager.disconnect()
                socketManager.connectSocket(access)
            }
        )

        // Restore session
        val accessToken = prefManager.getAccessToken()
        if (!accessToken.isNullOrEmpty()) {
            RetrofitClient.setAuthToken(accessToken)
            socketManager.connectSocket(accessToken)
            Log.d("MainActivity", "Session restored")
        }

        enableEdgeToEdge()

        setContent {
            Chat_app_cloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    companion object {
        fun saveToken(context: Context, access: String, refresh: String) {
            com.example.chat_app_clone.data.PreferenceManager(context).saveTokens(access, refresh)
        }

        fun saveCurrentUserId(context: Context, userId: Long, username: String) {
            com.example.chat_app_clone.data.PreferenceManager(context).saveUser(userId, username)
        }

        fun getCurrentUserId(context: Context): String {
            return com.example.chat_app_clone.data.PreferenceManager(context).getUserId().toString()
        }

        fun logout(context: Context) {
            com.example.chat_app_clone.data.PreferenceManager(context).clear()
            SocketManager.getInstance().disconnect()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    Chat_app_cloneTheme {
        NavGraph()
    }
}
