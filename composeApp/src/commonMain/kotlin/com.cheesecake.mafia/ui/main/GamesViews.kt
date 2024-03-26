package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.background
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
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.resultText

@Composable
fun GamesViews(
    modifier: Modifier = Modifier,
    dates: List<String> = emptyList(),
    gameItems: List<GameData> = emptyList(),
    onGameItemClicked: (gameId: Long) -> Unit = {},
) {
    var dateTabIndex by remember { mutableStateOf(0) }
    val filteredGameItems by derivedStateOf {
        gameItems.filter { it.date == dates[dateTabIndex] }
    }
    Column(modifier) {
        if (dates.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = dateTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                backgroundColor = Color.Transparent,
            ) {
                dates.forEachIndexed { index, date ->
                    Tab(
                        modifier = if (dateTabIndex == index) {
                            Modifier.background(BlackDark)
                        } else {
                            Modifier.background(GrayLight)
                        },
                        selected = dateTabIndex == index,
                        onClick = { dateTabIndex = index },
                        content = {
                            Text(
                                text = date,
                                modifier = Modifier.padding(8.dp),
                                color = White,
                            )
                        }
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(filteredGameItems) { gameData ->
                Surface(modifier = Modifier.clickable { onGameItemClicked(gameData.id) }) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        Column(
                            modifier = Modifier.defaultMinSize(minWidth = 140.dp)
                                .padding(start = 8.dp),
                        ) {
                            Text(
                                text = gameData.title,
                                style = MaterialTheme.typography.body1,
                            )
                            Text(
                                text = gameData.totalTime.timeFormat(),
                                style = MaterialTheme.typography.body2,
                                color = GrayLight,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                        Text(
                            text = gameData.finishResult.resultText(),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                        )
                    }
                }
            }
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