package com.xen.blobgame.data.remote.serializer

import kotlinx.serialization.Serializable

@Serializable
data class Position (
    val x: Float,
    val y: Float
)

@Serializable
data class AttackMessage (
    val roomId: String,
    val attackerId: String,
    val targetId: String
)

@Serializable
data class MoveMessage (
    val roomId: String,
    val playerId: String,
    val position: Position
)