package com.xen.blobgame.data.remote.serializer

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PlayerGameStateModel(
    val health: Int,
    val damage: Int,
    val posX: Float,
    val posY: Float,
    val isDead: Boolean,
)

@Serializable
data class PlayerModel(
    val id: @Serializable(with = UUIDSerializer::class) UUID,
    val name: String,
    val numOfKills: Int,
    val sessionId: String,
    val playerGameState: PlayerGameStateModel
)

@Serializable
data class RoomStateMessage(
    val players: List<PlayerModel>,
)

@Serializable
data class MoveMessage(
    val playerId: @Serializable(with = UUIDSerializer::class) UUID,
    val roomId:   @Serializable(with = UUIDSerializer::class) UUID,
    val posX: Float,
    val posY: Float,
)

@Serializable
data class AttackMessage(
    val roomId:     @Serializable(with = UUIDSerializer::class) UUID,
    val attackerId: @Serializable(with = UUIDSerializer::class) UUID,
    val targetId:   @Serializable(with = UUIDSerializer::class) UUID,
)