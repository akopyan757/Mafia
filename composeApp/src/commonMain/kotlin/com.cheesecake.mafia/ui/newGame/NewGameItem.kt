package com.cheesecake.mafia.ui.newGame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.cheesecake.mafia.common.Black
import com.cheesecake.mafia.common.GreyLight
import com.cheesecake.mafia.common.Red
import com.cheesecake.mafia.common.RussianKey
import com.cheesecake.mafia.common.White
import com.cheesecake.mafia.common.WhiteLight
import com.cheesecake.mafia.common.imageResources
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.SelectPlayerState
import com.cheesecake.mafia.state.primaryColor
import com.cheesecake.mafia.state.secondaryColor
import com.cheesecake.mafia.ui.VerticalDivider
import com.cheesecake.mafia.ui.nameColumnWeight
import com.cheesecake.mafia.ui.positionColumnWeight
import com.cheesecake.mafia.ui.roleColumnWeight

@Composable
fun NewGameItem(
    modifier: Modifier = Modifier,
    player: SelectPlayerState,
    availablePlayers: List<PlayerState> = emptyList(),
    onPlayerChoose: (PlayerState) -> Unit = {},
    onNewPlayerClicked: (name: String) -> Unit = {},
    minPlayerNameLength: Int = 3,
    role: GamePlayerRole = GamePlayerRole.Red.Сivilian,
    onRoleChanged: (GamePlayerRole) -> Unit = {},
    availableRoles: List<GamePlayerRole> = emptyList(),
    number: Int = 1,
) {
    Row(
        modifier = modifier.fillMaxWidth().background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier.padding(vertical = 8.dp).weight(positionColumnWeight),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )

        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))

        NewGamePlayer(
            modifier = Modifier.weight(nameColumnWeight),
            player = player,
            availablePlayers = availablePlayers,
            onPlayerChoose = onPlayerChoose,
            onRoleChosen = onRoleChanged,
            onNewPlayerClicked = onNewPlayerClicked,
            minPlayerNameLength = minPlayerNameLength,
        )

        VerticalDivider(color = WhiteLight, modifier = Modifier.align(Alignment.CenterVertically))

        NewGameRole(
            modifier = Modifier.weight(roleColumnWeight),
            role = role,
            onRoleChanged = onRoleChanged,
            availableRoles = availableRoles,
        )
    }
}

@Composable
fun NewGamePlayer(
    modifier: Modifier = Modifier,
    player: SelectPlayerState,
    availablePlayers: List<PlayerState> = emptyList(),
    onPlayerChoose: (PlayerState) -> Unit = {},
    onRoleChosen: (GamePlayerRole) -> Unit = {},
    onNewPlayerClicked: (String) -> Unit = {},
    minPlayerNameLength: Int = 3,
) {
    var nameState by remember { mutableStateOf(TextFieldValue(player.name)) }
    var nameExpanded by remember { mutableStateOf(false) }
    val nameInteractionSource = remember { MutableInteractionSource() }
    val isNameFocused by nameInteractionSource.collectIsFocusedAsState()
    val menuPlayers = if (isNameFocused) {
        availablePlayers.filter { it.name.lowercase().contains(nameState.text.lowercase()) }
    } else {
        listOf()
    }
    val nameStatusText = when {
        player is SelectPlayerState.New && nameState.text.length >= minPlayerNameLength -> "New"
        player is SelectPlayerState.Exist -> "Exist"
        nameState.text.isNotEmpty() -> "Error"
        else -> ""
    }
    val color = if (player is SelectPlayerState.None) Red else Black
    val nameStatusColor = if (player == SelectPlayerState.None && nameState.text.isNotEmpty()) {
        Red
    } else {
        GreyLight
    }
    var menuItemFocusIndex by remember(player, nameState) { mutableStateOf(-1) }
    val dropdownMenuExpanded by derivedStateOf {
        nameExpanded && isNameFocused && menuPlayers.isNotEmpty()
    }
    val focusedRequest = LocalFocusManager.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = nameState,
            onValueChange = { newName ->
                val playerState = availablePlayers.find { player ->
                    player.name.lowercase() == newName.text.lowercase()
                }
                if (playerState != null) {
                    onPlayerChoose(playerState)
                    nameState =
                        TextFieldValue(playerState.name, TextRange(playerState.name.length))
                    nameExpanded = false
                } else {
                    if (newName.text.length >= minPlayerNameLength) {
                        onNewPlayerClicked(newName.text)
                    }
                    nameExpanded = newName.text.isNotEmpty()
                    nameState = newName
                }
            },
            interactionSource = nameInteractionSource,
            singleLine = true,
            textStyle = MaterialTheme.typography.body1.copy(color = color),
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp)
                .weight(1f)
                .onKeyEvent {
                    if (it.isCtrlPressed) {
                        when(it.key) {
                            Key.M, RussianKey.M.key -> onRoleChosen(GamePlayerRole.Black.Mafia)
                            Key.N, RussianKey.N.key -> onRoleChosen(GamePlayerRole.Black.Don)
                            Key.B, RussianKey.B.key -> onRoleChosen(GamePlayerRole.White.Maniac)
                            Key.W, RussianKey.W.key -> onRoleChosen(GamePlayerRole.Red.Whore)
                            Key.S, RussianKey.S.key -> onRoleChosen(GamePlayerRole.Red.Sheriff)
                            Key.C, RussianKey.C.key -> onRoleChosen(GamePlayerRole.Red.Сivilian)
                            Key.D, RussianKey.D.key -> onRoleChosen(GamePlayerRole.Red.Doctor)
                        }
                        true
                    } else if (it.key == Key.DirectionDown && dropdownMenuExpanded) {
                        menuItemFocusIndex += 1
                        menuItemFocusIndex %= menuPlayers.size
                        true
                    } else if (it.key == Key.Enter) {
                        if (menuItemFocusIndex >= 0 && dropdownMenuExpanded) {
                            val menuPlayer = menuPlayers[menuItemFocusIndex]
                            nameState =
                                TextFieldValue(menuPlayer.name, TextRange(menuPlayer.name.length))
                            onPlayerChoose(menuPlayer)
                        } else {
                            focusedRequest.moveFocus(FocusDirection.Down)
                        }
                        true
                    } else false
                }
        )

        Text(
            text = nameStatusText,
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.body2,
            color = nameStatusColor,
        )

        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { nameExpanded = false },
            properties = PopupProperties(focusable = false),
        ) {
            menuPlayers.forEachIndexed { index, player ->
                val menuItemBackground = if (menuItemFocusIndex == index) WhiteLight else White
                DropdownMenuItem(
                    modifier = Modifier.background(menuItemBackground),
                    onClick = {
                        nameState = TextFieldValue(player.name, TextRange(player.name.length))
                        onPlayerChoose(player)
                    },
                    content = {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun NewGameRole(
    modifier: Modifier = Modifier,
    role: GamePlayerRole = GamePlayerRole.Red.Сivilian,
    onRoleChanged: (GamePlayerRole) -> Unit = {},
    availableRoles: List<GamePlayerRole> = emptyList(),
) {
    var roleExpanded by remember { mutableStateOf(false) }
    var roleWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Row(
        modifier = modifier.fillMaxHeight(),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(role.primaryColor())
                .clickable { roleExpanded = !roleExpanded }
                .onSizeChanged { size -> roleWidth = with(density) { size.width.toDp() } }
                .onKeyEvent {
                            false
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (role.iconRes.isNotEmpty()) {
                Icon(
                    painter = imageResources(role.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).padding(start = 16.dp),
                    tint = role.secondaryColor(),
                )
            }
            Text(
                text = role.name,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = role.secondaryColor(),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = if (roleExpanded) {
                    imageResources("ic_arrow_drop_up.xml")
                } else {
                    imageResources("ic_arrow_drop_down.xml")
                },
                contentDescription = null,
                tint = role.secondaryColor(),
                modifier = Modifier.wrapContentWidth().padding(start = 4.dp, end = 4.dp),
            )
        }

        DropdownMenu(
            expanded = roleExpanded,
            onDismissRequest = { roleExpanded = false },
            modifier = Modifier.width(roleWidth),
        ) {
            availableRoles.forEachIndexed { index, role ->
                DropdownMenuItem(
                    modifier = Modifier.background(role.primaryColor()).height(32.dp),
                    onClick = {
                        onRoleChanged(role)
                        roleExpanded = false
                    },
                    content = {
                        Row {
                            if (role.iconRes.isNotEmpty()) {
                                Icon(
                                    painter = imageResources(role.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = role.secondaryColor(),
                                )
                            }
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = role.name,
                                style = MaterialTheme.typography.body1,
                                color = role.secondaryColor(),
                            )
                        }
                    },
                )
            }
        }
    }
}