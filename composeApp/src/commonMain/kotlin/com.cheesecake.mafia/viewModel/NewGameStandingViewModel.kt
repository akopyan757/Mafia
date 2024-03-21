package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.entities.PlayerData
import com.cheesecake.mafia.repository.PlayerRepository
import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.NewGameState
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.SelectPlayerState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewGameStandingViewModel(
    private val repository: PlayerRepository
): ViewModel() {

    private val _state = MutableStateFlow(NewGameState())
    val state: StateFlow<NewGameState> get() = _state


    init {
        val existPlayers = repository.selectAll().map { PlayerState(it.id, it.name) }
        print("\nPlayer start")
        existPlayers.forEachIndexed { index, player ->
            print("\nPlayer: $index. $player")
        }
        _state.value = NewGameState(
            items = List(10) { index ->
                NewGamePlayerItem(index + 1, SelectPlayerState.None, GamePlayerRole.None)
            },
            totalPlayers = existPlayers,
            availablePlayers = existPlayers,
        )
    }

    fun saveNewPlayers() {
        val newPlayers = _state.value.items.mapNotNull { it.player as? SelectPlayerState.New }
        viewModelScope.launch {
            repository.insert(
                newPlayers.map {
                    PlayerData(
                        Random(it.hashCode()).nextLong(),
                        name = it.value
                    )
                }
            )
            print("\nPlayer2 updated")
            repository.selectAll().forEachIndexed { index, playerData ->
                print("\nPlayer2: $index. $playerData")
            }
        }
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
            ).let {
                it.copy(availablePlayers = it.getAvailablePlayers())
            }
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
            ).let {
                it.copy(availablePlayers = it.getAvailablePlayers())
            }
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
}