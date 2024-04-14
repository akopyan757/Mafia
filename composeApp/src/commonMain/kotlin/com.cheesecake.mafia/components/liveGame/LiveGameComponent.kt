package com.cheesecake.mafia.components.liveGame

import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.StartData

interface LiveGameComponent {

    val model: MutableValue<Model>

    fun onFinishGameClicked(gameId: Long)

    fun onBackToMenuClicked()

    data class Model(val data: StartData)
}