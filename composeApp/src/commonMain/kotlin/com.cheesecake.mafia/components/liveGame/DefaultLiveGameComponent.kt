package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.StartGameData

class DefaultLiveGameComponent(
    componentContext: ComponentContext,
    data: StartGameData,
    private val onFinishGame: (gameId: Long) -> Unit = {},
): LiveGameComponent, ComponentContext by componentContext {

    override val model: MutableValue<LiveGameComponent.Model> = MutableValue(
        LiveGameComponent.Model(data)
    )

    override fun onFinishGameClicked(gameId: Long) {
        onFinishGame(gameId)
    }
}