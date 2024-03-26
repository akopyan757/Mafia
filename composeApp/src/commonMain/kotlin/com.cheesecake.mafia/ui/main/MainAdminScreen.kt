package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun MainAdminScreen(component: MainComponent) {
    val viewModel = koinInject<MainViewModel>()
    MainAdminScreen(
        viewModel,
        onNewGameClicked = component::omStartNewGameClicked,
        onGameItemClicked = component::onGameClicked,
    )
}

@Composable
fun MainAdminScreen(
    viewModel: MainViewModel,
    onNewGameClicked: () -> Unit,
    onGameItemClicked: (gameId: Long) -> Unit,
) {
    val gameItems by viewModel.gameItems.collectAsState()
    val dates by viewModel.dates.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { onNewGameClicked() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = BlackDark,
                contentColor = Color.White
            )
        ) {
            Text(
                "Начать новую игру",
                style = MaterialTheme.typography.body1,
                color = White
            )
        }
        GamesViews(
            modifier = Modifier.weight(1f),
            dates = dates,
            gameItems = gameItems,
            onGameItemClicked = onGameItemClicked
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }
}
