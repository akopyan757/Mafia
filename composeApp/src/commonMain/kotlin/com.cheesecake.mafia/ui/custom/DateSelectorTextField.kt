package com.cheesecake.mafia.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.imageResources
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectorTextField(
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = true
        }
    )
    val textValue by derivedStateOf {
        val date = Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis ?: 0)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayValue = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}"
        val monthValue = if (date.month.number < 10) "0${date.month.number}" else "${date.month.number}"
        val year = date.year
        val dateText = "$dayValue.$monthValue.$year"
        onValueChanged(dateText)
        dateText
    }
    Row(modifier) {
        OutlinedTextField(
            value = textValue,
            onValueChange = {},
            label = { Text("Дата", color = BlackDark) },
            trailingIcon = {
                Icon(
                    painter = imageResources("ic_calendar_month.xml"),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                        .clickable { showDatePicker = !showDatePicker },
                    tint = BlackDark,
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlackDark,
                unfocusedBorderColor = BlackDark,
                focusedLabelColor = BlackDark,
                unfocusedLabelColor = BlackDark,
            ),
            readOnly = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )
        DropdownMenu(showDatePicker, onDismissRequest = { showDatePicker = false}) {
            Box(modifier = Modifier.width(350.dp).height(470.dp).background(Color.White)) {
                DatePicker(state = datePickerState, showModeToggle = false)
            }
        }
    }
}