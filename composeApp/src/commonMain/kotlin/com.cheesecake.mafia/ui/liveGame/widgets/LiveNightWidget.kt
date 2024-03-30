package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.VoteColorChosen
import com.cheesecake.mafia.common.VoteColorDisabled
import com.cheesecake.mafia.common.VoteColorEnable
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GameActionType

@Composable
fun LiveNightWidget(
    modifier: Modifier = Modifier,
    allActions: List<GameActionType.NightActon> = listOf(),
    killedPlayers: List<Int>,
    clientChosen: Int? = null,
    playersCount: Int = 0,
    blackPlayersCount: Int = 0,
    isFirstNight: Boolean = false,
    onFinish: (bestMove: Map<Int, List<Int>>) -> Unit = {}
) {
    val bestMoves = remember { mutableStateMapOf<Int, List<Int>>() }
    Column(modifier = modifier.wrapContentWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        text = "Клиент: $clientChosen",
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                    )
                }
                if (isFirstNight && killedPlayers.isNotEmpty()) {
                    Text(
                        text = "Лучший ход",
                        style = MaterialTheme.typography.body1,
                        color = BlackDark,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    killedPlayers.forEach { killedPlayer ->
                        BaseMoveSelectItem(
                            killedPlayer = killedPlayer,
                            playersCount = playersCount,
                            mafiaCount = blackPlayersCount,
                            onBestMoveSelected = { bestMove -> bestMoves[killedPlayer] = bestMove }
                        )
                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlackDark,
                        contentColor = White,
                    ),
                    onClick = { onFinish(bestMoves) }
                ) {
                    Text(text = "Подтвердить")
                }
            }
        }
    }
}


@Composable
private fun BaseMoveSelectItem(
    modifier: Modifier = Modifier,
    killedPlayer: Int,
    playersCount: Int,
    mafiaCount: Int,
    onBestMoveSelected: (List<Int>) -> Unit,
) {
    var selectedPlayers: Set<Int> by remember { mutableStateOf(setOf()) }
    val isClickEnabled by derivedStateOf { selectedPlayers.size < mafiaCount }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Text(
            text = killedPlayer.toString(),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically),
        )
        (1..playersCount).forEach { boxIndex ->
            val isSelected = selectedPlayers.contains(boxIndex)
            val color = when {
                isSelected -> VoteColorChosen
                isClickEnabled -> VoteColorEnable
                else -> VoteColorDisabled
            }
            Box(
                modifier = modifier.size(30.dp)
                    .background(color = color)
                    .clip(RoundedCornerShape(2.dp))
                    .clickable {
                        if (isClickEnabled || isSelected) {
                            selectedPlayers = selectedPlayers.toMutableSet().apply {
                                if (isSelected) remove(boxIndex) else add(boxIndex)
                            }
                            onBestMoveSelected(selectedPlayers.toList().sorted())
                        }
                    }
            ) {
                Text(
                    text = boxIndex.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.body2,
                    color = BlackDark.copy(alpha = 0.5f)
                )
            }
        }
    }
}