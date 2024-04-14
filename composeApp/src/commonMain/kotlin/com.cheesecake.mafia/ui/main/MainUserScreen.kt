package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun MainUserScreen(component: MainComponent) {
    val viewModel = koinInject<MainViewModel>()
    MainUserScreen(
        viewModel,
        //onGameItemClicked = component::onGameClicked,
    )
}

@Composable
fun MainUserScreen(
    viewModel: MainViewModel,
    //onGameItemClicked: (gameId: Long) -> Unit,
) {
    val gameItems by viewModel.finishedGames.collectAsState()
    val dates by viewModel.finishedGameDates.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        FinishedGamesViews(
            modifier = Modifier.weight(1f),
            dates = dates,
            gameItems = gameItems,
            onGameItemClicked = {}
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }
}
