package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerRole

data class LiveGameState(
    val id: Int = 1,
    val players: List<LivePlayerState> = emptyList(),
    val round: Int = 0,
    val stage: LiveStage = LiveStage.Start,
    val queueStage: List<LiveStage> = emptyList(),
    val voteCandidates: List<Int> = emptyList(),
    val nightActions: Map<GameActionType.NightActon, Int> = emptyMap(),
) {
    val winner: GameFinishResult?
        get() {
            val whiteCount = players.filter { it.role is GamePlayerRole.White && it.isAlive }.size
            val redCount = players.filter { it.role is GamePlayerRole.Red && it.isAlive }.size
            val blackCount = players.filter { it.role is GamePlayerRole.Black && it.isAlive }.size
            return when {
                whiteCount == 0 && blackCount == 0 && redCount > 0 -> GameFinishResult.RedWin
                whiteCount >= redCount + blackCount -> GameFinishResult.WhiteWin
                blackCount >= redCount + whiteCount -> GameFinishResult.BlackWin
                else -> null
            }
        }

    val totalVotes: Int get() = players.filter { it.isAlive && !it.isClient }.size

    val deleteCandidates: List<Int>
        get() = players.filter { it.isAlive && it.fouls == 4 }.map { it.number }

    val lastClientPlayer: Int?
        get() {
            val clientChosen = nightActions[GameActionType.NightActon.ClientChoose]
            return clientChosen?.takeIf { clientChosen !in lastKilledPlayers }
        }

    val lastKilledPlayers: List<Int>
        get() {
            val roles = players.associateBy(keySelector = { it.role }) { it.number }
            val mafiaKilling = nightActions[GameActionType.NightActon.MafiaKilling]
            val maniacKilling = nightActions[GameActionType.NightActon.ManiacKilling]
            val clientChosen = nightActions[GameActionType.NightActon.ClientChoose]
            val doctorSaving = nightActions[GameActionType.NightActon.Doctor]
            return if (
                roles[GamePlayerRole.Red.Whore] == mafiaKilling ||
                roles[GamePlayerRole.Red.Whore] == maniacKilling
            ) {
                listOfNotNull(mafiaKilling, maniacKilling, clientChosen)
                    .filterNot { number -> doctorSaving == number }
            } else {
                listOfNotNull(mafiaKilling, maniacKilling)
                    .filterNot { number -> doctorSaving == number || clientChosen == number }
            }
        }
}