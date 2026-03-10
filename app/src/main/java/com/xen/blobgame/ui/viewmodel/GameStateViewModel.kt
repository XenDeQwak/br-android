package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xen.blobgame.data.remote.serializer.AttackMessage
import com.xen.blobgame.data.remote.serializer.MoveMessage
import com.xen.blobgame.data.repository.PlayerGameStateRepository
import java.util.UUID

class GameStateViewModel(
    private val repository: PlayerGameStateRepository
) : ViewModel() {

    val gameState = repository.gameState

    fun connect(roomId: String) { repository.connect(roomId) }
    fun attack(message: AttackMessage) { repository.attack(message) }
    fun move(message: MoveMessage) { repository.move(message) }
    fun requestInitialState(roomId: String) { repository.requestInitialState(roomId) }  // ✅ NEW
    fun disconnect() { repository.disconnect() }
    override fun onCleared() { repository.disconnect() }

    class Factory(private val repository: PlayerGameStateRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GameStateViewModel(repository) as T
    }
}