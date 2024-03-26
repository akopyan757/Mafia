package com.cheesecake.mafia.database

import com.cheesecake.mafia.data.DayType
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameFinishResult
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Games: Table("game") {
    val id = Games.long("id").uniqueIndex()
    private val title = Games.varchar("title", 20)
    private val date = Games.varchar("date", 10)
    private val lastRound = Games.byte("lastRound")
    private val lastDayType = Games.varchar("lastDayType", 10)
    private val result = Games.varchar("result", 10)
    private val totalTime = Games.long("totalTime")

    fun insert(data: GameData) {
        Games.insert {
            it[id] = data.id
            it[title] = data.title
            it[date] = data.date
            it[lastRound] = data.lastRound
            it[lastDayType] = data.lastDayType.value
            it[result] = data.finishResult.value
            it[totalTime] = data.totalTime
        }
    }

    fun deleteGame(gameId: Long) {
        Games.deleteWhere { id eq gameId }
    }

    fun fetchAll(): List<GameData> {
        return try {
            transaction {
                Games.selectAll().toList().map { mapToGameData(it) }.sortedBy { it.title }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun fetchById(gameId: Long): GameData? {
        return try {
            transaction {
                Games.select { Games.id eq gameId }.firstOrNull()?.let { mapToGameData(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun mapToGameData(resultRow: ResultRow): GameData {
        return GameData(
            id = resultRow[this@Games.id],
            title = resultRow[title],
            date = resultRow[date],
            lastRound = resultRow[lastRound],
            lastDayType = DayType.ofValue(resultRow[lastDayType]),
            finishResult = GameFinishResult.ofValue(resultRow[result]),
            totalTime = resultRow[totalTime],
            emptyList(),
        )
    }
}