package com.cheesecake.mafia.components.finishedGame

import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.FinishedGameProtocolState

interface FinishedGameComponent {

    val model: MutableValue<Model>

    fun onBackPressed()

    data class Model(
        val model: FinishedGameProtocolState
    )
}