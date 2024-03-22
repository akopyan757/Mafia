package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.PlayerData

interface PlayerRepository {
    fun selectAll(): List<PlayerData>
    suspend fun insert(newPlayers: List<PlayerData>)
    suspend fun deleteAll()
}