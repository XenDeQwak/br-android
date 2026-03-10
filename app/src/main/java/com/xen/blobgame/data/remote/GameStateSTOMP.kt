package com.xen.blobgame.data.remote

import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.remote.serializer.PlayerGameStateModel
import com.xen.blobgame.data.remote.serializer.RoomStateMessage
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

class GameStateSTOMP {
    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws")
    private var roomSubscription: Disposable? = null
    private var lifeCycleDisposable: Disposable? = null
    private val _gameState = MutableSharedFlow<RoomStateMessage>()
    val gameState = _gameState.asSharedFlow()
    private val json = Json { ignoreUnknownKeys = true }

    fun connect() {
        stompClient.connect()

        lifeCycleDisposable = stompClient.lifecycle().subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED ->
                    println("STOMP connected")

                LifecycleEvent.Type.ERROR ->
                    println("STOMP error: ${event.exception}")

                LifecycleEvent.Type.CLOSED ->
                    println("STOMP disconnected")

                else -> {}
            }
        }
    }

    fun subscribeToRoom(roomId: String) {
        println("🔵 Subscribing to /topic/room/$roomId")
        roomSubscription = stompClient
            .topic("/topic/room/$roomId")
            .subscribe(
                { message: StompMessage ->
                    println("✅ Received message on /topic/room/$roomId")
                    val payload = message.payload
                    println("📦 Payload: $payload")
                    try {
                        val state = json.decodeFromString<RoomStateMessage>(payload)
                        println("✅ Parsed RoomStateMessage with ${state.players.size} players")
                        _gameState.tryEmit(state)
                    } catch (e: Exception) {
                        println("❌ Failed to parse: ${e.message}")
                        println("Exception: ${e.stackTrace}")
                    }
                },
                { error ->
                    println("❌ Subscription error: ${error.message}")
                    error.printStackTrace()
                }
            )
    }

    fun sendAttack(message: AttackMessage) {
        val json = json.encodeToString(message)
        stompClient.send("/app/attack", json).subscribe()
    }

    fun sendMove(message: MoveMessage) {
        val json = json.encodeToString(message)
        stompClient.send("/app/move", json).subscribe()
    }

    fun unSubscribe() {
        lifeCycleDisposable?.dispose()
        lifeCycleDisposable = null
        roomSubscription?.dispose()
        roomSubscription = null
        stompClient.disconnect()
    }



}