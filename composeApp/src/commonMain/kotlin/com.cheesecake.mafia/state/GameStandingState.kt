package com.cheesecake.mafia.state

import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameFinishResult

data class GameStandingState(
    val id: Int,
    val status: GameStatus,
    val result: GameFinishResult? = null,
    val round: Int,
    val dayType: DayType,
    val isShowRoles: Boolean,
)