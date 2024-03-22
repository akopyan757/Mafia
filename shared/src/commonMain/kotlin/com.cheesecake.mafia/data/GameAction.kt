package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
data class GameAction(
    val dayIndex: Int = 0,
    val actionType: GameActionType,
)