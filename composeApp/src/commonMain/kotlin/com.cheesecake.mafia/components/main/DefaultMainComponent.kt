package com.cheesecake.mafia.components.main

import com.arkivanov.decompose.ComponentContext

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onStartNewGame: () -> Unit = {},
): MainComponent, ComponentContext by componentContext {

    override fun omStartNewGameClicked() {
        onStartNewGame()
    }
}