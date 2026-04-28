package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.MessageResponse
import com.example.chat_app_clone.network.model.MessageSeenEvent
import com.example.chat_app_clone.network.model.MessagesSeenEvent
import com.example.chat_app_clone.network.model.TypingEvent
import com.example.chat_app_clone.network.model.UserOfflineEvent
import com.example.chat_app_clone.network.model.UserOnlineEvent
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager private constructor() {

    private var mSocket: Socket? = null
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: SocketManager? = null

        fun getInstance(): SocketManager {
            return instance ?: synchronized(this) {
                instance ?: SocketManager().also { instance = it }
            }
        }
    }

    fun connectSocket(jwtToken: String) {
        if (mSocket?.connected() == true) return

        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to jwtToken)
                reconnection = true
                reconnectionAttempts = 10
                reconnectionDelay = 1000
                reconnectionDelayMax = 5000
            }
            mSocket = IO.socket(NetworkConfig.SOCKET_URL, options)
            mSocket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket = null
    }

    fun isConnected(): Boolean = mSocket?.connected() == true

    // --- Emit methods ---

    fun joinConversation(conversationId: String) {
        mSocket?.emit("join_conversation", conversationId)
    }

    fun leaveConversation(conversationId: String) {
        mSocket?.emit("leave_conversation", conversationId)
    }

    fun sendMessage(conversationId: String, content: String, messageType: String = "text") {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
            put("content", content)
            put("message_type", messageType)
        }
        mSocket?.emit("send_message", data)
    }

    fun startTyping(conversationId: String) {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
        }
        mSocket?.emit("typing", data)
    }

    fun stopTyping(conversationId: String) {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
        }
        mSocket?.emit("stop_typing", data)
    }

    fun markMessageSeen(messageId: Long) {
        val data = JSONObject().apply {
            put("message_id", messageId)
        }
        mSocket?.emit("message_seen", data)
    }

    fun markAllSeen(conversationId: String) {
        val data = JSONObject().apply {
            put("conversation_id", conversationId)
        }
        mSocket?.emit("mark_all_seen", data)
    }

    // --- Listener registration ---

    fun onConnect(callback: () -> Unit) {
        mSocket?.on(Socket.EVENT_CONNECT) {
            callback()
        }
    }

    fun onDisconnect(callback: () -> Unit) {
        mSocket?.on(Socket.EVENT_DISCONNECT) {
            callback()
        }
    }

    fun onConnectError(callback: (String) -> Unit) {
        mSocket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val error = args.firstOrNull()?.toString() ?: "Connection error"
            callback(error)
        }
    }

    fun onNewMessage(callback: (MessageResponse) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val message = gson.fromJson(json, MessageResponse::class.java)
                callback(message)
            }
        }
        mSocket?.on("receive_message", listener)
        mSocket?.on("new_message", listener)
    }

    fun onMessageDelivered(callback: (JSONObject) -> Unit) {
        mSocket?.on("message_delivered") { args ->
            args.firstOrNull()?.let { callback(it as JSONObject) }
        }
    }

    fun onTyping(callback: (TypingEvent) -> Unit) {
        mSocket?.on("typing") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, TypingEvent::class.java)
                callback(event)
            }
        }
    }

    fun onStopTyping(callback: (TypingEvent) -> Unit) {
        mSocket?.on("stop_typing") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, TypingEvent::class.java)
                callback(event)
            }
        }
    }

    fun onMessageSeen(callback: (MessageSeenEvent) -> Unit) {
        mSocket?.on("message_seen") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, MessageSeenEvent::class.java)
                callback(event)
            }
        }
    }

    fun onMessagesSeen(callback: (MessagesSeenEvent) -> Unit) {
        mSocket?.on("messages_seen") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, MessagesSeenEvent::class.java)
                callback(event)
            }
        }
    }

    fun onUserOnline(callback: (UserOnlineEvent) -> Unit) {
        mSocket?.on("user_online") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, UserOnlineEvent::class.java)
                callback(event)
            }
        }
    }

    fun onUserOffline(callback: (UserOfflineEvent) -> Unit) {
        mSocket?.on("user_offline") { args ->
            if (args.isNotEmpty()) {
                val json = gson.toJson(args[0])
                val event = gson.fromJson(json, UserOfflineEvent::class.java)
                callback(event)
            }
        }
    }

    fun onError(callback: (String) -> Unit) {
        mSocket?.on("error") { args ->
            val message = args.firstOrNull()?.toString() ?: "Unknown error"
            callback(message)
        }
    }

    fun removeAllListeners() {
        mSocket?.off()
    }
}
