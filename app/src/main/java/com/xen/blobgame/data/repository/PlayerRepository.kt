package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.PlayerApi

class PlayerRepository (private val api: PlayerApi){
    suspend fun createPlayer(name: String) = api.createPlayer(name)
}