package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextUInt

@Serializable
data class GameData(
    val id: Long = 0L,
    val title: String,
    val date: String,
    val lastRound: Byte,
    val lastDayType: DayType,
    val finishResult: GameFinishResult,
    val totalTime: Long,
    val players: List<GamePlayerData>
) {
    fun generateIds(): GameData {
        val gameId = Random(hashCode()).nextUInt().toLong()
        val players = players.map {
            val playerId = Random(it.name.hashCode()).nextUInt().toLong()
            if (it.isNewPlayer) {
                it.copy(gameId = gameId, playerId = playerId, name = it.name)
            } else {
                it.copy(gameId = gameId, playerId = it.playerId)
            }
        }
        return copy(id = gameId, players = players)
    }

    fun newPlayers(): List<PlayerData> = players.filter { it.isNewPlayer }.map {
        PlayerData(id = it.playerId, name = it.name)
    }
}


