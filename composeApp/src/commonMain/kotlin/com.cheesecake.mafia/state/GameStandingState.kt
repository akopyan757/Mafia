package com.cheesecake.mafia.state

data class GameStandingState(
    val id: Int,
    val status: GameStatus,
    val stage: GameStageState,
    val isShowRoles: Boolean,
)