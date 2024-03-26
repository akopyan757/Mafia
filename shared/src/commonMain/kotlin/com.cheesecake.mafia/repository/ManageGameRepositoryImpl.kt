package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.Game
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.database.PlayerGame
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class ManageGameRepositoryImpl(
    driverFactory: IDriverFactory,
    private val client: HttpClient,
) : ManageGameRepository {

    private val database = Database(
        driverFactory.createDriver(Database.Schema)
    )
    private val dbQuery = database.databaseQueries

    private val jsonArray = Json { useArrayPolymorphism = true }

    override suspend fun insert(item: GameData) {
        withContext(Dispatchers.IO) {
            client.post("/game/save") {
                contentType(ContentType.Application.Json)
                setBody(jsonArray.encodeToString(GameData.serializer(), item))
            }
            /*
            dbQuery.transaction {
                dbQuery.insertGame(item.toDatabaseDto())
                item.players.forEach { playerDate ->
                    dbQuery.insertPlayerGame(playerGame = playerDate.toDatabaseDto())
                }
            }*/
        }
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        dbQuery.transaction {
            //dbQuery.deleteGameById(id)
            //dbQuery.deletePlayerFromGame(id)
            client.delete("game/delete/$id")
        }
    }
}