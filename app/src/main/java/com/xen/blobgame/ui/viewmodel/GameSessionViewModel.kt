package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.GameSocketManager
import com.xen.blobgame.data.remote.PlayerUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameSessionViewModel : ViewModel() {

    private val socketManager = GameSocketManager()
    private var playerId: Int = -1
    private var roomId: Int = -1

    fun startSession(playerId: Int, roomId: Int) {
        this.playerId = playerId
        this.roomId = roomId
    }

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

    }

    fun move(dx: Float, dy: Float) {

        val json = """
    {
        "action": "move",
        "playerId": $playerId,
        "roomId": $roomId,
        "dx": $dx,
        "dy": $dy
    }
    """
        socketManager.sendMove(json)
    }

    fun attack() {

        val json = """
    {
        "action": "attack",
        "playerId": $playerId,
        "roomId": $roomId
    }
    """
        socketManager.sendAttack(json)
    }

    override fun onCleared() {
        socketManager.disconnect()
    }
}