package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.RedLight
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.imageResources

@Composable
fun ErrorMessageWidget(
    modifier: Modifier,
    description: String,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(contentColor = RedLight)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                color = White
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = imageResources("ic_close.xml"),
                    modifier = Modifier.wrapContentSize(),
                    tint = White,
                    contentDescription = null,
                )
            }
        }
    }
}