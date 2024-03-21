package com.cheesecake.mafia.state

import kotlinx.serialization.Serializable

enum class GameStatus {
    NewGame,
    Live,
    Finished,
}

@Serializable
enum class GameFinishResult {
    WhiteWin,
    RedWin,
    BlackWin,
}