package com.cheesecake.mafia.state

data class GameStandingState(
    val id: Int,
    val status: GameStatus,
    val round: Int,
    val stage: LiveStage,
    val isShowRoles: Boolean,
)