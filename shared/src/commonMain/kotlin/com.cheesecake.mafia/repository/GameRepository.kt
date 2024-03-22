package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.GameData

interface GameRepository {
    suspend fun selectAll(): List<GameData>
    suspend fun selectById(gameId: Long): GameData?
    suspend fun insert(item: GameData)
    suspend fun deleteById(id: Long)
}