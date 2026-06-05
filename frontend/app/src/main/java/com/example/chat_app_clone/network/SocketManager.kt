package com.example.chat_app_clone.network

import com.example.chat_app_clone.network.model.MessageResponse
import com.example.chat_app_clone.network.model.MessageSeenEvent
import com.example.chat_app_clone.network.model.MessagesSeenEvent
import com.example.chat_app_clone.network.model.TypingEvent
import com.example.chat_app_clone.network.model.UserOfflineEvent
import com.example.chat_app_clone.network.model.UserOnlineEvent
import com.example.chat_app_clone.network.model.MessageDeletedEvent
import com.example.chat_app_clone.network.model.MessageEditedEvent
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager {

    private var mSocket: Socket? = null
    val gson = Gson()
    private val listeners = mutableMapOf<String, MutableList<(Array<Any>) -> Unit>>()

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
            
            // Re-register all listeners to the new socket
            listeners.forEach { (event, eventListeners) ->
                eventListeners.forEach { listener ->
                    mSocket?.on(event) { args -> listener(args) }
                }
            }

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

    private fun addListener(event: String, listener: (Array<Any>) -> Unit) {
        val eventListeners = listeners.getOrPut(event) { mutableListOf() }
        eventListeners.add(listener)
        mSocket?.on(event) { args -> listener(args) }
    }

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
        addListener(Socket.EVENT_CONNECT) {
            callback()
        }
    }

    fun onDisconnect(callback: () -> Unit) {
        addListener(Socket.EVENT_DISCONNECT) {
            callback()
        }
    }

    fun onConnectError(callback: (String) -> Unit) {
        addListener(Socket.EVENT_CONNECT_ERROR) { args ->
            val error = args.firstOrNull()?.toString() ?: "Connection error"
            callback(error)
        }
    }

    fun onNewMessage(callback: (MessageResponse) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val message = gson.fromJson(json, MessageResponse::class.java)
                    callback(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("receive_message", listener)
        addListener("new_message", listener)
    }

    fun onMessageDelivered(callback: (JSONObject) -> Unit) {
        addListener("message_delivered") { args ->
            try {
                val data = args.firstOrNull()
                if (data is JSONObject) {
                    callback(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onMessageDeleted(callback: (MessageDeletedEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, MessageDeletedEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("message_deleted", listener)
    }

    fun onMessageEdited(callback: (MessageEditedEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, MessageEditedEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("message_edited", listener)
    }

    fun onTyping(callback: (TypingEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, TypingEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("typing", listener)
    }

    fun onStopTyping(callback: (TypingEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, TypingEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("stop_typing", listener)
    }

    fun onMessageSeen(callback: (MessageSeenEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, MessageSeenEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("message_seen", listener)
    }

    fun onMessagesSeen(callback: (MessagesSeenEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, MessagesSeenEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("messages_seen", listener)
    }

    fun onUserOnline(callback: (UserOnlineEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, UserOnlineEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("user_online", listener)
    }

    fun onUserOffline(callback: (UserOfflineEvent) -> Unit) {
        val listener: (Array<Any>) -> Unit = { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0]
                    val json = if (data is JSONObject) data.toString() else gson.toJson(data)
                    val event = gson.fromJson(json, UserOfflineEvent::class.java)
                    callback(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        addListener("user_offline", listener)
    }

    fun onNewNotification(callback: (JSONObject) -> Unit) {
        addListener("new_notification") { args ->
            try {
                val data = args.firstOrNull()
                if (data is JSONObject) {
                    callback(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onError(callback: (String) -> Unit) {
        addListener("error") { args ->
            val message = args.firstOrNull()?.toString() ?: "Unknown error"
            callback(message)
        }
    }

    fun removeAllListeners() {
        mSocket?.off()
        listeners.clear()
    }
}
