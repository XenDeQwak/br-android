package com.xen.blobgame.data.remote

data class PlayerGameStateModel (
    val health: Int,
    val damage: Int,
    val posX: Double,
    val posY: Double,
    val isDead: Boolean,
)

data class NewPlayerGameState(
    val health: Int = 100,
    val damage: Int = 10,
    val posX: Double = Math.random() * 100,
    val posY: Double = Math.random() * 100,
    val isDead: Boolean = false
)