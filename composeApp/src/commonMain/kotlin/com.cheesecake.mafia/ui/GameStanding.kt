package com.cheesecake.mafia.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.cheesecake.mafia.common.Blue
import com.cheesecake.mafia.common.GreyLight
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.Yellow
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.state.LivePlayerState
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.state.LiveStage
import com.cheesecake.mafia.state.StageDayType
import com.cheesecake.mafia.state.generateHistory

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
                stage = standingState.stage,
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
    stage: LiveStage,
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
        if (status != GameStatus.NewGame) {
            if (isShowRoles) {
                VerticalDivider(color = White, modifier = Modifier.align(Alignment.CenterVertically))
            }
            Text(
                text = "Фолы",
                modifier = Modifier.width(foulsColumnSize),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = White,
            )
            val actionsHistory = generateHistory(stage.type, round)
            actionsHistory.forEachIndexed { index, (stageType, _) ->
                VerticalDivider(
                    color = White,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                val modifierColumn = if (stageType == StageDayType.Day) {
                    Modifier.defaultMinSize(minWidth = dayStageColumnMinWidth)
                        .weight(dayStageColumnWeight)
                } else if (index < actionsHistory.size - 1) {
                    Modifier.defaultMinSize(minWidth = nightStageColumnMinWidth)
                        .weight(nightStageColumnWeight)
                } else {
                    Modifier.width(activeStageColumnMinWidth)
                }
                Text(
                    text = stageType.value + " " + index,
                    modifier = modifierColumn,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    color = White,
                )
            }
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

@Composable
fun FinishedGameItem(
    modifier: Modifier = Modifier,
    player: LivePlayerState,
    round: Int,
    stage: LiveStage,
    position: Int,
) {
    val backgroundColor = when (player.role) {
        is GamePlayerRole.Black -> Red.copy(alpha = 0.25f)
        is GamePlayerRole.White -> Blue.copy(alpha = 0.25f)
        is GamePlayerRole.Red -> if (player.role !is GamePlayerRole.Red.Сivilian) {
            Yellow.copy(alpha = 0.25f)
        } else {
            White
        }
        GamePlayerRole.None -> White
    }
    Row(
        modifier = modifier.fillMaxWidth().background(backgroundColor)
    ) {
        Text(
            text = position.toString(),
            modifier = Modifier.padding(8.dp).weight(positionColumnWeight),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
        )
        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
        Text(
            text = player.name,
            modifier = Modifier.padding(8.dp).weight(nameColumnWeight),
            style = MaterialTheme.typography.body1,
        )
        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
        Row(
            modifier = Modifier.weight(roleColumnWeight).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = player.role.name + " ",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(end = 4.dp),
                textAlign = TextAlign.Center,
            )
            if (player.role.iconRes.isNotEmpty()) {
                Image(
                    painter = imageResources(player.role.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.weight(1f))
        }
        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
        for ((stageType, _) in generateHistory(stage.type, round)) {
            val color = if (stageType == StageDayType.Night) {
                GreyLight.copy(alpha = 0.05f)
            } else {
                Color.Transparent
            }
            val modifierAction = if (stageType == StageDayType.Day) {
                Modifier.weight(dayStageColumnWeight)
                    .defaultMinSize(minWidth = dayStageColumnMinWidth)
            } else {
                Modifier.weight(nightStageColumnWeight)
                    .defaultMinSize(minWidth = nightStageColumnMinWidth)
            }
            Row(modifier = modifierAction.background(color)
            ) {
                Text(
                    text = "",
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }
            VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
        }
        Text(
            text = player.fouls.toString(),
            modifier = Modifier.padding(8.dp).width(foulsColumnSize),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
    }
}
