package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class FinishedGameProtocolState(
    val id: Int,
    val title: String,
    val date: String,
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

fun buildProtocol(
    startGameData: StartGameData,
    liveGameState: LiveGameState,
    history: List<HistoryItem>,
    finishResult: GameFinishResult,
    totalTime: Int,
) = FinishedGameProtocolState(
    id = Random(value.hashCode()).nextInt(),
    title = startGameData.title,
    date = startGameData.date,
    players = liveGameState.players.map { it.toFinishedGamePlayer() },
    lastRound = liveGameState.round,
    lastDayType = liveGameState.stage.type,
    history = history,
    finishResult = finishResult,
    totalTime = totalTime,
)

fun LivePlayerState.toFinishedGamePlayer(): FinishedGamePlayersState {
    return FinishedGamePlayersState(
        playerId, number, name, role, isAlive, isDeleted, actions
    )
}


