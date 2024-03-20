package com.cheesecake.mafia.state

data class GameStandingState(
    val id: Int,
    val status: GameStatus,
    val result: GameFinishResult? = null,
    val round: Int,
    val stage: LiveStage,
    val isShowRoles: Boolean,
)