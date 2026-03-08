package com.xen.blobgame.data.remote

import java.util.UUID

data class PlayerModel (
    val id: UUID,
    val name: String,
    val numOfKills: Int,
    val sessionId: String,
    val playerGameState: PlayerGameStateModel
)


class PlayerApi {
}