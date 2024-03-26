package com.cheesecake.mafia.ui.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.cheesecake.mafia.components.root.RootComponent
import com.cheesecake.mafia.ui.finishedGame.FinishedGameScreen
import com.cheesecake.mafia.ui.liveGame.LiveGameScreen
import com.cheesecake.mafia.ui.main.MainAdminScreen
import com.cheesecake.mafia.ui.main.MainUserScreen
import com.cheesecake.mafia.ui.newGame.NewGameScreen

@Composable
fun RootUserScreen(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    MaterialTheme {
        Box(modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            Children(
                stack = component.stack,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (val instance = it.instance) {
                    is RootComponent.Child.Main -> MainUserScreen(component = instance.component)
                    else -> {}
                }
            }
        }
    }
}