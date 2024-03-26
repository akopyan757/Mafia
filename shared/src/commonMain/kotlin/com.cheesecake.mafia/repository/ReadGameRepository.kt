package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.GameData

interface ReadGameRepository {
    suspend fun selectAll(): List<GameData>
    suspend fun selectById(gameId: Long): GameData?
}