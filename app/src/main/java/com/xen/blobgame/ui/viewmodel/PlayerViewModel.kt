package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.NewPlayer
import com.xen.blobgame.data.remote.UpdatePlayer
import com.xen.blobgame.data.repository.PlayerRepository
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
): ViewModel() {
    fun addPlayer(player: NewPlayer) {
        viewModelScope.launch {
            playerRepository.createPlayer(player)
        }
    }

    fun updatePlayer(id: Int, player: UpdatePlayer) {
        viewModelScope.launch {
            playerRepository.updatePlayer(id, player)
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