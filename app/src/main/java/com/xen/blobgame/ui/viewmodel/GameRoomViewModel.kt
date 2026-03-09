package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.GameRoomRequest
import com.xen.blobgame.data.repository.GameRoomRepository
import kotlinx.coroutines.launch

class GameRoomViewModel(private val repository: GameRoomRepository): ViewModel() {
    fun createRoom(maxGameRooms: Int, roomName: String, playerId: GameRoomRequest) {
        viewModelScope.launch {
            repository.createRoom(maxGameRooms, roomName, playerId)
        }
    }

    fun joinRoom(roomCode: String, playerId: GameRoomRequest) {
        viewModelScope.launch {
            repository.joinRoom(roomCode, playerId)
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