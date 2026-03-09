package com.xen.blobgame.data.remote

import com.xen.blobgame.data.remote.serializer.AttackMessage
import io.reactivex.disposables.Disposable
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.UUID

class GameStateSTOMP {
    private val stompClient: StompClient =
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2/ws")
    private var roomSubscription: Disposable? = null
    private var lifeCycleDisposable: Disposable? = null

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

    fun subscribeToRoom(roomId: UUID) {
        roomSubscription = stompClient
            .topic("/topic/room/$roomId")
            .subscribe(
                { message: StompMessage ->
                    val payload = message.payload
                    println(payload)
                },
                { error ->
                    println("Subscription error: ${error.message}")
                }
            )
    }

    fun sendAttack(message: AttackMessage) {
        val json = Json.encodeToString(message)
        stompClient.send("/app/attack", json).subscribe()
    }

    fun unSubscribe() {
        lifeCycleDisposable?.dispose()
        lifeCycleDisposable = null
        roomSubscription?.dispose()
        roomSubscription = null
        stompClient.disconnect()
    }



}