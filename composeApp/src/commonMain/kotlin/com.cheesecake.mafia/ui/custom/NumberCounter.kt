package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White

@Composable
fun IntCounter(
    modifier: Modifier = Modifier,
    startValue: Int = 0,
    stepValue: Int = 1,
    minValue: Int = -5,
    maxValue: Int = 5,
    onValueChanged: (Int) -> Unit = {},
    buttonsColor: Color = BlackDark,
    tintColor: Color = White,
) {
    var value by remember { mutableStateOf(startValue) }
    Row(modifier) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonsColor,
                contentColor = tintColor
            ),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onClick = {
                if (value - stepValue >= minValue) {
                    value -= stepValue
                    onValueChanged(value)
                }
            }
        ) {
            Text(text = "-")
        }
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.body1.copy(textAlign = TextAlign.Center),
            singleLine = true,
            modifier = Modifier.weight(1.5f).fillMaxHeight().padding(horizontal = 2.dp),
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonsColor,
                contentColor = tintColor
            ),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onClick = {
                if (value + stepValue <= maxValue) {
                    value += stepValue
                    onValueChanged(value)
                }
            }
        ) {
            Text(text = "+")
        }
    }
}

@Composable
fun IntCounterApp() {
    IntCounter(
        modifier = Modifier.width(200.dp).height(50.dp),
        startValue = 10,
        stepValue = 1,
        minValue = 5,
        maxValue = 25,
    )
}