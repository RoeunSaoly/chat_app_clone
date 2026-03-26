package com.example.chat_app_clone

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
import com.example.chat_app_clone.navigation.NavGraph
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.ui.theme.Chat_app_cloneTheme
import io.socket.client.Socket

class MainActivity : ComponentActivity() {
    // Initialize the SocketManager
    private val socketManager = SocketManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Socket Connection (Use a placeholder for now)
        socketManager.connectSocket("your_actual_jwt_token_here")

        // 2. Set up Socket Listeners
        val socket = socketManager.getSocket()
        socket?.on(Socket.EVENT_CONNECT) {
            Log.d("SocketIO", "Successfully connected to the server!")
        }

        socket?.on("newMessage") { args ->
            if (args.isNotEmpty()) {
                val data = args[0].toString()
                Log.d("SocketIO", "New Message received: $data")
            }
        }

        // 3. Enable Full Screen (Edge-to-Edge)
        enableEdgeToEdge()

        // 4. Set the UI Content
        setContent {
            Chat_app_cloneTheme {
                // We use a Scaffold to handle system bar padding properly
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pass the padding to your NavGraph or main container
                    NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Good practice: Disconnect socket when activity is destroyed
        socketManager.getSocket()?.disconnect()
    }
}

/**
 * A Preview function to see your UI in the Android Studio Design tab.
 */
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    Chat_app_cloneTheme {
        NavGraph() // ✅ modifier has a default value
    }
}
