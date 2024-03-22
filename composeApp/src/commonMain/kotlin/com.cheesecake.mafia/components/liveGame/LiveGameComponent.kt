package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.StartGameData

interface LiveGameComponent {

    val model: MutableValue<Model>

    fun onFinishGameClicked(gameId: Long)

    data class Model(val data: StartGameData)
}