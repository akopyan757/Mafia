package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable

@Serializable
data class FinishedGameProtocolState(
    val players: List<FinishedGamePlayersState>,
    val lastRound: Int,
    val lastDayType: StageDayType,
    val history: List<HistoryItem> = emptyList(),
    val finishResult: GameFinishResult,
    val totalTime: Int,
)

@Serializable
data class FinishedGamePlayersState(
    val playerId: Long,
    val number: Int = 1,
    val name: String = "",
    val role: GamePlayerRole = GamePlayerRole.None,
    val isAlive: Boolean = true,
    val isDeleted: Boolean = false,
    val actions: List<GameAction> = emptyList(),
)

fun buildFinishedProtocol(
    liveGameState: LiveGameState,
    history: List<HistoryItem>,
    finishResult: GameFinishResult,
    totalTime: Int,
): FinishedGameProtocolState {
    return FinishedGameProtocolState(
        liveGameState.players.map { it.toFinishedGamePlayer() },
        liveGameState.round,
        liveGameState.stage.type,
        history,
        finishResult,
        totalTime,
    )
}

fun LivePlayerState.toFinishedGamePlayer(): FinishedGamePlayersState {
    return FinishedGamePlayersState(
        playerId, number, name, role, isAlive, isDeleted, actions
    )
}


