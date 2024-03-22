package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.Game
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.database.PlayerGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class ManageGameRepositoryImpl(driverFactory: IDriverFactory) : ManageGameRepository {

    private val database = Database(
        driverFactory.createDriver(Database.Schema)
    )
    private val dbQuery = database.databaseQueries

    private val jsonArray = Json { useArrayPolymorphism = true }

    override suspend fun insert(item: GameData) = withContext(Dispatchers.IO) {
        dbQuery.transaction {
            dbQuery.insertGame(item.toDatabaseDto())
            item.players.forEach { playerDate ->
                dbQuery.insertPlayerGame(playerGame = playerDate.toDatabaseDto())
            }
        }
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        dbQuery.transaction {
            dbQuery.deleteGameById(id)
            dbQuery.deletePlayerFromGame(id)
        }
    }

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