package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import kotlinx.coroutines.delay

@Composable
fun LiveSpeechPlayerTimerWidget(
    modifier: Modifier = Modifier,
    gameActive: Boolean = false,
    title: String = "",
    playerNumber: Int = 1,
    seconds: Int = 0,
    onFinish: () -> Unit = {},
) {
    var timer by remember(playerNumber) { mutableStateOf(seconds) }
    val timerColor = if (timer > 10) BlackDark else Red
    var isActive by remember { mutableStateOf(false) }
    val iconPlayRes = if (isActive) "ic_pause_button.xml" else "ic_play_button.xml"

    Card(
        modifier = modifier.wrapContentSize(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = White,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1,
                    color = BlackDark
                )
            }
            Text(
                text = "Игрок $playerNumber",
                style = MaterialTheme.typography.body1,
                color = BlackDark
            )
            Text(
                text = "Время: " + timer.toString() + "s",
                style = MaterialTheme.typography.body1,
                color = timerColor,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { timer = seconds },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlackDark,
                        contentColor = White
                    ),
                    content = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = imageResources("ic_reset_button.xml"),
                            contentDescription = "Reset",
                            tint = White,
                        )
                    },
                )
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { isActive = !isActive },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlackDark,
                        contentColor = White
                    ),
                    content = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = imageResources(iconPlayRes),
                            contentDescription = if (isActive) "Pause" else "Start",
                            tint = White,
                        )
                    },
                )
                Button(
                    modifier = Modifier.size(50.dp),
                    onClick = { onFinish() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlackDark,
                        contentColor = White
                    ),
                    content = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = imageResources("ic_next_button.xml"),
                            contentDescription = "Next",
                            tint = White,
                        )
                    },
                )
            }
        }
    }

    LaunchedEffect(playerNumber, gameActive) {
        isActive = true
        while (timer > 0) {
            delay(1000)
            if (isActive && gameActive) {
                timer -= 1
            }
        }
        timer = seconds
        onFinish()
    }
}