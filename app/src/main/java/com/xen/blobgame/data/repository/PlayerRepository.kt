package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.NewPlayer
import com.xen.blobgame.data.remote.PlayerApi
import com.xen.blobgame.data.remote.UpdatePlayer

class PlayerRepository(private val api: PlayerApi) {
    suspend fun createPlayer(player: NewPlayer) = api.createPlayer(player)
    suspend fun updatePlayer(id: Int, player: UpdatePlayer) = api.updatePlayer(id, player)
}