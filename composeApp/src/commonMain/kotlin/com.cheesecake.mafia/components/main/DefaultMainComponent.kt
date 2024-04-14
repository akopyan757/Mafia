package com.cheesecake.mafia.components.main

import com.arkivanov.decompose.ComponentContext
import com.cheesecake.mafia.data.LiveGameData

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: () -> Unit = {},
    private val onResumeGame: (LiveGameData) -> Unit = {},
    private val onGameItemClicked: (gameId: Long) -> Unit = {},
): MainComponent, ComponentContext by componentContext {

    override fun omStartNewGameClicked() {
        onStartNewGame()
    }

    override fun onResumeGameClicked(liveGameData: LiveGameData) {
        onResumeGame(liveGameData)
    }

    override fun onGameClicked(gameId: Long) {
        onGameItemClicked(gameId)
    }
}