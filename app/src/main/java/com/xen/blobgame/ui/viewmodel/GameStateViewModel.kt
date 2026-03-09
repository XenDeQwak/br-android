package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.repository.PlayerGameStateRepository

class GameStateViewModel(
    private val repository: PlayerGameStateRepository
) : ViewModel() {

    val gameState = repository.gameState

    fun connect(roomId: String) { repository.connect(roomId) }
    fun attack(message: AttackMessage) { repository.attack(message) }
    fun move(message: MoveMessage) { repository.move(message) }
    fun disconnect() { repository.disconnect() }
}