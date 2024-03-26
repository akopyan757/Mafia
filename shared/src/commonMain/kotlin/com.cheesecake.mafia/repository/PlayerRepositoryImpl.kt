package com.cheesecake.mafia.repository

import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.data.PlayerData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class PlayerRepositoryImpl(
    driverFactory: IDriverFactory,
    private val client: HttpClient
): PlayerRepository {

    private val database = Database(driverFactory.createDriver(Database.Schema))
    private val dbQuery = database.databaseQueries

    override suspend fun selectAll() = withContext(Dispatchers.IO) {
        /*
        return dbQuery.selectPlayers { id, name -> PlayerData(id, name) }.executeAsList()
         */
        val json: String = client.get("/players").body()
        println("\nplayers: selectAll: json: $json")
        Json.decodeFromString(ListSerializer(PlayerData.serializer()), json)
    }

    override suspend fun insert(newPlayers: List<PlayerData>) {
        /*
        dbQuery.transaction {
            newPlayers.forEach { player ->
                dbQuery.insertPlayer(player.id, player.name)
            }
        }*/

    }

    override suspend fun deleteAll() {
        dbQuery.transaction {
            dbQuery.deleteAllPlayers()
        }
    }
}