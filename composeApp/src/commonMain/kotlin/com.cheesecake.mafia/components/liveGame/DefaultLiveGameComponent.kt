package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.StartData

class DefaultLiveGameComponent(
    componentContext: ComponentContext,
    startData: StartData,
    private val onFinishGame: (gameId: Long) -> Unit = {},
    private val onBackToMenu: () -> Unit = {},
): LiveGameComponent, ComponentContext by componentContext {

    override val model: MutableValue<LiveGameComponent.Model> = MutableValue(
        LiveGameComponent.Model(startData)
    )

    override fun onFinishGameClicked(gameId: Long) {
        onFinishGame(gameId)
    }

    override fun onBackToMenuClicked() {
        onBackToMenu()
    }
}