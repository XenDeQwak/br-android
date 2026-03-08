package com.xen.blobgame.data.remote

import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

data class PlayerModel (
    val id: UUID,
    val name: String,
    val numOfKills: Int,
    val sessionId: String,
    val playerGameState: PlayerGameStateModel
)

const val playerEndpoint = "/player"

interface PlayerApi {
    @POST("$playerEndpoint/create")
    suspend fun createPlayer(@Query("name") name: String): PlayerModel
    //TODO: GAMEPLAY API INTEGRATION AND REPOS
}