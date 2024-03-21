package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.components.finishedGame.FinishedGameComponent
import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.GameFinishResult

@Composable
fun FinishedGameScreen(component: FinishedGameComponent) {
    val protocol by component.model.subscribeAsState()
    FinishedGameScreen(protocol.model, component::onBackPressed)
}

@Composable
fun FinishedGameScreen(
    protocol: FinishedGameProtocolState,
    onBackPressed: () -> Unit = {}
) {
    val text = when (protocol.finishResult) {
        GameFinishResult.BlackWin -> "Победа мафии"
        GameFinishResult.RedWin -> "Победа мирного города"
        GameFinishResult.WhiteWin -> "Победа маньяка"
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.h5,
            color = BlackDark,
        )
    }
}