package com.xen.blobgame.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.time.LocalDateTime

data class GameRoomModel (
    val id: Int,
    val status: Status,
    val maxPlayers: Int,
    val numOfPlayers: Int,
    val players: List<PlayerModel>,
    val createdAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?
)

data class JoinRequest (
    val playerId: Int
)

data class RoomRequest (
    val maxPlayers: Int
)

data class CreateRequest (
    val name: String,
    val maxPlayers: Int
)

private const val gameRoomEndPoint = "/game-room"
interface GameRoomApi {
    @POST("$gameRoomEndPoint/join/{id}")
    suspend fun joinRoom(@Path("id") id: Int,
                         @Body playerId: JoinRequest): GameRoomModel

    @POST("$gameRoomEndPoint/create")
    suspend fun createRoom(@Body createRequest: CreateRequest): GameRoomModel

    @PUT("$gameRoomEndPoint/update/{id}")
    suspend fun updateRoom(@Path("id") id: Int,
                           @Body maxPlayers: RoomRequest): GameRoomModel

    @DELETE("$gameRoomEndPoint/delete/{id}")
    suspend fun deleteRoom(@Path("id") id: Int): GameRoomModel


}