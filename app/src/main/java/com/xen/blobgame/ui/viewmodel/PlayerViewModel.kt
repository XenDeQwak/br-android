package com.xen.blobgame.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.remote.NewPlayer
import com.xen.blobgame.data.remote.PlayerModel
import com.xen.blobgame.data.remote.UpdatePlayer
import com.xen.blobgame.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
): ViewModel() {

    private val _currentPlayer = MutableStateFlow<PlayerModel?>(null)
    val currentPlayer: StateFlow<PlayerModel?> = _currentPlayer.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun createPlayer(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val player = playerRepository.createPlayer(NewPlayer(name = name))
                _currentPlayer.value = player
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error creating player", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePlayer(id: Int, player: UpdatePlayer) {
        viewModelScope.launch {
            try {
                playerRepository.updatePlayer(id, player)
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error updating player", e)
            }
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