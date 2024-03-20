package com.cheesecake.mafia.components.newGame

import com.arkivanov.decompose.ComponentContext
import com.cheesecake.mafia.state.NewGamePlayerItem

class DefaultNewGameComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: (items: List<NewGamePlayerItem>) -> Unit = {},
    private val onBackPressed: () -> Unit = {},
): NewGameComponent, ComponentContext by componentContext {

    override fun onBackClicked() {
        onBackPressed()
    }

    override fun onStartGameClicked(items: List<NewGamePlayerItem>) {
        onStartNewGame(items)
    }
}