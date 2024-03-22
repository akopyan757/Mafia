package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.ApiResult
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.components.finishedGame.FinishedGameComponent
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.viewModel.FinishedGameViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun FinishedGameScreen(component: FinishedGameComponent) {
    val model by component.model.subscribeAsState()
    val viewModel = koinInject<FinishedGameViewModel> { parametersOf(model.gameId) }
    FinishedGameScreen(viewModel, component::onBackPressed)
}

@Composable
fun FinishedGameScreen(
    viewModel: FinishedGameViewModel,
    onBackPressed: () -> Unit = {}
) {
    val result by viewModel.gameDataResult.collectAsState()
    if (result is ApiResult.Loading) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
           Button(
               onClick = onBackPressed,
               colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
           ) {
               Text(text = "Назад", style = MaterialTheme.typography.body1, color = Color.White)
           }
        }
    } else if (result is ApiResult.Success<GameData>) {
        val protocol = (result as ApiResult.Success<GameData>).data
        val text = when (protocol.finishResult) {
            GameFinishResult.BlackWin -> "Победа мафии"
            GameFinishResult.RedWin -> "Победа мирного города"
            GameFinishResult.WhiteWin -> "Победа маньяка"
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Button(
                    onClick = onBackPressed,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Text(text = "Назад", style = MaterialTheme.typography.body1, color = Color.White)
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.h5,
                    color = BlackDark,
                )
            }
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
}