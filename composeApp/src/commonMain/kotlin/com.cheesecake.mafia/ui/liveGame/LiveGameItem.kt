package com.cheesecake.mafia.ui.liveGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.RedDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.YellowDark
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.generateHistory
import com.cheesecake.mafia.state.primaryColor
import com.cheesecake.mafia.state.secondaryColor
import com.cheesecake.mafia.ui.VerticalDivider
import com.cheesecake.mafia.ui.activeStageColumnMinWidth
import com.cheesecake.mafia.ui.custom.ActionHistoryItem
import com.cheesecake.mafia.ui.custom.GameRoleItem
import com.cheesecake.mafia.ui.dayStageColumnMinWidth
import com.cheesecake.mafia.ui.dayStageColumnWeight
import com.cheesecake.mafia.ui.foulsColumnSize
import com.cheesecake.mafia.ui.nameColumnWidth
import com.cheesecake.mafia.ui.nightStageColumnMinWidth
import com.cheesecake.mafia.ui.positionColumnWidth
import com.cheesecake.mafia.ui.roleColumnWidth
import com.cheesecake.mafia.ui.nightStageColumnWeight

@Composable
fun LiveGameItem(
    modifier: Modifier = Modifier,
    player: LivePlayerData,
    showRoles: Boolean = true,
    round: Int = 0,
    stage: LiveStage = LiveStage.Start,
    onFoulsChanged: (Int) -> Unit = {},
    onPutOnVote: () -> Unit = {},
    isPutOnVote: Boolean = false,
    checkedActions: List<SelectedNightGameAction> = emptyList(),
    onActionCheckedChanged: (checkActions: List<SelectedNightGameAction>) -> Unit = {},
) {
    val nameColumnWidth = if (showRoles) {
        nameColumnWidth
    } else {
        nameColumnWidth + roleColumnWidth + 1.dp
    }

    Row(
        modifier = modifier.fillMaxWidth().background(White).height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = player.number.toString(),
            modifier = Modifier.padding(vertical = 8.dp).width(positionColumnWidth),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1.let {
                if (!player.isAlive) it.copy(textDecoration = TextDecoration.LineThrough) else it
            },
            color = if (player.isAlive) {
                BlackDark
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
        )

        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))

        Box(modifier = Modifier.width(nameColumnWidth)) {
            Text(
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterStart),
                text = player.name,
                style = MaterialTheme.typography.body1.let {
                    if (!player.isAlive) it.copy(textDecoration = TextDecoration.LineThrough) else it
                },
                color = if (player.isAlive) {
                    BlackDark
                } else {
                    Color.Gray.copy(alpha = 0.5f)
                }
            )
        }

        if (showRoles) {
            VerticalDivider(
                color = WhiteLight,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            GameRoleItem(
                modifier = Modifier.width(roleColumnWidth).clip(RoundedCornerShape(8.dp)),
                role = player.role,
            )
        }

        VerticalDivider(
            color = WhiteLight,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        if (player.isAlive) {
            LiveGameFouls(
                modifier = Modifier.width(foulsColumnSize),
                fouls = player.fouls,
                onFoulsChanged = onFoulsChanged,
            )
        } else {
            Box(modifier = Modifier.width(foulsColumnSize))
        }
        val actionsHistory = generateHistory(stage.type, round)
        val deadAction = player.actions.firstOrNull { it.actionType is GameActionType.Dead }
        val notAliveValue = deadAction?.let { it.dayIndex * 2 + it.actionType.dayType().order } ?: Int.MAX_VALUE
        if (actionsHistory.isNotEmpty()) {
            (0 until actionsHistory.size - 1).forEach { index ->
                val (dayType, dayRound) = actionsHistory[index]
                val aliveValue = dayRound * 2 + dayType.order
                val isAlive = aliveValue <= notAliveValue
                println("\nPlayer: ${player.number}, type: $dayType, round: $dayRound, isAlive: $isAlive")
                val gameActions = player.actions
                    .filter { it.actionType.dayType() == dayType && it.dayIndex == dayRound }
                    .map { gameAction -> gameAction.actionType }
                val historyItemModifier = if (dayType == DayType.Day) {
                    Modifier.weight(dayStageColumnWeight).defaultMinSize(minWidth = dayStageColumnMinWidth)
                } else {
                    Modifier.weight(nightStageColumnWeight).defaultMinSize(minWidth = nightStageColumnMinWidth)
                }
                VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
                ActionHistoryItem(
                    modifier = historyItemModifier,
                    actions = gameActions,
                    isAlive = isAlive,
                )
            }
        }
        actionsHistory.lastOrNull()?.let { (stageDayType, _) ->
            VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))
            val itemModifier = if (stageDayType == DayType.Day) {
                Modifier.defaultMinSize(minWidth = dayStageColumnMinWidth).weight(dayStageColumnWeight)
            } else {
                Modifier.width(activeStageColumnMinWidth)
            }
            if (player.isAlive) {
                if (stageDayType == DayType.Day) {
                    if (!player.isClient) {
                        DayActionItem(
                            modifier = itemModifier,
                            isPutOnVote = isPutOnVote,
                            onPutOnVote = onPutOnVote,
                            canAddCandidate = stage.canAddCandidate(),
                        )
                    } else {
                        ActionHistoryItem(
                            modifier = itemModifier,
                            actions = listOf(GameActionType.NightActon.ClientChoose),
                            isAlive = player.isAlive,
                        )
                    }
                } else {
                    NightActionItem(
                        modifier = itemModifier,
                        checkedActions = checkedActions,
                        onActionCheckedChanged = onActionCheckedChanged,
                    )
                }
            } else {
                Box(modifier = itemModifier.background(WhiteLight.copy(alpha = 0.7f)).fillMaxHeight())
            }
        }
    }
}

@Composable
fun DayActionItem(
    modifier: Modifier = Modifier,
    onPutOnVote: () -> Unit = {},
    isPutOnVote: Boolean = false,
    canAddCandidate: Boolean,
) {
    Box(modifier) {
        if (canAddCandidate) {
            Button(
                onClick = { onPutOnVote() },
                colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                enabled = !isPutOnVote,
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.align(Alignment.Center).size(32.dp)
            ) {
                Icon(
                    painter = imageResources("ic_like_button.xml"),
                    contentDescription = null,
                    tint = White,
                )
            }
        }
    }
}

@Composable
fun NightActionItem(
    modifier: Modifier = Modifier,
    checkedActions: List<SelectedNightGameAction> = emptyList(),
    onActionCheckedChanged: (List<SelectedNightGameAction>) -> Unit = {},
) {
    val actions = checkedActions.map { it.action }
    val nightActions = remember(checkedActions) {
        mutableStateMapOf(*checkedActions.map { it.action to it.checked }.toTypedArray())
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        actions.forEach { action ->
            val checked = nightActions[action] ?: false
            val tintColor = if (checked) {
                if (action.role !is GamePlayerRole.White) {
                    action.role.primaryColor()
                } else {
                    action.role.secondaryColor()
                }
            } else {
                Color.Gray.copy(alpha = 0.4f)
            }
            IconToggleButton(
                checked = checked,
                onCheckedChange = { isChecked ->
                    nightActions[action] = isChecked
                    onActionCheckedChanged(
                        actions.map { SelectedNightGameAction(it, nightActions[it] ?: false) }
                    )
                },
                modifier = Modifier.padding(horizontal = 2.dp).size(20.dp),
            ) {
                Icon(
                    painter = imageResources(action.iconRes()),
                    contentDescription = null,
                    tint = tintColor
                )
            }
        }
    }
}

@Composable
fun LiveGameFouls(
    modifier: Modifier = Modifier,
    fouls: Int = 0,
    onFoulsChanged: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier.width(120.dp),
    ) {
        repeat(4) { index ->
            val color = when (index) {
                2 -> YellowDark
                3 -> RedDark
                else -> Color.Gray
            }
            Checkbox(
                modifier = Modifier.weight(1f),
                checked = fouls >= index + 1,
                enabled = fouls == index || fouls == index + 1,
                onCheckedChange = { checked ->
                    onFoulsChanged(fouls + if (checked) 1 else -1)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = color,
                    uncheckedColor = color,
                    disabledColor = color,
                )
            )
        }
    }
}