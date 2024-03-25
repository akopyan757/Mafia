package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.data.PlayerData
import com.cheesecake.mafia.repository.PlayerRepository
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.InteractiveScreenState
import com.cheesecake.mafia.repository.InteractiveGameRepository
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.NewGameState
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.SelectPlayerState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewGameViewModel(
    private val repository: PlayerRepository,
    private val interactiveGameRepository: InteractiveGameRepository,
): ViewModel() {

    companion object {
        private const val DEFAULT_GAME_COUNT = 10
    }

    private val _state = MutableStateFlow(NewGameState())
    val state: StateFlow<NewGameState> get() = _state

    init {
        interactiveGameRepository.saveState(InteractiveScreenState.Main)
        val existPlayers = repository.selectAll().map { PlayerState(it.id, it.name) }
        _state.value = NewGameState(
            title = "Игра",
            items = List(DEFAULT_GAME_COUNT) { index ->
                NewGamePlayerItem(index + 1, SelectPlayerState.None, GamePlayerRole.None)
            },
            totalPlayers = existPlayers,
            availablePlayers = existPlayers,
        )
    }

    fun changeTitleValue(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun changeDate(value: String) {
        _state.value = _state.value.copy(date = value)
    }

    fun saveNewPlayers() {
        val newPlayers = _state.value.items
            .mapNotNull { (it.player as? SelectPlayerState.New)?.value }
            .map { value -> PlayerData(Random(value.hashCode()).nextInt().toLong(), name = value) }
        viewModelScope.launch {
            repository.insert(newPlayers)
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