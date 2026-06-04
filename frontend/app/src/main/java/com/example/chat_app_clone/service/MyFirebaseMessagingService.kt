package com.example.chat_app_clone.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.chat_app_clone.MainActivity
import com.example.chat_app_clone.R
import com.example.chat_app_clone.data.PreferenceManager
import com.example.chat_app_clone.network.UserApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var userApi: UserApi

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to preferences to be sent to server later
        preferenceManager.saveFcmToken(token)
        
        // If user is already logged in, send token to server
        val currentToken = preferenceManager.getAccessToken()
        if (currentToken != null) {
            sendTokenToServer(token, currentToken)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val type = data["type"]
        val conversationId = data["conversationId"]?.toLongOrNull()
        
        // If it's a message and user is already in that chat, don't show notification
        if (type == "message" && conversationId != null) {
            val activeConversationId = preferenceManager.getActiveConversationId()
            if (activeConversationId == conversationId) {
                return
            }
        }

        // Check if message contains a notification payload.
        // We do not show a system notification popup when the app is in the foreground
        /*
        remoteMessage.notification?.let {
            val title = it.title ?: "New Notification"
            val body = it.body ?: ""
            sendNotification(title, body, data)
        }
        */
    }

    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        // Add data to intent for navigation
        for ((key, value) in data) {
            intent.putExtra(key, value)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "chat_app_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat App Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun sendTokenToServer(fcmToken: String, authToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userApi.updateProfile(
                    com.example.chat_app_clone.network.UpdateProfileRequest(fcmToken = fcmToken)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
