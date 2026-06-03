package com.example.chat_app_clone.data

import android.content.Context
import android.content.SharedPreferences
import com.example.chat_app_clone.network.NotificationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("messenger_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_ACTIVE_CONVERSATION_ID = "active_conversation_id"
        private const val KEY_NOTIFICATIONS = "notifications"
    }

    fun saveNotifications(notifications: List<NotificationResponse>) {
        val json = gson.toJson(notifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply()
    }

    fun getNotifications(): List<NotificationResponse> {
        val json = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<NotificationResponse>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveActiveConversationId(id: Long) {
        prefs.edit().putLong(KEY_ACTIVE_CONVERSATION_ID, id).apply()
    }

    fun getActiveConversationId(): Long = prefs.getLong(KEY_ACTIVE_CONVERSATION_ID, -1)

    fun clearActiveConversationId() {
        prefs.edit().remove(KEY_ACTIVE_CONVERSATION_ID).apply()
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, access)
            putString(KEY_REFRESH_TOKEN, refresh)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun saveUser(id: Long, username: String) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, id)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1)

    fun saveFcmToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
