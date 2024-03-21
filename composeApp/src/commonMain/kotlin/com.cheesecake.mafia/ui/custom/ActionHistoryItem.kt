package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.state.GameActionType

@Composable
fun ActionHistoryItem(
    modifier: Modifier = Modifier,
    actions: List<GameActionType>,
    isAlive: Boolean = true,
) {
    val color = if (isAlive) Color.Transparent else WhiteLight.copy(alpha = 0.7f)
    Row(modifier.background(color).fillMaxHeight(), horizontalArrangement = Arrangement.Center) {
        actions.forEach { gameActionType ->
            if (gameActionType.iconRes().isNotEmpty()) {
                Icon(
                    painter = imageResources(gameActionType.iconRes()),
                    modifier = Modifier.size(16.dp).align(Alignment.CenterVertically),
                    contentDescription = null,
                )
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}