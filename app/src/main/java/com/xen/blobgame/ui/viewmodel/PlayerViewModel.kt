package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.PlayerModel
import com.xen.blobgame.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlayerViewModel(private val repository: PlayerRepository): ViewModel() {
    private val _currentPlayer = MutableStateFlow<PlayerModel?>(null)
    val currentPlayer: StateFlow<PlayerModel?> = _currentPlayer.asStateFlow()
    fun createPlayer(name: String) {
        viewModelScope.launch {
            val player = repository.createPlayer(name)
            println("PLAYER ID: ${player.id}")
            _currentPlayer.value = player
        }
    }

    class Factory(private val repository: PlayerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}