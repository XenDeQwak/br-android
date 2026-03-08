package com.xen.blobgame.data.remote

data class PlayerGameStateModel (
    val health: Int,
    val damage: Int,
    val posX: Double,
    val posY: Double,
    val isDead: Boolean,
)