package com.example.chat_app_clone

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.chat_app_clone.network.UpdateProfileRequest
import com.example.chat_app_clone.network.SocketManager
import com.example.chat_app_clone.ui.theme.Chat_app_cloneTheme

import com.example.chat_app_clone.navigation.NavGraph
import com.example.chat_app_clone.network.UserApi
import com.example.chat_app_clone.data.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var socketManager: SocketManager

    @Inject
    lateinit var prefManager: PreferenceManager

    @Inject
    lateinit var userApi: UserApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore session
        val accessToken = prefManager.getAccessToken()
        if (!accessToken.isNullOrEmpty()) {
            socketManager.connectSocket(accessToken)
            Log.d("MainActivity", "Session restored")
            
            // Get FCM Token and send to server
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                prefManager.saveFcmToken(token)
                
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        userApi.updateProfile(UpdateProfileRequest(fcmToken = token))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        enableEdgeToEdge()

        // Determine start destination based on token presence (persistent login)
        val startDestination = if (!accessToken.isNullOrEmpty()) {
            com.example.chat_app_clone.navigation.Screen.Home.route
        } else {
            com.example.chat_app_clone.navigation.Screen.Welcome.route
        }

        setContent {
            Chat_app_cloneTheme {
                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            Log.d("MainActivity", "Notification permission granted")
                        } else {
                            Log.d("MainActivity", "Notification permission denied")
                        }
                    }

                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    Chat_app_cloneTheme {
        NavGraph()
    }
}
