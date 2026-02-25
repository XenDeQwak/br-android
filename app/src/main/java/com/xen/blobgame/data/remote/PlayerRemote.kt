package com.xen.blobgame.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.time.LocalDateTime

data class PlayerModel (
    val id: Int,
    val name: String,
    val numOfWins: Int,
    val numOfLoss: Int,
    val createdAt: LocalDateTime,
    val gameRoom: Int? //later change to GameRoomModel once GameRoomRemote is implemented
)

data class NewPlayer (
    val name: String,
    val numOfWins: Int = 0,
    val numOfLoss: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class UpdatePlayer ( val name: String )

const val playerEndPoint = "/players"

interface PlayerApi {
    @POST("$playerEndPoint/create")
    suspend fun createPlayer(@Body player: NewPlayer): PlayerModel

    @PUT("$playerEndPoint/update/{id}")
    suspend fun updatePlayer(
        @Path("id") id: Int,
        @Body player: UpdatePlayer
    ): PlayerModel
}