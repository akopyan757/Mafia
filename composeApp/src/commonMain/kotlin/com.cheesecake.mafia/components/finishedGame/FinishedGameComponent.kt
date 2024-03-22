package com.cheesecake.mafia.components.finishedGame

import com.arkivanov.decompose.value.MutableValue

interface FinishedGameComponent {

    val model: MutableValue<Model>

    fun onBackPressed()

    data class Model(val gameId: Long)
}