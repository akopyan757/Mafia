package com.cheesecake.mafia.ui.finishedGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.state.generateHistory
import com.cheesecake.mafia.ui.VerticalDivider
import com.cheesecake.mafia.ui.custom.ActionHistoryItem
import com.cheesecake.mafia.ui.custom.GameRoleItem
import com.cheesecake.mafia.ui.dayStageColumnMinWidth
import com.cheesecake.mafia.ui.dayStageColumnWeight
import com.cheesecake.mafia.ui.nameColumnWidth
import com.cheesecake.mafia.ui.nightStageColumnMinWidth
import com.cheesecake.mafia.ui.nightStageColumnWeight
import com.cheesecake.mafia.ui.positionColumnWidth
import com.cheesecake.mafia.ui.roleColumnWidth

@Composable
fun FinishedGameItem(
    modifier: Modifier = Modifier,
    player: GamePlayerData,
    lastRound: Int = 0,
    lastDayType: DayType,
) {
    Row(
        modifier = modifier.fillMaxWidth().background(White).height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = player.number.toString(),
            modifier = Modifier.padding(vertical = 8.dp).width(positionColumnWidth),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
            color = BlackDark
        )

        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))

        Box(modifier = Modifier.width(nameColumnWidth)) {
            Text(
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterStart),
                text = player.name,
                style = MaterialTheme.typography.body1,
                color = BlackDark,
            )
        }

        VerticalDivider(
            color = WhiteLight,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        GameRoleItem(
            modifier = Modifier.width(roleColumnWidth).clip(RoundedCornerShape(8.dp)),
            role = player.role,
        )

        VerticalDivider(
            color = WhiteLight,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        val notAliveAction = player.actions.firstOrNull { it.actionType is GameActionType.Dead }
        val notAliveValue = notAliveAction?.let { it.dayIndex * 2 + it.actionType.dayType().order } ?: Int.MAX_VALUE
        generateHistory(lastDayType, lastRound).forEach { (dayType, dayIndex) ->
            val aliveValue = dayIndex * 2 + dayType.order
            val isAlive = aliveValue <= notAliveValue
            val gameActions = player.actions
                .filter { it.actionType.dayType() == dayType && it.dayIndex == dayIndex }
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
                isAlive = player.isAlive || isAlive,
            )
        }
    }
}