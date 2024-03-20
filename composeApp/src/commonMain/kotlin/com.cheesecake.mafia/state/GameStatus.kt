package com.cheesecake.mafia.state

enum class GameStatus {
    NewGame,
    Live,
    Finished,
}

enum class GameFinishResult {
    WhiteWin,
    RedWin,
    BlackWin,
}