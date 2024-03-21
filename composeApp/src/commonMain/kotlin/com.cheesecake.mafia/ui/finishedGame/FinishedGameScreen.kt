package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.components.finishedGame.FinishedGameComponent
import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.GameFinishResult
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.ui.GameStanding

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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.h5,
            color = BlackDark,
        )
        GameStanding(
            modifier = Modifier,
            standingState = GameStandingState(
                id = 0,
                status = GameStatus.Finished,
                round = protocol.lastRound,
                dayType = protocol.lastDayType,
                isShowRoles = true
            ),
            itemsCount = protocol.players.size,
            itemContent = { index ->
                val player = protocol.players[index]
                FinishedGameItem(
                    modifier = Modifier,
                    player = player,
                    lastRound = protocol.lastRound,
                    lastDayType = protocol.lastDayType,
                )
            }
        )
    }
}