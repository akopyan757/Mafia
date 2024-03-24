package com.cheesecake.mafia.ui.second

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.Blue
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.common.GrayTransparent
import com.cheesecake.mafia.common.Green
import com.cheesecake.mafia.common.LogoTint
import com.cheesecake.mafia.common.PlayersColors
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.RedLight
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.data.TimerData
import com.cheesecake.mafia.state.SquareData
import com.cheesecake.mafia.ui.custom.Grid
import com.cheesecake.mafia.viewModel.InteractiveScreenViewModel
import org.koin.compose.koinInject

@Composable
fun InteractiveScreen(
    viewModel: InteractiveScreenViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val timer by viewModel.timer.collectAsState()
    if (state is InteractiveScreenState.LiveGame) {
        InteractiveLiveScreen((state as InteractiveScreenState.LiveGame).state, timer)
    } else {
        InteractiveNewGameScreen()
    }
}

private fun createSquare(count: Int): SquareData {
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
fun InteractiveNewGameScreen() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Box {
            Image(
                painter = imageResources("background.xml"),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = imageResources("logo_gt.xml"),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(LogoTint),
                modifier = Modifier.align(Alignment.Center).fillMaxHeight(0.6f)
            )
        }
    }
}

@Composable
fun InteractiveLiveScreen(liveGameData: LiveGameData, timer: TimerData) {
    val playersCount = liveGameData.players.size
    val (top, right, left, bottom) = createSquare(playersCount)
    val stage = liveGameData.stage
    val speakingNumber = when (stage) {
        is LiveStage.Day.Speech -> stage.playerNumber
        is LiveStage.Day.LastDeathSpeech -> stage.playerNumber
        is LiveStage.Day.LastVotedSpeech -> stage.playerNumber
        else -> null
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Image(
            painter = imageResources("background.xml"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Grid(
            columns = top.size,
            modifier = Modifier.fillMaxSize().padding(8.dp),
        ) {
            top.forEach { index ->
                val data = liveGameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    isSpeaking = data?.number == speakingNumber,
                    livePlayerData = data,
                )
            }
            Column(modifier = Modifier.span(columns = 1, rows = left.size)) {
                left.forEach { index ->
                    val data = liveGameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        isSpeaking = data?.number == speakingNumber,
                        livePlayerData = data
                    )
                }
            }
            Column(
                modifier = Modifier.span(columns = top.size - 2, rows = left.size),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                when (stage) {
                    is LiveStage.Day.Vote -> {
                        val text = if (!stage.reVote) "Голосование" else "Переголосование"
                        Text(text, style = MaterialTheme.typography.h3, color = White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            liveGameData.voteCandidates.forEach { number ->
                                PlayerNumberWidget(
                                    modifier = Modifier.size(60.dp),
                                    number = number,
                                )
                            }
                        }
                    }

                    is LiveStage.Day.Speech,
                    is LiveStage.Day.LastDeathSpeech,
                    is LiveStage.Day.LastVotedSpeech -> {
                        val text = if (stage is LiveStage.Day.Speech && stage.candidateForElimination) {
                            "Оправдательное слово | Речь игрока"
                        } else if (stage is LiveStage.Day.LastVotedSpeech) {
                            "Последнее слово | Речь игрока"
                        } else if (stage is LiveStage.Day.LastDeathSpeech) {
                            "Последнее слово | Речь игрока"
                        } else {
                            "Речь игрока"
                        }
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.h5,
                                color = White,
                            )
                            PlayerNumberWidget(
                                modifier = Modifier.size(60.dp),
                                number = stage.playerNumber,
                            )
                        }

                        if (timer.active) {
                            Box(modifier = Modifier.size(100.dp)) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    progress = timer.value / timer.total.toFloat(),
                                    color = Blue.copy(0.7f),
                                    strokeWidth = 10.dp,
                                )
                                Text(
                                    text = timer.value.toString(),
                                    style = MaterialTheme.typography.h3,
                                    color = White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }


                        if (liveGameData.voteCandidates.isNotEmpty() && stage is LiveStage.Day.Speech) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Выставленные кандидатуры",
                                    style = MaterialTheme.typography.h5,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = White,
                                )
                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp, Alignment.CenterHorizontally
                                    )
                                ) {
                                    liveGameData.voteCandidates.forEach { number ->
                                        PlayerNumberWidget(
                                            modifier = Modifier.size(60.dp),
                                            number = number,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is LiveStage.Night -> {
                        Text("Ночь", style = MaterialTheme.typography.h3, color = White)
                    }

                    else -> {}
                }
            }
            Column(modifier = Modifier.span(columns = 1, rows = right.size)) {
                right.forEach { index ->
                    val data = liveGameData.players.getOrNull(index)
                    PlayerItem(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        isSpeaking = data?.number == speakingNumber,
                        livePlayerData = data,
                    )
                }
            }
            bottom.forEach { index ->
                val data = liveGameData.players.getOrNull(index)
                PlayerItem(
                    modifier = Modifier.span(columns = 1, rows = 1),
                    isSpeaking = data?.number == speakingNumber,
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
    isSpeaking: Boolean = false,
) {
    val cardBackground = when {
        isSpeaking -> Green.copy(alpha = 0.15f)
        livePlayerData == null -> GrayTransparent.copy(alpha = 0.5f)
        livePlayerData.isKilled -> GrayTransparent.copy(alpha = 0.5f)
        livePlayerData.isVoted -> GrayTransparent.copy(alpha = 0.5f)
        livePlayerData.isDeleted -> GrayTransparent.copy(alpha = 0.5f)
        livePlayerData.isClient -> RedLight.copy(alpha = 0.2f)
        livePlayerData.isAlive -> GrayLight.copy(alpha = 0.5f)
        else -> GrayTransparent.copy(alpha = 0.5f)
    }
    val tintColor = if (livePlayerData?.isAlive == true) White else White.copy(alpha = 0.4f)
    Box(modifier) {
        Card(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            shape = RoundedCornerShape(4.dp),
            backgroundColor = cardBackground,
        ) {
            if (livePlayerData != null) {
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.weight(0.7F))
                    PlayerNumberWidget(
                        modifier = Modifier.size(70.dp),
                        number = livePlayerData.number,
                        isAlive = livePlayerData.isAlive,
                    )
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = livePlayerData.name,
                        style = MaterialTheme.typography.h5.copy(fontSize = 30.sp),
                        color = tintColor
                    )
                    Spacer(Modifier.weight(0.2F))
                    Row(
                        modifier = Modifier.weight(1f).padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (livePlayerData.isAlive) {
                            if (livePlayerData.isClient) {
                                Icon(
                                    modifier = Modifier.size(48.dp),
                                    painter = imageResources("ic_role_whore.xml"),
                                    contentDescription = null,
                                    tint = Red,
                                )
                            }
                            (0 until livePlayerData.fouls).forEach {
                                Icon(
                                    modifier = Modifier.size(48.dp),
                                    painter = imageResources("ic_check_circle.xml"),
                                    contentDescription = null,
                                    tint = tintColor
                                )
                            }
                        } else if (livePlayerData.isDeleted) {
                            Text(
                                text = "Удален",
                                style = MaterialTheme.typography.h5,
                                color = tintColor
                            )
                        } else if (livePlayerData.isVoted) {
                            Text(
                                text = "Заголосован",
                                style = MaterialTheme.typography.h5,
                                color = tintColor
                            )
                        } else if (livePlayerData.isKilled) {
                            Text(
                                text = "Убит",
                                style = MaterialTheme.typography.h5,
                                color = tintColor
                            )
                        }
                    }
                    Spacer(Modifier.weight(0.3F))
                }
            } else {
                Image(
                    painter = imageResources("logo_gt.xml"),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(LogoTint),
                    modifier = Modifier.align(Alignment.Center).fillMaxHeight(0.8f).padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerNumberWidget(
    modifier: Modifier = Modifier,
    number: Int,
    isAlive: Boolean = true,
) {
    val index = (number - 1) % PlayersColors.size
    val backgroundColor = if (isAlive) PlayersColors[index] else BlackDark
    val tintColor = if (isAlive) White else White.copy(alpha = 0.4f)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        backgroundColor = backgroundColor,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.h3,
                color = tintColor,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}