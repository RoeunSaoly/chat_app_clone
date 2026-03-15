package com.example.chat_app_clone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.chat_app_clone.navigation.NavGraph
import com.example.chat_app_clone.ui.theme.Chat_app_cloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Chat_app_cloneTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}