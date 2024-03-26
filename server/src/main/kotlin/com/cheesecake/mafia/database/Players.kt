package com.cheesecake.mafia.database

import com.cheesecake.mafia.data.PlayerData
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Players: Table("player") {
    val id = Players.long("id").uniqueIndex()
    val name = Players.varchar("name", 30)

    fun insert(players: List<PlayerData>) {
        players.forEach { data ->
            Players.insertIgnore {
                it[id] = data.id
                it[name] = data.name
            }
        }
    }

    fun fetchAll(): List<PlayerData> {
        return try {
            transaction {
                Players.selectAll().toList().map {
                    PlayerData(
                        id = it[this@Players.id],
                        name = it[this@Players.name],
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}