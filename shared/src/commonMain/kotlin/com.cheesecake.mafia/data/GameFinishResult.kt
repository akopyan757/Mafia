package com.cheesecake.mafia.data

import kotlinx.serialization.Serializable

@Serializable
enum class GameFinishResult {
    WhiteWin,
    RedWin,
    BlackWin,
}