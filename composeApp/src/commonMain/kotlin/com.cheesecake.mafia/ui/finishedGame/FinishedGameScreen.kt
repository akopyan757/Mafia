package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.components.finishedGame.FinishedGameComponent
import com.cheesecake.mafia.data.ApiResult
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.resultText
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
    val isDeleted by viewModel.isDeleted.collectAsState()

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onBackPressed()
        }
    }

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
                    text = protocol.finishResult.resultText(),
                    style = MaterialTheme.typography.h5,
                    color = BlackDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.deleteGame()
                        onBackPressed()
                    },
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Icon(
                        painter = imageResources("ic_delete.xml"),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
            GameStanding(
                modifier = Modifier,
                standingState = GameStandingState(
                    id = 0,
                    status = GameStatus.Finished,
                    round = protocol.lastRound.toInt(),
                    dayType = protocol.lastDayType,
                    isShowRoles = true
                ),
                itemsCount = protocol.players.size,
                itemContent = { index ->
                    val player = protocol.players[index]
                    FinishedGameItem(
                        modifier = Modifier,
                        player = player,
                        lastRound = protocol.lastRound.toInt(),
                        lastDayType = protocol.lastDayType,
                    )
                }
            )
        }
    }
}