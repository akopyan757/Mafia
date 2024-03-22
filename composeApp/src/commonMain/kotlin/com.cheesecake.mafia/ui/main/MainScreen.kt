package com.cheesecake.mafia.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
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
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.viewModel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen(component: MainComponent) {
    val viewModel = koinInject<MainViewModel>()
    MainScreen(
        viewModel,
        onNewGameClicked = component::omStartNewGameClicked,
        onGameItemClicked = component::onGameClicked,
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNewGameClicked: () -> Unit,
    onGameItemClicked: (gameId: Long) -> Unit,
) {
    val gameItems by viewModel.gameItems.collectAsState()

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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(gameItems) { gameData ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color.White,
                    modifier = Modifier.clickable { onGameItemClicked(gameData.id) }
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(gameData.date, style = MaterialTheme.typography.body1)
                        Text(gameData.title, style = MaterialTheme.typography.body1)
                        Icon(
                            modifier = Modifier.clickable { viewModel.deleteGame(gameData.id) },
                            painter = imageResources("ic_delete.xml"),
                            contentDescription = null,
                            tint = BlackDark
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }
}
