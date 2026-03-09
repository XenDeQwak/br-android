package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.GameRoomRequest

class GameRoomRepository(private val api: GameRoomApi) {
    suspend fun createRoom(
        maxPlayers: Int,
        roomName: String,
        playerId: GameRoomRequest) = api.createRoom(maxPlayers, roomName, playerId)
    suspend fun joinRoom(
        roomCode: String,
        playerId: GameRoomRequest) = api.joinRoom(roomCode, playerId)
}