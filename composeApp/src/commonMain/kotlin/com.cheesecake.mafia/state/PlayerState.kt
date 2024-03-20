package com.cheesecake.mafia.state

@kotlinx.serialization.Serializable
data class PlayerState(
    val id: Int, val name: String
)

@kotlinx.serialization.Serializable
sealed class SelectPlayerState(val name: String, val id: Int) {
    data object None: SelectPlayerState("", -1)
    data class New(val value: String): SelectPlayerState(value, 0)
    data class Exist(val player: PlayerState): SelectPlayerState(player.name, player.id)
}

@kotlinx.serialization.Serializable
data class NewGamePlayerItem(
    val number: Int = 0,
    val player: SelectPlayerState = SelectPlayerState.None,
    val role: GamePlayerRole = GamePlayerRole.None,
)