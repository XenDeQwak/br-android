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
import java.util.UUID

class GameStateSTOMP {
    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8081/ws")
    private var roomSubscription: Disposable? = null
    private var lifeCycleDisposable: Disposable? = null

    private val _gameState = MutableSharedFlow<RoomStateMessage>(replay = 1)
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
                        println("🎯 About to emit state with ${state.players.size} players")
                        _gameState.tryEmit(state)
                        println("✅ Emitted successfully")
                    } catch (e: Exception) {
                        println("❌ Failed to parse: ${e.message}")
                        e.printStackTrace()
                    }
                },
                { error ->
                    println("❌ Subscription error: ${error.message}")
                    error.printStackTrace()
                }
            )
    }

    fun sendAttack(message: AttackMessage) {
        val json = Json.encodeToString(message)
        stompClient.send("/app/attack", json).subscribe()
    }

    fun sendMove(message: MoveMessage) {
        val json = Json.encodeToString(message)
        stompClient.send("/app/move", json).subscribe()
    }

    fun sendStartGame(roomId: UUID, playerId: UUID) {
        val message = mapOf(
            "roomId" to roomId.toString(),
            "playerId" to playerId.toString()
        )
        val json = Json.encodeToString(message)
        println("📤 Requesting game start for room: $roomId")
        stompClient.send("/app/start-game", json).subscribe()
    }

    fun unSubscribe() {
        lifeCycleDisposable?.dispose()
        lifeCycleDisposable = null
        roomSubscription?.dispose()
        roomSubscription = null
        stompClient.disconnect()
    }
}