package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
data class GamePlayerData(
    val playerId: Long = EMPTY_ID,
    val gameId: Long = 0L,
    val number: Byte,
    val name: String,
    val role: GamePlayerRole,
    val isWinner: Boolean,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val isNewPlayer: Boolean = true,
    val actions: List<GameAction> = emptyList(),
    val bestMove: List<Int> = emptyList(),
) {
    companion object {
        const val EMPTY_ID = -1L
    }
}

