package com.cheesecake.mafia.ui.newGame.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.state.primaryColor
import com.cheesecake.mafia.state.secondaryColor

@Composable
fun NewGameRolesWidget(
    modifier: Modifier = Modifier,
    rolesCounts: List<Pair<GamePlayerRole, Int>> = emptyList(),
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(rolesCounts) { (role, count) ->
            Card(
                modifier = Modifier.width(50.dp).size(70.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = role.primaryColor(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (role.iconRes.isNotEmpty()) {
                        Icon(
                            painter = imageResources(role.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = role.secondaryColor(),
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = count.toString(),
                        style = MaterialTheme.typography.body1,
                        color = role.secondaryColor(),
                    )
                }
            }
        }
    }
}