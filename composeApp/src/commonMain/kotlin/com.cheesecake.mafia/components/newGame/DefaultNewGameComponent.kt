package com.cheesecake.mafia.components.newGame

import com.arkivanov.decompose.ComponentContext
import com.cheesecake.mafia.state.StartData

class DefaultNewGameComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: (StartData.NewGame) -> Unit = {},
    private val onBackPressed: () -> Unit = {},
): NewGameComponent, ComponentContext by componentContext {

    override fun onBackClicked() {
        onBackPressed()
    }

    override fun onStartNewGameClicked(data: StartData.NewGame) {
        onStartNewGame(data)
    }
}