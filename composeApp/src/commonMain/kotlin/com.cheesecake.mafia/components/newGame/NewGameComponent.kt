package com.cheesecake.mafia.components.newGame

import com.cheesecake.mafia.state.StartData

interface NewGameComponent {
    fun onBackClicked()
    fun onStartNewGameClicked(data: StartData.NewGame) {}
}