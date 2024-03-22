package com.cheesecake.mafia.data

data class GamePlayerData(
    val playerId: Long,
    val gameId: Long,
    val number: Int,
    val name: String,
    val role: GamePlayerRole,
    val isWinner: Boolean,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val actions: List<GameAction> = emptyList(),
)

