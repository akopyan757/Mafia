package com.cheesecake.mafia.data

sealed class InteractiveScreenState {
    data object None: InteractiveScreenState()
    data object Main: InteractiveScreenState()
    data class LiveGame(val state: LiveGameData): InteractiveScreenState()
    data object NewGame: InteractiveScreenState()
    data class FinishGame(val state: GameData?): InteractiveScreenState()
}