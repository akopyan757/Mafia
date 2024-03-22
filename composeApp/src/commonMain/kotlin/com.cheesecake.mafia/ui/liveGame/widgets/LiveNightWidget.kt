package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GameActionType

@Composable
fun LiveNightWidget(
    modifier: Modifier = Modifier,
    allActions: List<GameActionType.NightActon> = listOf(),
    killedPlayers: List<Int>,
    clientChosen: Int? = null,
    onFinish: () -> Unit = {}
) {

    Column(modifier = modifier.width(240.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = White,
        ) {
            Row(
                modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
            ) {
                allActions.forEach { action ->
                    Icon(
                        modifier = Modifier.size(24.dp).padding(horizontal = 2.dp),
                        painter = imageResources(action.iconRes()),
                        contentDescription = null,
                        tint = BlackDark,
                    )
                }
            }
        }

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = White,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (killedPlayers.isNotEmpty()) {
                    Text(
                        text = "Убиты игроки: ${
                            killedPlayers.sorted().joinToString(", ") { it.toString() }
                        }",
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                    )
                } else {
                    Text(
                        text = "Никто не убит",
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                    )
                }
                if (clientChosen != null) {
                    Text(
                        text = "Клиент: ${clientChosen}",
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlackDark,
                        contentColor = White,
                    ),
                    onClick = { onFinish() }
                ) {
                    Text(text = "Подтвердить")
                }
            }
        }
    }
}