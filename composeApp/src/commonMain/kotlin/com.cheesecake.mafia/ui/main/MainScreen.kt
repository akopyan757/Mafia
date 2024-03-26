package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.ui.VerticalDivider
import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen(component: MainComponent) {
    val viewModel = koinInject<MainViewModel>()
    MainScreen(
        viewModel,
        onNewGameClicked = component::omStartNewGameClicked,
        onGameItemClicked = component::onGameClicked,
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNewGameClicked: () -> Unit,
    onGameItemClicked: (gameId: Long) -> Unit,
) {
    val gameItems by viewModel.gameItems.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { onNewGameClicked() },
            colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark, contentColor = Color.White)
        ) {
            Text(
                "Начать новую игру",
                style = MaterialTheme.typography.body1,
                color = White
            )
        }
        Row(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.padding(8.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(gameItems) { gameData ->
                    Surface {
                        Row(
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text(
                                text = gameData.date,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                            )
                            VerticalDivider(
                                color = WhiteLight,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                text = gameData.title,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                            )
                            VerticalDivider(
                                color = WhiteLight,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                text = gameData.totalTime.timeFormat(),
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }
}

private fun Long.timeFormat(): String {
    val seconds = this % 60
    val minutes = this / 60 % 60
    val secondsValue = if (seconds < 10) "0$seconds" else "$seconds"
    val hours = this / 3600
    return if (hours > 0L) {
        val minutesValue = if (minutes < 10) "0$minutes" else "$minutes"
        "${hours}:${minutesValue}:${secondsValue}"
    } else {
        "${minutes}:${secondsValue}"
    }
}
