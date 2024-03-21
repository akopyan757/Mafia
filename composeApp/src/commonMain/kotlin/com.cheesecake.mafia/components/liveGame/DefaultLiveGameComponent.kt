package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.NewGamePlayerItem

class DefaultLiveGameComponent(
    componentContext: ComponentContext,
    players: List<NewGamePlayerItem>,
    private val onFinishGame: (protocol: FinishedGameProtocolState) -> Unit = {},
): LiveGameComponent, ComponentContext by componentContext {

    //private val _model = MutableValue(LiveGameComponent.Model(players))
    override val model: MutableValue<LiveGameComponent.Model> = MutableValue(
        LiveGameComponent.Model(
            players
        )
    )

    override fun onFinishGameClicked(protocol: FinishedGameProtocolState) {
        onFinishGame(protocol)
    }
}