package com.xen.blobgame.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class GameSocketManager {
    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/game")
            .build()

        webSocket = OkHttpClient()
            .newWebSocket(request, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    sendConnectFrame()
                    subscribe("/topic/game-actions")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (text.startsWith("MESSAGE")) {
                        val body = text.substringAfter("\n\n")
                            .removeSuffix("\u0000")
                        CoroutineScope(Dispatchers.IO).launch {
                            _messages.emit(body)
                        }
                    }
                }
            })
    }

    private fun sendConnectFrame() {
        webSocket?.send(
            "CONNECT\naccept-version:1.2\nhost:localhost\n\n\u0000"
        )
    }
    private fun subscribe(destination: String) {
        webSocket?.send(
            "SUBSCRIBE\nid:sub-0\ndestination:$destination\n\n\u0000"
        )
    }
    fun sendAttack(json: String) {
        webSocket?.send(
            "SEND\ndestination:/app/attack\ncontent-type:application/json\n\n$json\u0000"
        )
    }
    fun disconnect() {
        webSocket?.close(1000, null)
    }

    fun sendMove(json: String) {
        webSocket?.send(
            "SEND\ndestination:/app/move\ncontent-type:application/json\n\n$json\u0000"
        )
    }
}