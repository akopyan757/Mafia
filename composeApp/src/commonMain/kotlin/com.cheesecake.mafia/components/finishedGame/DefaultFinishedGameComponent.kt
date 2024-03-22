package com.cheesecake.mafia.components.finishedGame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue

class DefaultFinishedGameComponent(
    componentContext: ComponentContext,
    gameId: Long,
    private val onBackPressedClicked: () -> Unit,
): FinishedGameComponent, ComponentContext by componentContext {

    override val model: MutableValue<FinishedGameComponent.Model> = MutableValue(
        FinishedGameComponent.Model(gameId)
    )

    override fun onBackPressed() {
        onBackPressedClicked()
    }
}