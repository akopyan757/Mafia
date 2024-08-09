package com.cheesecake.mafia.ui.newGame

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheesecake.mafia.common.BlackDark
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.Yellow
import com.cheesecake.mafia.components.newGame.NewGameComponent
import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.roleValues
import com.cheesecake.mafia.state.GameStandingState
import com.cheesecake.mafia.state.GameStatus
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.StartData
import com.cheesecake.mafia.ui.GameStanding
import com.cheesecake.mafia.ui.custom.DateSelectorTextField
import com.cheesecake.mafia.ui.custom.IntCounter
import com.cheesecake.mafia.ui.newGame.widget.NewGameRolesWidget
import com.cheesecake.mafia.viewModel.NewGameViewModel
import org.koin.compose.koinInject

@Composable
fun NewGameScreen(component: NewGameComponent) {
    val viewModel: NewGameViewModel = koinInject()

    NewGameStanding(
        viewModel = viewModel,
        onBackPressed = component::onBackClicked,
        onStartGameClicked = component::onStartNewGameClicked
    )
}

@Composable
fun NewGameStanding(
    viewModel: NewGameViewModel,
    onBackPressed: () -> Unit,
    onStartGameClicked: (date: StartData.NewGame) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var copiedPlayer by remember { mutableStateOf<PlayerState?>(null) }

    Box {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
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
                        label = { Text("Название", color = BlackDark) },
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
                    modifier = Modifier.width(200.dp),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExistPlayers(
                    modifier = Modifier.width(170.dp).wrapContentHeight(),
                    selectedPlayer = copiedPlayer,
                    availablePlayers = state.availablePlayers,
                    onClicked = { player ->
                        copiedPlayer = if (copiedPlayer != player) player else null
                    }
                )

                NewGameStanding(
                    modifier = Modifier.wrapContentSize().padding(top = 8.dp),
                    items = state.items,
                    availablePlayers = state.availablePlayers,
                    availableRoles = roleValues(),
                    copiedPlayerToPast = copiedPlayer,
                    onNumberClicked = { number, selectedPlayer ->
                        viewModel.onPlayerChosen(number, selectedPlayer)
                        copiedPlayer = null
                    },
                    onRoleChanged = { number, role -> viewModel.onRoleChanged(number, role) },
                    onPlayerChoose = { number, player ->
                        viewModel.onPlayerChosen(number, player)
                        copiedPlayer = null
                    },
                    onNewPlayerChosen = { number, name ->
                        viewModel.onNewPlayerNameChanged(number, name)
                        copiedPlayer = null
                    },
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
    copiedPlayerToPast: PlayerState? = null,
    onNumberClicked: (number: Int, player: PlayerState) -> Unit,
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
            dayType = DayType.Night,
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
                onNumberClicked = onNumberClicked,
                onExistPlayerCleaned = { onNewPlayerChosen(item.number, "") },
                copiedPlayerToPast = copiedPlayerToPast,
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
            modifier = modifier.width(200.dp).height(60.dp),
            minValue = 5,
            maxValue = 20,
            stepValue = 1,
            startValue = startValue,
            onValueChanged = onValueChanged,
        )
    }
}

@Composable
fun ExistPlayers(
    modifier: Modifier = Modifier,
    selectedPlayer: PlayerState? = null,
    availablePlayers: List<PlayerState> = emptyList(),
    onClicked: (PlayerState) -> Unit = {},
) {
    var nameFilter by remember(availablePlayers) { mutableStateOf("") }
    val players by derivedStateOf {
        availablePlayers.filter { it.name.contains(nameFilter) }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            value = nameFilter,
            label = { Text("Поиск", color = BlackDark) },
            onValueChange = { nameFilter = it },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlackDark,
                unfocusedBorderColor = BlackDark,
                focusedLabelColor = BlackDark,
                unfocusedLabelColor = BlackDark,
            ),
        )

        LazyColumn(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            players.forEach { player ->
                val isSelected = player == selectedPlayer
                item(player.id) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onClicked(player) },
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = if (isSelected) {
                            Yellow
                        } else if (player.isPlayedToday) {
                            WhiteLight
                        } else {
                            White
                        }
                    ) {
                        Text(
                            style = MaterialTheme.typography.body2,
                            text = player.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }
    }
}