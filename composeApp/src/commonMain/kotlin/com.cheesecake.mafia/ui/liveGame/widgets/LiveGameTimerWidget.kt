package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.resultText
import kotlinx.coroutines.delay

@Composable
fun LiveGameTimer(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    finishResult: GameFinishResult? = null,
    onStopGame: (totalTime: Long) -> Unit = {},
    onPauseGame: () -> Unit = {},
    undoActive: Boolean = false,
    onUndo: () -> Unit = {},
    redoActive: Boolean = false,
    onRedo: () -> Unit = {},
) {
    var timer by remember { mutableStateOf(0L) }
    val hours by derivedStateOf { timer.div(3600).toString() }
    val minutes by derivedStateOf {
        timer.mod(3600).div(60).let { if (it < 9) "0${it}" else "$it" }
    }
    val seconds by derivedStateOf {
        timer.mod(60).let { if (it < 9) "0${it}" else "$it" }
    }
    val finishResultText = finishResult?.resultText()
    Card(modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "${hours}:${minutes}:${seconds}",
                style = MaterialTheme.typography.h6,
            )
            if (finishResultText != null) {
                Text(
                    text = finishResultText,
                    style = MaterialTheme.typography.body1,
                    color = BlackDark,
                    textAlign = TextAlign.Center,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPauseGame,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Text(
                        text = "Пауза",
                        style = MaterialTheme.typography.body1,
                        color = White,
                    )
                }
                Button(
                    onClick = { onStopGame(timer) },
                    modifier = Modifier.weight(1f),
                    enabled = finishResult != null,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Text(
                        text = "Стоп",
                        style = MaterialTheme.typography.body1,
                        color = White,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onUndo() },
                    modifier = Modifier.weight(1f),
                    enabled = undoActive,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Text(
                        text = "Undo",
                        style = MaterialTheme.typography.body1,
                        color = White,
                    )
                }
                Button(
                    onClick = { onRedo() },
                    modifier = Modifier.weight(1f),
                    enabled = redoActive,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                ) {
                    Text(
                        text = "Redo",
                        style = MaterialTheme.typography.body1,
                        color = White,
                    )
                }
            }
        }
    }

    LaunchedEffect(active) {
        while (active) {
            timer += 1
            delay(1000)
        }
    }
}