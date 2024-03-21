package com.cheesecake.mafia.state

import com.cheesecake.mafia.viewModel.Item

data class LivePlayerState(
    val playerId: Long,
    val number: Int = 1,
    val name: String = "",
    val role: GamePlayerRole = GamePlayerRole.None,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val isClient: Boolean = false,
    val actions: List<GameAction> = emptyList(),
    val fouls: Int = 0,
): Item<Int> {
    override val key: Int get() = number
}