package com.xen.blobgame.data.repository

import com.xen.blobgame.data.remote.GameRoomApi
import com.xen.blobgame.data.remote.JoinRequest
import com.xen.blobgame.data.remote.RoomRequest

class GameRoomRepository(private val api: GameRoomApi) {
    suspend fun joinRoom(id: Int, playerId: JoinRequest) = api.joinRoom(id, playerId)
    suspend fun createRoom(maxPlayers: RoomRequest) = api.createRoom(maxPlayers)
    suspend fun updateRoom(id: Int, maxPlayers: RoomRequest) = api.updateRoom(id, maxPlayers)
    suspend fun deleteRoom(id: Int) = api.deleteRoom(id);

}