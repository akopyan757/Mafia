package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.NewGamePlayerItem

interface LiveGameComponent {

    val model: MutableValue<Model>

    fun onFinishGameClicked(protocol: FinishedGameProtocolState)

    data class Model(
        val model: List<NewGamePlayerItem>
    )
}