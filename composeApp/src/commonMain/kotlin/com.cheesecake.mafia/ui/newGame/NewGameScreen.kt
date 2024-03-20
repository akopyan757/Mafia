package com.cheesecake.mafia.ui.newGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.components.newGame.NewGameComponent
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.LiveStage
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.ui.custom.IntCounter
import com.cheesecake.mafia.ui.newGame.widget.NewGameRolesWidget
import com.cheesecake.mafia.viewModel.NewGameStandingViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun NewGameScreen(component: NewGameComponent) {
    val viewModel = getViewModel(
        key = "new-standing",
        factory = viewModelFactory { NewGameStandingViewModel() }
    )

    NewGameStanding(
        viewModel = viewModel,
        onBackPressed = component::onBackClicked,
        onStartGameClicked = component::onStartGameClicked
    )
}

@Composable
fun NewGameStanding(
    viewModel: NewGameStandingViewModel,
    onBackPressed: () -> Unit,
    onStartGameClicked: (items: List<NewGamePlayerItem>) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NewGameStanding(
            modifier = Modifier.wrapContentSize().padding(top = 8.dp),
            items = state.items,
            availablePlayers = state.availablePlayers,
            availableRoles = GamePlayerRole.values(),
            onRoleChanged = { number, role -> viewModel.onRoleChanged(number, role) },
            onPlayerChoose = { number, player -> viewModel.onPlayerChosen(number, player) },
            onNewPlayerChosen = { number, name -> viewModel.onNewPlayerNameChanged(number, name) },
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NewGamePlayerCount(
                modifier = Modifier.fillMaxHeight(),
                startValue = 10,
                onValueChanged = { viewModel.onPlayerCountsChanged(it) }
            )
            NewGameRolesWidget(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                rolesCounts = state.rolesCount,
            )
            Button(
                onClick = { onBackPressed() },
                enabled = state.isItemsFilled,
            ) {
                Text(
                    "Назад",
                    style = MaterialTheme.typography.body1,
                    color = White,
                )
            }
            Button(
                onClick = { onStartGameClicked(state.items) },
                enabled = state.isItemsFilled,
            ) {
                Text(
                    "Начать",
                    style = MaterialTheme.typography.body1,
                    color = White,
                )
            }
        }
    }
}

@Composable
fun NewGameStanding(
    modifier: Modifier = Modifier,
    items: List<NewGamePlayerItem>,
    availablePlayers: List<PlayerState>,
    availableRoles: List<GamePlayerRole>,
    onRoleChanged: (number: Int, role: GamePlayerRole) -> Unit = { _, _ -> },
    onPlayerChoose: (number: Int, player: PlayerState) -> Unit = { _, _ -> },
    onNewPlayerChosen: (number: Int, name: String) -> Unit = { _, _ -> },
) {
    GameStanding(
        modifier = modifier,
        standingState = GameStandingState(
            id = 0,
            status = GameStatus.NewGame,
            round = 0,
            stage = LiveStage.Start,
            isShowRoles = true,
        ),
        itemsCount = items.size,
        itemContent = { position ->
            val item: NewGamePlayerItem = items[position]
            NewGameItem(
                modifier = Modifier.fillMaxWidth(),
                player = item.player,
                role = item.role,
                number = item.number,
                availablePlayers = availablePlayers,
                availableRoles = availableRoles,
                onRoleChanged = { role -> onRoleChanged(item.number, role) },
                onPlayerChoose = { playerState -> onPlayerChoose(item.number, playerState) },
                onNewPlayerClicked = { name -> onNewPlayerChosen(item.number, name) },
            )
        }
    )
}

@Composable
fun NewGamePlayerCount(
    modifier: Modifier = Modifier,
    startValue: Int = 10,
    onValueChanged: (Int) -> Unit = {},
) {
    Column(modifier.width(180.dp)) {
        Text(
            text = "Количество игроков",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.fillMaxWidth(),
            color = BlackDark,
        )
        IntCounter(
            modifier = modifier.width(180.dp).height(50.dp),
            minValue = 5,
            maxValue = 25,
            stepValue = 1,
            startValue = startValue,
            onValueChanged = onValueChanged,
        )
    }
}
