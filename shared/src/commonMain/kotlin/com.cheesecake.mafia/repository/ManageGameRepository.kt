package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.ApiResult
import com.cheesecake.mafia.data.GameData

interface ManageGameRepository {
    suspend fun insert(item: GameData): ApiResult<Long>
    suspend fun deleteById(id: Long): ApiResult<Unit>
}