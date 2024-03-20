package com.cheesecake.mafia.components.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.cheesecake.mafia.components.liveGame.LiveGameComponent
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.components.newGame.NewGameComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class Main(val component: MainComponent) : Child()
        class NewGame(val component: NewGameComponent) : Child()
        class LiveGame(val component: LiveGameComponent) : Child()
    }
}