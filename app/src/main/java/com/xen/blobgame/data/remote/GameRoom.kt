package com.xen.blobgame.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

data class GameRoomModel (
    private val id: String,
    private val roomCode: String,
    private val status: Status,
    private val name: String,
    private val maxPlayers: Int,
    private val numOfPlayers: Int
)

data class GameRoomRequest(
    val playerId: String?
)


const val roomEndPoint = "/game-room"

interface GameRoomApi {
    @POST("$roomEndPoint/create")
    suspend fun createRoom(@Query("maxPlayers") maxPlayers: Int,
                           @Query("roomName") roomName: String,
                           @Body gameRoomRequest: GameRoomRequest
    ): GameRoomModel

    @POST("$roomEndPoint/join")
    suspend fun joinRoom(
        @Query("roomCode") roomCode: String,
        @Body gameRoomRequest: GameRoomRequest
    ): GameRoomModel
}
