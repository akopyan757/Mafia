package com.cheesecake.mafia.components.newGame

import com.arkivanov.decompose.ComponentContext
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.StartGameData

class DefaultNewGameComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: (StartGameData) -> Unit = {},
    private val onBackPressed: () -> Unit = {},
): NewGameComponent, ComponentContext by componentContext {

    override fun onBackClicked() {
        onBackPressed()
    }

    override fun onStartGameClicked(data: StartGameData) {
        onStartNewGame(data)
    }
}