package com.cheesecake.mafia.components.finishedGame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.cheesecake.mafia.state.FinishedGameProtocolState

class DefaultFinishedGameComponent(
    componentContext: ComponentContext,
    protocol: FinishedGameProtocolState,
    private val onBackPressedClicked: () -> Unit,
): FinishedGameComponent, ComponentContext by componentContext {

    override val model: MutableValue<FinishedGameComponent.Model> = MutableValue(
        FinishedGameComponent.Model(protocol)
    )

    override fun onBackPressed() {
        onBackPressedClicked()
    }
}