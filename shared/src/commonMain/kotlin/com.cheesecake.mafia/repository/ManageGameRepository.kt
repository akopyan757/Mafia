package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.GameData

interface ManageGameRepository {
    suspend fun insert(item: GameData)
    suspend fun deleteById(id: Long)
}