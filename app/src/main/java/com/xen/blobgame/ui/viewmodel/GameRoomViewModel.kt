package com.xen.blobgame.ui.viewmodel

import android.util.Log
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
            try {
                gameRoomRepository.joinRoom(id, playerId)
            } catch (e: Exception) {
                Log.e("GameRoomViewModel", "Error joining room", e)
            }
        }
    }

    fun createRoom(maxPlayers: RoomRequest) {
        viewModelScope.launch {
            try {
                gameRoomRepository.createRoom(maxPlayers)
            } catch (e: Exception) {
                Log.e("GameRoomViewModel", "Error creating room", e)
            }
        }
    }

    fun updateRoom(id: Int, maxPlayers: RoomRequest) {
        viewModelScope.launch {
            try {
                gameRoomRepository.updateRoom(id, maxPlayers)
            } catch (e: Exception) {
                Log.e("GameRoomViewModel", "Error updating room", e)
            }
        }
    }

    fun deleteRoom(id: Int) {
        viewModelScope.launch {
            try {
                gameRoomRepository.deleteRoom(id)
            } catch (e: Exception) {
                Log.e("GameRoomViewModel", "Error deleting room", e)
            }
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