package com.cheesecake.mafia.data

data class GameData(
    val id: Long,
    val title: String,
    val date: String,
    val lastRound: Int,
    val lastDayType: DayType,
    val finishResult: GameFinishResult,
    val totalTime: Long,
    val players: List<GamePlayerData>
)

