package com.cheesecake.mafia.repository

import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.data.PlayerData

internal class PlayerRepositoryImpl(driverFactory: IDriverFactory): PlayerRepository {

    private val database = Database(driverFactory.createDriver(Database.Schema))
    private val dbQuery = database.databaseQueries

    override fun selectAll(): List<PlayerData> {
        return dbQuery.selectPlayers { id, name -> PlayerData(id, name) }.executeAsList()
    }

    override suspend fun insert(newPlayers: List<PlayerData>) {
        dbQuery.transaction {
            newPlayers.forEach { player ->
                dbQuery.insertPlayer(player.id, player.name)
            }
        }
    }

    override suspend fun deleteAll() {
        dbQuery.transaction {
            dbQuery.deleteAllPlayers()
        }
    }
}