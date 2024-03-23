package com.cheesecake.mafia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.generateHistory
import com.cheesecake.mafia.data.toText

@Composable
fun GameStanding(
    modifier: Modifier = Modifier,
    standingState: GameStandingState,
    itemContent: @Composable (position: Int) -> Unit,
    itemsCount: Int = 0,
) {
    LazyColumn(modifier = modifier) {
        item {
            HeaderItem(
                round = standingState.round,
                dayType = standingState.dayType,
                status = standingState.status,
                isShowRoles = standingState.isShowRoles,
            )
        }

        items(itemsCount) { position ->
            itemContent(position)
            if (position < itemsCount - 1) {
                Divider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    color = WhiteLight,
                )
            }
        }
    }
}

@Composable
fun HeaderItem(
    modifier: Modifier = Modifier,
    round: Int,
    dayType: DayType,
    status: GameStatus,
    isShowRoles: Boolean,
) {
    val nameColumnWidth = if (isShowRoles) {
        nameColumnWidth
    } else {
        nameColumnWidth + roleColumnWidth
    }
    Row(
        modifier = modifier.fillMaxWidth().background(color = BlackDark),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Номер",
            modifier = Modifier.padding(vertical = 8.dp).run {
                if (status == GameStatus.NewGame) {
                    weight(positionColumnWeight)
                } else {
                    width(positionColumnWidth)
                }
            },
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = White,
        )
        VerticalDivider(color = White, modifier = Modifier.align(Alignment.CenterVertically))
        Box(modifier = Modifier.run {
            if (status == GameStatus.NewGame)
                weight(nameColumnWeight)
            else
                width(nameColumnWidth)
        }) {
            Text(
                text = "Никнейм",
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterStart),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
                color = White,
            )
        }
        VerticalDivider(color = White, modifier = Modifier.align(Alignment.CenterVertically))
        if (isShowRoles) {
            Text(
                text = "Роль",
                modifier = Modifier.padding(vertical = 8.dp).run {
                    if (status == GameStatus.NewGame) {
                        this.weight(roleColumnWeight)
                    } else {
                        this.width(roleColumnWidth)
                    }
                },
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = White,
            )
        }
        if (status == GameStatus.Live) {
            if (isShowRoles) {
                VerticalDivider(
                    color = White,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Text(
                text = "Фолы",
                modifier = Modifier.width(foulsColumnSize),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = White,
            )
        }
        val actionsHistory = generateHistory(dayType, round)
        actionsHistory.forEachIndexed { index, (stageType, _) ->
            VerticalDivider(
                color = White,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            val modifierColumn = if (stageType == DayType.Day) {
                Modifier.defaultMinSize(minWidth = dayStageColumnMinWidth)
                    .weight(dayStageColumnWeight)
            } else if (index < actionsHistory.size - 1 || status == GameStatus.Finished) {
                Modifier.defaultMinSize(minWidth = nightStageColumnMinWidth)
                    .weight(nightStageColumnWeight)
            } else {
                Modifier.width(activeStageColumnMinWidth)
            }
            Text(
                text = stageType.toText() + " " + index,
                modifier = modifierColumn,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = White,
            )
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .padding(vertical = 2.dp)
            .width(1.dp)
            .background(color)
    )
}