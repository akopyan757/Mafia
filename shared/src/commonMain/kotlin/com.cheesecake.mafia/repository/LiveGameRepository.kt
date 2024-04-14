package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.ApiResult
import com.cheesecake.mafia.data.LiveGameData
import kotlinx.coroutines.flow.Flow

interface LiveGameRepository {
    suspend fun selectAll(): Flow<ApiResult<List<LiveGameData>>>
    suspend fun selectById(id: Long): Flow<ApiResult<LiveGameData>>
    suspend fun insertOrUpdate(item: LiveGameData): Flow<ApiResult<Long>>
    suspend fun deleteById(id: Long): Flow<ApiResult<Unit>>
}