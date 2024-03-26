package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.GamePlayerRoleSerializer
import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.Game
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.database.PlayerGame
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.Identity.encode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

internal class ReadGameRepositoryImpl(
    driverFactory: IDriverFactory,
    private val client: HttpClient,
) : ReadGameRepository {

    private val database = Database(
        driverFactory.createDriver(Database.Schema)
    )
    private val dbQuery = database.databaseQueries
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

    override suspend fun selectById(gameId: Long): GameData = withContext(Dispatchers.IO) {
        val json: String = client.get("/game/$gameId").body()
        println("\ngames: selectById: json: id: $gameId, $json")
        jsonArray.decodeFromString(GameData.serializer(), json)
    }

    private fun Game.toData(): GameData = GameData(
        id = id,
        title = title,
        date = date,
        lastRound = lastRound.toByte(),
        lastDayType = Json.decodeFromString(DayType.serializer(), lastDayType),
        finishResult = Json.decodeFromString(GameFinishResult.serializer(), finishResult),
        totalTime = totalTime,
        players = emptyList()
    )

    private fun PlayerGame.toData() = GamePlayerData(
        playerId = playerId,
        gameId = gameId,
        number = number.toByte(),
        name = name,
        role = Json.decodeFromString(GamePlayerRole.serializer(), role),
        isWinner = isWinner,
        isAlive = isAlive,
        isDeleted = isDeleted,
        actions = jsonArray.decodeFromString(ListSerializer(GameAction.serializer()), actions),
    )


    private fun GameData.toDatabaseDto() = Game(
        id = id,
        title = title,
        date = date,
        lastRound = lastRound.toLong(),
        lastDayType = Json.encodeToString(DayType.serializer(), lastDayType),
        finishResult = Json.encodeToString(GameFinishResult.serializer(), finishResult),
        totalTime = totalTime
    )

    private fun GamePlayerData.toDatabaseDto() = PlayerGame(
        playerId = playerId,
        gameId = gameId,
        number = number.toLong(),
        name = name,
        role = Json.encodeToString(GamePlayerRole.serializer(), role),
        isWinner = isWinner,
        isAlive = isAlive,
        isDeleted = isDeleted,
        actions =  jsonArray.encodeToString(ListSerializer(GameAction.serializer()), actions),
    )
}