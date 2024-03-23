package com.cheesecake.mafia.ui.second

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.ui.custom.Grid
import com.cheesecake.mafia.viewModel.SecondScreenViewModel
import org.koin.compose.koinInject

@Composable
fun SecondScreen(
    viewModel: SecondScreenViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    SecondLiveScreen(state)
}

data class SquareData(
    val topIndexes: List<Int>,
    val rightIndexes: List<Int>,
    val leftIndexes: List<Int>,
    val bottomIndexes: List<Int>,
)

fun createSquare(count: Int): SquareData {
    val gridColumns = when {
        count >= 19 -> 6
        count in 13..18 -> 5
        count in 9..12 -> 4
        else -> 3
    }
    val gridMiddleRows = maxOf(((count + count % 2) - 2 * gridColumns) / 2, 1)
    val cellsCount = 2 * (gridMiddleRows + gridColumns)
    val offset = if (gridColumns % 2 == 0) gridColumns / 2 else gridColumns / 2 + count % 2
    val filterIndex: ((Int) -> Int) = { if (it in (0 until count)) it else -1 }
    val top = ((cellsCount - offset until cellsCount) +
               (0 until gridColumns - offset).toList()).map(filterIndex)
    val right = (gridColumns - offset until gridColumns + gridMiddleRows - offset).toList().map(filterIndex)
    val bottom  = (gridColumns + gridMiddleRows - offset until 2 * gridColumns + gridMiddleRows - offset).toList().reversed().map(filterIndex)
    val left = (2 * gridColumns + gridMiddleRows - offset until 2 * (gridColumns + gridMiddleRows) - offset).toList().reversed().map(filterIndex)
    return SquareData(top, right, left, bottom)
}

@Composable
fun SecondLiveScreen(liveGameData: LiveGameData) {
    val playersCount = if (liveGameData.players.size < 5) return else liveGameData.players.size
    val (top, right, left, bottom) = createSquare(playersCount)
    val stage = liveGameData.stage
    liveGameData.stage is LiveStage.Day
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Grid(
            columns = top.size,
            modifier = Modifier.fillMaxSize().padding(8.dp),
        ) {
            top.forEach { index ->
                val data = liveGameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    livePlayerData = data
                )
            }
            Column(modifier = Modifier.span(columns = 1, rows = left.size)) {
                left.forEach { index ->
                    val data = liveGameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        livePlayerData = data
                    )
                }
            }
            Column(
                modifier = Modifier.span(columns = top.size - 2, rows = left.size),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                when (stage) {
                    is LiveStage.Day.Vote -> {
                        val text = if (!stage.reVote) "Голосование" else "Переголосование"
                        Text(text, style = MaterialTheme.typography.h5, color = BlackDark)
                        Row {
                            stage.candidates.forEach { number ->
                                Card(
                                    modifier = Modifier.size(50.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    backgroundColor = BlackDark,
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = number.toString(),
                                            style = MaterialTheme.typography.h5,
                                            color = White,
                                            modifier = Modifier.align(Alignment.Center),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is LiveStage.Day.Speech -> {
                        if (stage.candidateForElimination) {
                            Text("Оправдательное слово", style = MaterialTheme.typography.h6, color = BlackDark)
                        } else {
                            Text("Речь", style = MaterialTheme.typography.h6, color = BlackDark)
                        }
                        Text("Игрок ${stage.playerNumber}", style = MaterialTheme.typography.h5, color = BlackDark)

                        Text("Кандидаты", style = MaterialTheme.typography.body1, color = BlackDark)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                            liveGameData.voteCandidates.forEach { number ->
                                Card(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    backgroundColor = BlackDark,
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = number.toString(),
                                            style = MaterialTheme.typography.h5,
                                            color = White,
                                            modifier = Modifier.align(Alignment.Center),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is LiveStage.Day.LastVotedSpeech -> {
                        Text("Последнее слово", style = MaterialTheme.typography.h6, color = BlackDark)
                        Text("Игрок ${stage.playerNumber}", style = MaterialTheme.typography.h5, color = BlackDark)
                    }
                    is LiveStage.Day.LastDeathSpeech -> {
                        Text("Последнее слово", style = MaterialTheme.typography.h6, color = BlackDark)
                        Text("Игрок ${stage.playerNumber}", style = MaterialTheme.typography.h5, color = BlackDark)
                    }
                    is LiveStage.Night -> {
                        Text("Ночь", style = MaterialTheme.typography.h6, color = BlackDark)
                    }
                    else -> {}
                }
            }
            Column(modifier = Modifier.span(columns = 1, rows = right.size)) {
                right.forEach { index ->
                    val data = liveGameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        livePlayerData = data
                    )
                }
            }
            bottom.forEach { index ->
                val data = liveGameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    livePlayerData = data
                )
            }
        }
    }
}

@Composable
private fun PlayerItem(
    modifier: Modifier = Modifier,
    livePlayerData: LivePlayerData? = null,
) {
    Box(modifier) {
        if (livePlayerData != null) {
           Card(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = WhiteLight,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Card(
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(4.dp),
                        backgroundColor = BlackDark,
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = livePlayerData.number.toString(),
                                style = MaterialTheme.typography.h5,
                                color = White,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                    Text(
                        text = livePlayerData.name,
                        style = MaterialTheme.typography.h6,
                        color = BlackDark
                    )
                    Row {
                        (0 until livePlayerData.fouls).forEach {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                painter = imageResources("ic_check_circle.xml"),
                                contentDescription = null,
                                tint = BlackDark
                            )
                        }
                    }
                }
            }
        }
    }
}