package com.cheesecake.mafia.data

import com.cheesecake.mafia.data.PlayerData
import kotlinx.serialization.Serializable

@Serializable
class AllPlayersResponse(
    val players: List<PlayerData>
)