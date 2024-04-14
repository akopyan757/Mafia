package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.GrayLight
import com.cheesecake.mafia.common.White

@Composable
fun PrimitiveTabRow(
    modifier: Modifier = Modifier,
    values: List<String> = emptyList(),
    onValueSelected: (value: String) -> Unit = {},
) {
    var tabIndex by remember { mutableStateOf(0) }
    ScrollableTabRow(
        selectedTabIndex = tabIndex,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 0.dp,
        backgroundColor = Color.Transparent,
    ) {
        values.forEachIndexed { index, value ->
            Tab(
                modifier = if (tabIndex == index) {
                    Modifier.background(BlackDark)
                } else {
                    Modifier.background(GrayLight)
                },
                selected = tabIndex == index,
                onClick = {
                    tabIndex = index
                    onValueSelected(value)
                },
                content = {
                    Text(
                        text = value,
                        modifier = Modifier.padding(8.dp),
                        color = White,
                    )
                }
            )
        }
    }
}