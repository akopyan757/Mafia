package com.cheesecake.mafia.ui.newGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.cheesecake.mafia.state.StageDayType
import com.cheesecake.mafia.state.StartGameData
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.ui.custom.DateSelectorTextField
import com.cheesecake.mafia.ui.custom.IntCounter
import com.cheesecake.mafia.ui.newGame.widget.NewGameRolesWidget
import com.cheesecake.mafia.viewModel.NewGameStandingViewModel
import org.koin.compose.koinInject

@Composable
fun NewGameScreen(component: NewGameComponent) {
    val viewModel: NewGameStandingViewModel = koinInject()

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
    onStartGameClicked: (date: StartGameData) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Box {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column (Modifier.width(170.dp)){
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                        onClick = { onBackPressed() },
                    ) {
                        Text(
                            text = "Назад",
                            style = MaterialTheme.typography.body1,
                            color = White,
                        )
                    }
                    OutlinedTextField(
                        value = state.title,
                        label = { Text("Название") },
                        onValueChange = { viewModel.changeTitleValue(it) },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = BlackDark,
                            unfocusedBorderColor = BlackDark,
                            focusedLabelColor = BlackDark,
                            unfocusedLabelColor = BlackDark,
                        ),
                    )
                    DateSelectorTextField(modifier = Modifier.fillMaxWidth()) { value ->
                        viewModel.changeDate(value)
                    }
                }
                Column(
                    modifier = Modifier.width(170.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NewGamePlayerCount(
                        modifier = Modifier.fillMaxWidth(),
                        startValue = 10,
                        onValueChanged = { viewModel.onPlayerCountsChanged(it) }
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = BlackDark),
                        onClick = {
                            viewModel.saveNewPlayers()
                            onStartGameClicked(state.toStartData())
                        },
                        enabled = state.isItemsFilled,
                    ) {
                        Text(
                            text = "Начать",
                            style = MaterialTheme.typography.body1,
                            color = White,
                        )
                    }
                }
                NewGameRolesWidget(
                    modifier = Modifier.wrapContentHeight(),
                    rolesCounts = state.rolesCount,
                )
            }
            NewGameStanding(
                modifier = Modifier.wrapContentSize().padding(top = 8.dp),
                items = state.items,
                availablePlayers = state.availablePlayers,
                availableRoles = GamePlayerRole.values(),
                onRoleChanged = { number, role -> viewModel.onRoleChanged(number, role) },
                onPlayerChoose = { number, player -> viewModel.onPlayerChosen(number, player) },
                onNewPlayerChosen = { number, name -> viewModel.onNewPlayerNameChanged(number, name) },
            )
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
            dayType = StageDayType.Night,
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
