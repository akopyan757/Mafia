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
    val isItemsFilled : Boolean
        get() = items.all { it.player != SelectPlayerState.None }

    private val filledPlayers: List<NewGamePlayerItem>
        get() = items.map { item ->
            if (item.role is GamePlayerRole.None)
                item.copy(role = GamePlayerRole.Red.Сivilian)
            else
                item
        }

    fun toStartData() = StartGameData(filledPlayers, date, title)

    val rolesCount: List<Pair<GamePlayerRole, Int>>
        get() = filledPlayers
            .groupBy { it.role }
            .map { (role, items) -> role to items.size }
            .filterNot { (role, count) -> count == 0 || role == GamePlayerRole.None }
            .sortedByDescending { (role, count) -> role.priority() * 100 + count  }
}