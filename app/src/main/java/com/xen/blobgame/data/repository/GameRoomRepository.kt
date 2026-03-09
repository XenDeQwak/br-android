package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.GameRoomRequest

class GameRoomRepository(private val api: GameRoomApi) {
    suspend fun createRoom(
        playerId: GameRoomRequest,
        maxPlayers: Int,
        roomName: String) = api.createRoom( playerId, maxPlayers, roomName,)
    suspend fun joinRoom(
        roomCode: String,
        playerId: GameRoomRequest) = api.joinRoom(roomCode, playerId)
}