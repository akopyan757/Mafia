package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.primaryColor
import com.cheesecake.mafia.state.secondaryColor

@Composable
fun GameRoleItem(
    modifier: Modifier = Modifier,
    role: GamePlayerRole = GamePlayerRole.None,
) {
    Row(
        modifier = modifier.background(role.primaryColor()).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (role.iconRes.isNotEmpty()) {
            Icon(
                painter = imageResources(role.iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp).padding(start = 16.dp),
                tint = role.secondaryColor(),
            )
        }
        Text(
            text = role.name,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = role.secondaryColor(),
            modifier = Modifier.weight(1f)
        )
    }
}