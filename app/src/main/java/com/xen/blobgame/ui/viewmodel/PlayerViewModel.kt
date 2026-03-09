package com.xen.blobgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xen.blobgame.data.repository.PlayerRepository
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: PlayerRepository): ViewModel() {
    fun createPlayer(name: String) {
        viewModelScope.launch {
            repository.createPlayer(name)
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