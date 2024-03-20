package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.SelectPlayerState
import com.cheesecake.mafia.state.priority
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NewGameState(
    val items: List<NewGamePlayerItem> = emptyList(),
    val totalPlayers: List<PlayerState> = emptyList(),
    val availablePlayers: List<PlayerState> = emptyList(),
) {
    val isItemsFilled : Boolean
        get() = items.all { it.role != GamePlayerRole.None && it.player != SelectPlayerState.None }

    val rolesCount: List<Pair<GamePlayerRole, Int>>
        get() = items
            .groupBy { it.role }
            .map { (role, items) -> role to items.size }
            .filterNot { (role, count) -> count == 0 || role == GamePlayerRole.None }
            .sortedByDescending { (role, count) -> role.priority() * 100 + count  }
}

class NewGameStandingViewModel: ViewModel() {

    private val _state = MutableStateFlow(NewGameState())
    val state: StateFlow<NewGameState> get() = _state

    init {
        _state.value = NewGameState(
            items = listOf(
                NewGamePlayerItem(1, SelectPlayerState.New("AAA"), GamePlayerRole.Black.Mafia),
                NewGamePlayerItem(2, SelectPlayerState.New("BBB"), GamePlayerRole.Black.Mafia),
                NewGamePlayerItem(3, SelectPlayerState.New("CCC"), GamePlayerRole.Black.Don),
                NewGamePlayerItem(4, SelectPlayerState.New("DDD"), GamePlayerRole.White.Maniac),
                NewGamePlayerItem(5, SelectPlayerState.New("EEE"), GamePlayerRole.Red.Сivilian),
                NewGamePlayerItem(6, SelectPlayerState.New("FFF"), GamePlayerRole.Red.Сivilian),
                NewGamePlayerItem(7, SelectPlayerState.New("HHH"), GamePlayerRole.Red.Сivilian),
                NewGamePlayerItem(8, SelectPlayerState.New("III"), GamePlayerRole.Red.Doctor),
                NewGamePlayerItem(9, SelectPlayerState.New("JJJ"), GamePlayerRole.Red.Sheriff),
                NewGamePlayerItem(10, SelectPlayerState.New("KKK"), GamePlayerRole.Red.Whore),
            ),
            totalPlayers = players,
            availablePlayers = players,
        )
    }

    fun onPlayerCountsChanged(count: Int) {
        changeState { state ->
            if (count < state.items.size) {
                state.copy(items = state.items.take(count))
            } else if (count > state.items.size) {
                val newItems = List(count - state.items.size) { index ->
                    NewGamePlayerItem(state.items.size + index + 1)
                }
                state.copy(items = state.items + newItems)
            } else {
                state
            }
        }
    }

    fun onRoleChanged(number: Int, role: GamePlayerRole) {
        changeState { state ->
            state.copy(items = state.items.changeItem(number) { item -> item.copy(role = role) })
        }
    }

    fun onPlayerChosen(number: Int, playerState: PlayerState) {
        changeState { state ->
            state.copy(
                items = state.items.changeItem(number) { item ->
                    item.copy(player = SelectPlayerState.Exist(playerState))
                }
            ).copy(
                availablePlayers = state.getAvailablePlayers()
            )
        }
    }

    fun onNewPlayerNameChanged(number: Int, name: String) {
        changeState { state ->
            state.copy(
                items = state.items.changeItem(number) { player ->
                    val playerState = if (name.length >= 3) {
                        SelectPlayerState.New(name)
                    } else {
                        SelectPlayerState.None
                    }
                    player.copy(player = playerState)
                },
            ).copy(
                availablePlayers = state.getAvailablePlayers(),
            )
        }
    }

    private fun changeState(transform: (NewGameState) -> NewGameState) {
        _state.value = transform(_state.value)
    }

    private fun NewGameState.getAvailablePlayers(): List<PlayerState> {
        val attachedPlayers = items.mapNotNull { item ->
            (item.player as? SelectPlayerState.Exist)?.player
        }
        return totalPlayers.filterNot { attachedPlayers.contains(it) }
    }

    private fun List<NewGamePlayerItem>.changeItem(
        number: Int,
        transform: (NewGamePlayerItem) -> NewGamePlayerItem
    ): List<NewGamePlayerItem> {
        val players = toMutableList()
        val index = players.indexOfFirst { it.number == number }
        if (index != -1) players[index] = transform(players[index])
        return players.toList()
    }

    companion object {
        private val players = listOf(
            PlayerState(1, "Albert"),
            PlayerState(2, "Bob"),
            PlayerState(3, "Claus"),
            PlayerState(4, "Dona"),
            PlayerState(5, "Elena"),
            PlayerState(6, "Frank"),
            PlayerState(7, "Gina"),
            PlayerState(8, "Hope"),
            PlayerState(9, "Iran"),
            PlayerState(10, "Jakie"),
        )
    }
}