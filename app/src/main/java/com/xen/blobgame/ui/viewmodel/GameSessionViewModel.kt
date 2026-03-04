package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.GameSocketManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class GameSessionViewModel : ViewModel() {

    private val socketManager = GameSocketManager()

    val gameUpdates = socketManager.messages
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun connect() {
        socketManager.connect()
    }

    fun attack(attackerId: Int, targetId: Int, roomId: Int, damage: Double) {
        val json = """
            {
                "attackerId": $attackerId,
                "targetId": $targetId,
                "action": "attack",
                "roomId": $roomId,
                "damage": $damage
            }
        """.trimIndent()

        socketManager.sendAttack(json)
    }

    override fun onCleared() {
        socketManager.disconnect()
    }
}