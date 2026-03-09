package com.xen.blobgame.data.remote

import retrofit2.http.POST
import java.util.UUID

data class GameRoomModel (
    private val id: UUID,
    private val roomCode: String,
    private val status: Status,
    private val name: String,
    private val maxPlayers: Int,
    private val numOfPlayers: Int
)

data class NewGameRoom(
    val playerId: UUID,
    val roomName: String,
    val maxPlayers: Int
)

data class JoinGameRoom(
    val playerId: UUID,
    val roomCode: String
)

const val roomEndPoint = "/room"

interface GameRoomApi {
    @POST("$roomEndPoint/create")
    suspend fun createRoom(newRoom: NewGameRoom): GameRoomModel

    @POST("$roomEndPoint/join")
    suspend fun joinRoom(joinRoom: JoinGameRoom): GameRoomModel
}
