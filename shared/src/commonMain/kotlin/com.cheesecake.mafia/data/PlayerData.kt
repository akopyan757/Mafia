package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val id: Long = -1,
    val name: String,
    val gamesCount: Int = 0,
    val hasPlayedToday: Boolean = false,
)