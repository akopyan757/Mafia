package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.GamePlayerRoleSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal class ReadGameRepositoryImpl(
    private val client: HttpClient,
) : ReadGameRepository {

    private val jsonArray = Json {
        useArrayPolymorphism = true
        serializersModule = SerializersModule {
            polymorphicDefaultDeserializer(GamePlayerRole::class) { GamePlayerRoleSerializer }
        }
    }

    override suspend fun selectAll(): List<GameData> = withContext(Dispatchers.IO) {
        val json: String = client.get("/game/all").body()
        jsonArray.decodeFromString(ListSerializer(GameData.serializer()), json)
    }

    override suspend fun selectById(gameId: Long): GameData? = withContext(Dispatchers.IO) {
        val json: String = client.get("/game/$gameId").body()
        println("\ngames: selectById: json: id: $gameId, $json")
        if (json.isNotEmpty()) {
            jsonArray.decodeFromString(GameData.serializer(), json)
        } else {
            null
        }
    }
}