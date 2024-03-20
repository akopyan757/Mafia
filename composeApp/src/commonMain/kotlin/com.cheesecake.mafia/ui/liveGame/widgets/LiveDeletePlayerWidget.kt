package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White

@Composable
fun LiveDeletePlayerWidget(
    modifier: Modifier = Modifier,
    playerNumbers: List<Int>,
    isDayStage: Boolean,
    onAccept: (skipToNight: Boolean) -> Unit = {},
) {
    Card(modifier) {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Удаление игроков: ${playerNumbers.joinToString(separator = ", ")}",
                style = MaterialTheme.typography.body1,
            )
            if (isDayStage) {
                Button(
                    modifier = Modifier.width(260.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                    onClick = { onAccept(true) },
                ) {
                    Text(
                        text = "Подтвердить и уйти в ночь",
                        style = MaterialTheme.typography.body1,
                        color = White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Button(
                modifier = Modifier.width(260.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                onClick = { onAccept(false) },
            ) {
                Text(
                    text = "Подтвердить и продолжить",
                    style = MaterialTheme.typography.body1,
                    color = White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}