package com.cheesecake.mafia.viewModel

import com.cheesecake.mafia.state.GamePlayerRole
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.state.PlayerState
import com.cheesecake.mafia.state.SelectPlayerState
import com.cheesecake.mafia.state.priority
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class NewGameStandingViewModel: ViewModel() {

    private val _playersCounts = MutableStateFlow(10)
    private val _items = MutableStateFlow(
        List(_playersCounts.value) { index -> NewGamePlayerItem(index + 1) }
    )
    private val _totalAvailablePlayer = players
    private val _availablePlayer = MutableStateFlow(_totalAvailablePlayer)

    val playersCount: StateFlow<Int>
        get() = _playersCounts

    val items: StateFlow<List<NewGamePlayerItem>>
        get() = _items

    val isItemsFilled : Flow<Boolean>
        get() = _items.map { items ->
            items.all { it.role != GamePlayerRole.None && it.player != SelectPlayerState.None }
        }

    val availablePlayer: StateFlow<List<PlayerState>>
        get() = _availablePlayer

    val rolesCount: Flow<List<Pair<GamePlayerRole, Int>>>
        get() = _items.map { roleItems ->
            roleItems.groupBy { it.role }
                .map { (role, items) -> role to items.size }
                .filterNot { (role, count) -> count == 0 || role == GamePlayerRole.None }
                .sortedByDescending { (role, count) -> role.priority() * 100 + count  }
        }

    fun onPlayerCountsChanged(count: Int) {
        _playersCounts.value = count
        if (count < _items.value.size) {
            _items.value = _items.value.take(count)
        } else if (count > _items.value.size) {
            _items.value += List(count - _items.value.size) { index ->
                NewGamePlayerItem(_items.value.size + index + 1)
            }
        }
    }

    fun onRoleChanged(number: Int, role: GamePlayerRole) {
        changeItem(number) { item -> item.copy(role = role) }
    }

    fun onPlayerChosen(number: Int, playerState: PlayerState) {
        changeItem(number) { item -> item.copy(player = SelectPlayerState.Exist(playerState)) }
        changeAvailablePlayers()
    }

    fun onNewPlayerNameChanged(number: Int, name: String) {
        changeItem(number) { item ->
            val playerState = if (name.length >= 3) {
                SelectPlayerState.New(name)
            } else {
                SelectPlayerState.None
            }
            item.copy(player = playerState)
        }
        changeAvailablePlayers()
    }

    private fun changeItem(number: Int, transform: (NewGamePlayerItem) -> NewGamePlayerItem) {
        val mutableList = _items.value.toMutableList()
        val index = mutableList.indexOfFirst { it.number == number }.takeIf { it != -1 } ?: return
        mutableList[index] = transform(mutableList[index])
        _items.value = mutableList.toList()
    }

    private fun changeAvailablePlayers() {
        val attachedPlayers = _items.value
            .mapNotNull { item -> item.player as? SelectPlayerState.Exist }
            .map { item -> item.player }
        val players = _totalAvailablePlayer.filterNot { item -> attachedPlayers.contains(item) }
        _availablePlayer.value = players
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