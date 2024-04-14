package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.resultText
import com.cheesecake.mafia.ui.custom.PrimitiveTabRow

@Composable
fun LiveGamesViews(
    modifier: Modifier = Modifier,
    liveGames: List<LiveGameData>,
    onGameItemClicked: (LiveGameData) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(liveGames) { gameData ->
            GameItem(
                modifier = Modifier,
                title = gameData.title,
                timer = gameData.totalTime,
                finishResult = GameFinishResult.None,
                onClicked = { onGameItemClicked(gameData) }
            )
        }
    }
}

@Composable
fun FinishedGamesViews(
    modifier: Modifier = Modifier,
    dates: List<String> = emptyList(),
    gameItems: List<GameData> = emptyList(),
    onGameItemClicked: (gameId: Long) -> Unit = {},
) {
    var selectedDate by remember { mutableStateOf("") }
    val filteredGameItems by derivedStateOf {
        gameItems.filter { it.date == selectedDate }
    }
    Column(modifier) {
        if (dates.isNotEmpty()) {
            PrimitiveTabRow(
                modifier = Modifier.fillMaxWidth(),
                values = dates,
                onValueSelected = { selectedDate = it },
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(filteredGameItems) { gameData ->
                GameItem(
                    modifier = Modifier,
                    title = gameData.title,
                    timer = gameData.totalTime,
                    finishResult = gameData.finishResult,
                    onClicked = { onGameItemClicked(gameData.id) }
                )
            }
        }
    }
}

@Composable
private fun GameItem(
    modifier: Modifier = Modifier,
    title: String = "",
    timer: Long = 0L,
    finishResult: GameFinishResult = GameFinishResult.None,
    onClicked: () -> Unit = {},
) {
    Surface(modifier = modifier.clickable { onClicked() }) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier.defaultMinSize(minWidth = 140.dp)
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = timer.timeFormat(),
                    style = MaterialTheme.typography.body2,
                    color = GrayLight,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.wrapContentWidth()
                )
            }
            Text(
                text = finishResult.resultText(),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
            )
        }
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