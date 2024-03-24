package com.cheesecake.mafia.data

import com.cheesecake.mafia.common.Item

data class LivePlayerData(
    val playerId: Long = 0L,
    val number: Int = 1,
    val name: String = "",
    val role: GamePlayerRole = GamePlayerRole.None,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val isClient: Boolean = false,
    val isVoted: Boolean = false,
    val isKilled: Boolean = false,
    val actions: List<GameAction> = emptyList(),
    val fouls: Int = 0,
): Item<Int> {
    override val key: Int get() = number
}