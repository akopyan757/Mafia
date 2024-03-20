package com.cheesecake.mafia.ui.liveGame.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White

@Composable
fun LiveGameVoteCandidatesWidget(
    modifier: Modifier = Modifier,
    numbers: List<Int> = emptyList(),
) {
    if (numbers.isNotEmpty()) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = White,
        ) {
            Row(
                modifier = Modifier.padding(16.dp).wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Кандидаты",
                    style = MaterialTheme.typography.body1,
                    color = BlackDark,
                    modifier = Modifier.padding(end = 8.dp)
                )
                numbers.forEach { number ->
                    Card(modifier = Modifier.size(36.dp), backgroundColor = BlackDark) {
                        Text(
                            text = number.toString(),
                            modifier = modifier.padding(8.dp).wrapContentSize(),
                            style = MaterialTheme.typography.body1,
                            color = White,
                        )
                    }
                }
            }
        }
    }
}