package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.GamePlayerRole
import kotlinx.serialization.Serializable

@Serializable
data class NewGameState(
    val date: String = "",
    val title: String = "",
    val items: List<NewGamePlayerItem> = emptyList(),
    val totalPlayers: List<PlayerState> = emptyList(),
    val availablePlayers: List<PlayerState> = emptyList(),
) {
    fun toStartData() = StartGameData(items, date, title)

    val isItemsFilled : Boolean
        get() = items.all { it.role != GamePlayerRole.None && it.player != SelectPlayerState.None }

    val rolesCount: List<Pair<GamePlayerRole, Int>>
        get() = items
            .groupBy { it.role }
            .map { (role, items) -> role to items.size }
            .filterNot { (role, count) -> count == 0 || role == GamePlayerRole.None }
            .sortedByDescending { (role, count) -> role.priority() * 100 + count  }
}