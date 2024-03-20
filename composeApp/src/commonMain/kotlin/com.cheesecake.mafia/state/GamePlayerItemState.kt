package com.cheesecake.mafia.state

data class GamePlayerItemState(
    val playerId: Int,
    val number: Int = 1,
    val name: String = "",
    val role: GamePlayerRole = GamePlayerRole.None,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val isClient: Boolean = false,
    val actions: List<GameAction> = emptyList(),
    val fouls: Int = 0,
    val points: Float = 0f,
)