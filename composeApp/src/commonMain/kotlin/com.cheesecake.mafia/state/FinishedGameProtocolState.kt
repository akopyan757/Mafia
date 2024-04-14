package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import kotlin.random.Random

fun buildProtocol(
    state: LiveGameData,
    totalTime: Long,
): GameData {
    val winner = state.winner ?: GameFinishResult.None
    return GameData(
        id = state.id,
        title = state.title,
        date = state.date,
        players = state.players.map { it.toPlayerGameData(state.id, winner) },
        lastRound = state.round,
        lastDayType = state.stage.dayType,
        finishResult = winner,
        totalTime = totalTime,
    )
}

fun LivePlayerData.toPlayerGameData(
    gameId: Long,
    finishResult: GameFinishResult,
): GamePlayerData {
    val isWinner = when {
        role is GamePlayerRole.White && finishResult == GameFinishResult.WhiteWin -> true
        role is GamePlayerRole.Red && finishResult == GameFinishResult.RedWin -> true
        role is GamePlayerRole.Black && finishResult == GameFinishResult.BlackWin -> true
        else -> false
    }
    return GamePlayerData(
        playerId, gameId, number.toByte(), name, role, isWinner, isAlive, isDeleted, isNewPlayer,
        actions, bestMove
    )
}


