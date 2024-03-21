package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable

@Serializable
data class StartGameData(
    val items: List<NewGamePlayerItem> = emptyList(),
    val date: String = "",
    val title: String = "",
)
