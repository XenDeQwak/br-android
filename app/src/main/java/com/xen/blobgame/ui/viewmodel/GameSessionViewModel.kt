package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.GameSocketManager
import com.xen.blobgame.data.remote.PlayerUi
import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.remote.serializer.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class GameSessionViewModel : ViewModel() {

    private val socketManager = GameSocketManager()
    private val _selectedTarget = MutableStateFlow<Int?>(null)
    val selectedTarget: StateFlow<Int?> = _selectedTarget
    private var playerId: Int = -1
    private var roomId: Int = -1
    private val json = Json { ignoreUnknownKeys = true }

    fun startSession(playerId: Int, roomId: Int) {
        this.playerId = playerId
        this.roomId = roomId
        connect()
    }

    fun selectedTarget(id: Int) { _selectedTarget.value = id }

    private val _players = MutableStateFlow<List<PlayerUi>>(emptyList())
    val players: StateFlow<List<PlayerUi>> = _players.asStateFlow()

    fun connect() {
        socketManager.connect()

        viewModelScope.launch {
            socketManager.messages.collect { message ->
                handleServerMessage(message)
            }
        }
    }

    private fun handleServerMessage(message: String) {
        viewModelScope.launch {
            try {
                val updatedPlayers = json.decodeFromString<List<PlayerUi>>(message)
                _players.value = updatedPlayers.map {it}
            } catch (e: Exception) {
            }
        }
    }

    fun move(dx: Float, dy: Float) {
        if (playerId == -1 || roomId == -1) return

        val message = MoveMessage(
            roomId = roomId,
            playerId = playerId,
            position = Position(
                x = dx,
                y = dy
            )
        )
        val payload = json.encodeToString(message)
        socketManager.sendMove(payload)
    }

    fun attack() {
        val targetId = _selectedTarget.value ?: return
        val message = AttackMessage(
            roomId = roomId,
            attackerId = playerId,
            targetId = targetId
        )
        val payload = json.encodeToString(message)
        socketManager.sendAttack(payload)
    }

    override fun onCleared() {
        socketManager.disconnect()
    }
}