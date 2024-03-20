package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.viewModel.MainViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun MainScreen(component: MainComponent) {
    val viewModel = getViewModel(
        key = "live-standing",
        factory = viewModelFactory { MainViewModel() }
    )
    MainScreen(viewModel, onNewGameClicked = component::omStartNewGameClicked)
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNewGameClicked: () -> Unit,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { onNewGameClicked() },
            colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark, contentColor = Color.White)
        ) {
            Text(
                "Начать новую игру",
                style = MaterialTheme.typography.body1,
                color = White
            )
        }
    }
}