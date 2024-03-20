package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Column(modifier.padding(8.dp), Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Удаление игроков: ${playerNumbers.joinToString(separator = ", ")}",
                style = MaterialTheme.typography.body1,
            )
            if (isDayStage) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                    onClick = { onAccept(true) },
                ) {
                    Text(
                        text = "Подтвердить и уйти в ночь",
                        style = MaterialTheme.typography.body1,
                        color = White,
                    )
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                onClick = { onAccept(false) },
            ) {
                Text(
                    text = "Подтвердить и продолжить",
                    style = MaterialTheme.typography.body1,
                    color = White,
                )
            }
        }
    }
}