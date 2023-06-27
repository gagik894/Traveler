package com.together.traveler.ui.chat.singleChatScreen

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket


fun createSocketConnection(): Socket {
    val socket = IO.socket("http://localhost:3333/")
    try {
        // Connect the socket
        socket.connect()
        Log.i("Socket", "Connect")
    } catch (e: Exception) {
        // Handle the error
        Log.e("Socket", "Error connecting to socket", e)
    }

    return socket
}