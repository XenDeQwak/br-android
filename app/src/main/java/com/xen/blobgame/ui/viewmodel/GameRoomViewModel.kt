package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.JoinRequest
import com.xen.blobgame.data.remote.RoomRequest
import com.xen.blobgame.data.repository.GameRoomRepository
import kotlinx.coroutines.launch

class GameRoomViewModel(private val gameRoomRepository: GameRoomRepository): ViewModel() {
    fun joinRoom(id: Int, playerId: JoinRequest) {
        viewModelScope.launch {
            gameRoomRepository.joinRoom(id, playerId)
        }
    }

    fun createRoom(maxPlayers: RoomRequest) {
        viewModelScope.launch {
            gameRoomRepository.createRoom(maxPlayers)
        }
    }

    fun updateRoom(id: Int, maxPlayers: RoomRequest) {
        viewModelScope.launch {
            gameRoomRepository.updateRoom(id, maxPlayers)
        }
    }

    fun deleteRoom(id: Int) {
        viewModelScope.launch {
            gameRoomRepository.deleteRoom(id)
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