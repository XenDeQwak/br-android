package com.xen.blobgame.data.remote

import androidx.compose.ui.graphics.Color
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
    val gameRoom: GameRoomModel?
)

data class NewPlayer (
    val name: String,
    val numOfWins: Int = 0,
    val numOfLoss: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class PlayerUi(
    val id: Int,
    val x: Float,
    val y: Float,
    val isAlive: Boolean,
    val color: Color
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