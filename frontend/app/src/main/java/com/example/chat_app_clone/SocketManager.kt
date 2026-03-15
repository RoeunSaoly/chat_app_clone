package com.example.chat_app_clone

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketManager {

    private var mSocket: Socket? = null

    fun connectSocket(jwtToken: String) {
        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to jwtToken)
            }
            mSocket = IO.socket("http://10.0.2.2:5000", options)
            mSocket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getSocket(): Socket? {
        return mSocket
    }

    fun disconnect() {
        mSocket?.disconnect()
    }
}
