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
    private val prefs by lazy { getSharedPreferences("chat_app", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore token and initialize socket
        val token = prefs.getString("auth_token", null)
        if (!token.isNullOrEmpty()) {
            RetrofitClient.setAuthToken(token)
            RetrofitClient.rebuild()
            socketManager.connectSocket(token)
            Log.d("SocketIO", "Restored socket connection with token")
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
        fun saveToken(context: Context, token: String) {
            context.getSharedPreferences("chat_app", Context.MODE_PRIVATE)
                .edit()
                .putString("auth_token", token)
                .apply()
        }

        fun clearToken(context: Context) {
            context.getSharedPreferences("chat_app", Context.MODE_PRIVATE)
                .edit()
                .remove("auth_token")
                .apply()
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
