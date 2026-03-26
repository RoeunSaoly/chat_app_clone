package com.example.chat_app_clone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chat_app_clone.ui.theme.Chat_app_cloneTheme
import io.socket.client.Socket
import android.util.Log

import com.example.chat_app_clone.navigation.NavGraph
import com.example.chat_app_clone.navigation.Screen

class MainActivity : ComponentActivity() {
    private val socketManager = SocketManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        socketManager.connectSocket("your_actual_jwt_token_here")
        // Assuming you successfully connected:
        val socket = socketManager.getSocket()
        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("SocketIO", "Connected to Socket.IO server!")
        }
        socket?.on("newMessage") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as String
                Log.d("SocketIO", "New Message received: $data")
            }
        }
        
        enableEdgeToEdge()
        setContent {
            Chat_app_cloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(startDestination = Screen.Profile.createRoute("1"))
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeCarousel(modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to Chat App\nConnect with your friends easily.",
        // FIXED: Use 16.dp directly
        modifier = modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CarouselPreview() {
    Chat_app_cloneTheme {
        WelcomeCarousel()
    }
}