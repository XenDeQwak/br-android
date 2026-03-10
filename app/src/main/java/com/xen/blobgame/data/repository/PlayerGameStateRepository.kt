package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.GameStateSTOMP
import com.xen.blobgame.data.remote.serializer.PlayerGameStateModel
import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.remote.serializer.RoomStateMessage
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

class PlayerGameStateRepository(
    private val socket: GameStateSTOMP
) {
    val gameState: SharedFlow<RoomStateMessage> = socket.gameState

    fun connect(roomId: String) {
        socket.connect()
        socket.subscribeToRoom(roomId)
    }

    fun attack(message: AttackMessage) = socket.sendAttack(message)
    fun move(message: MoveMessage) = socket.sendMove(message)
    fun requestInitialState(roomId: String) = socket.sendStartGame(roomId)  // ✅ NEW
    fun disconnect() = socket.unSubscribe()
}