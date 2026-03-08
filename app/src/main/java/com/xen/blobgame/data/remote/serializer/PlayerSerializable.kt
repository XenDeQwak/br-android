package com.xen.blobgame.data.remote.serializer

import kotlinx.serialization.Serializable

@Serializable
data class Position (
    val x: Float,
    val y: Float
)

@Serializable
data class AttackMessage (
    val roomId: Int,
    val attackerId: Int,
    val targetId: Int
)

@Serializable
data class MoveMessage (
    val roomId: Int,
    val playerId: Int,
    val position: Position
)