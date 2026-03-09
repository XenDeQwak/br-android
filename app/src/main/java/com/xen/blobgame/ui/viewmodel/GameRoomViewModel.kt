package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.GameRoomModel
import com.xen.blobgame.data.remote.GameRoomRequest
import com.xen.blobgame.data.remote.PlayerModel
import com.xen.blobgame.data.repository.GameRoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameRoomViewModel(private val repository: GameRoomRepository): ViewModel() {

    private val _currentRoom = MutableStateFlow<GameRoomModel?>(null)
    val currentRoom = _currentRoom.asStateFlow()
    fun createRoom(playerId: GameRoomRequest, maxPlayers: Int, roomName: String) {
        viewModelScope.launch {
            val room = repository.createRoom(playerId, maxPlayers, roomName)
            _currentRoom.value = room
        }
    }

    fun joinRoom(roomCode: String, playerId: GameRoomRequest) {
        viewModelScope.launch {
            val room = repository.joinRoom(roomCode, playerId)
            _currentRoom.value = room
        }
    }

    class Factory(private val repository: GameRoomRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameRoomViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameRoomViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}