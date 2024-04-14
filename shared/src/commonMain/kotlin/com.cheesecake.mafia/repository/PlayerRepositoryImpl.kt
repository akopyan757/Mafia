package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.PlayerData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class PlayerRepositoryImpl(private val client: HttpClient): PlayerRepository {

    override suspend fun selectAll() = withContext(Dispatchers.IO) {
        val json: String = client.get("/players").body()
        Json.decodeFromString(ListSerializer(PlayerData.serializer()), json)
    }

    override suspend fun insert(newPlayers: List<PlayerData>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById() {
        TODO("Not yet implemented")
    }

}