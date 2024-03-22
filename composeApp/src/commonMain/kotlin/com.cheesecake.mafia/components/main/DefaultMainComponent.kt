package com.cheesecake.mafia.components.main

import com.arkivanov.decompose.ComponentContext

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: () -> Unit = {},
    private val onGameItemClicked: (gameId: Long) -> Unit = {},
): MainComponent, ComponentContext by componentContext {

    override fun omStartNewGameClicked() {
        onStartNewGame()
    }

    override fun onGameClicked(gameId: Long) {
        onGameItemClicked(gameId)
    }
}