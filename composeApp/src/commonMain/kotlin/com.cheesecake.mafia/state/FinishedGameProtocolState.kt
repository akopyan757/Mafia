package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import kotlin.random.Random

fun buildProtocol(
    startGameData: StartGameData,
    liveGameState: LiveGameState,
    finishResult: GameFinishResult,
    totalTime: Long,
): GameData {
    val gameId = Random(startGameData.hashCode()).nextInt().toLong()
    return GameData(
        id = gameId,
        title = startGameData.title,
        date = startGameData.date,
        players = liveGameState.players.map { it.toPlayerGameData(gameId, finishResult) },
        lastRound = liveGameState.round,
        lastDayType = liveGameState.stage.type,
        finishResult = finishResult,
        totalTime = totalTime,
    )
}

fun LivePlayerState.toPlayerGameData(
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
        playerId, gameId = gameId, number, name, role, isWinner, isAlive, isDeleted, actions
    )
}


