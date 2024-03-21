package com.cheesecake.mafia.components.newGame

import com.cheesecake.mafia.state.StartGameData

interface NewGameComponent {
    fun onBackClicked()
    fun onStartGameClicked(data: StartGameData) {}
}