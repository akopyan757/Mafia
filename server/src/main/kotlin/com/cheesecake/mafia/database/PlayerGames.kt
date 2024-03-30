package com.cheesecake.mafia.database

import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GamePlayerData
import com.cheesecake.mafia.data.GamePlayerRole
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

object PlayerGames: Table("playergame") {

    private const val SEPARATOR = ";"

    private val playerId = PlayerGames.long("playerId").uniqueIndex().references(Players.id)
    private val gameId = PlayerGames.long("gameId").uniqueIndex().references(Games.id)
    private val number = PlayerGames.byte("number")
    private val role = PlayerGames.varchar("role", 15)
    private val isWinner = PlayerGames.bool("isWinner")
    private val isAlive = PlayerGames.bool("isAlive")
    private val isDeleted = PlayerGames.bool("isDeleted")
    private val actions = PlayerGames.blob("actions")
    private val bestMove = PlayerGames.varchar("bestMove", 20)

    private val jsonArray = Json { useArrayPolymorphism = true }
    private val actionSerializer = ListSerializer(GameAction.serializer())

    fun insert(players: List<GamePlayerData>) {
        players.forEach { data ->
            insert {
                it[playerId] = data.playerId
                it[gameId] = data.gameId
                it[number] = data.number
                it[role] = data.role.name
                it[isWinner] = data.isWinner
                it[isAlive] = data.isAlive
                it[isDeleted] = data.isDeleted
                it[actions] = ExposedBlob(
                    jsonArray.encodeToString(actionSerializer, data.actions).toByteArray()
                )
                if (data.bestMove.isNotEmpty()) {
                    it[bestMove] = data.bestMove.joinToString(separator = SEPARATOR)
                }
            }
        }
    }

    fun delete(gameId: Long) {
        PlayerGames.deleteWhere { PlayerGames.gameId eq gameId }
    }

    fun fetchByGame(gameId: Long): List<GamePlayerData> {
        return try {
            transaction {
                (PlayerGames innerJoin Players)
                    .select { (playerId eq Players.id) and (PlayerGames.gameId eq gameId) }
                    .toList()
                    .map { it.mapPlayerGame() }
                    .sortedBy { it.number }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun fetchByPlayer(playerId: Long): List<GamePlayerData> {
        return try {
            transaction {
                (PlayerGames innerJoin Players)
                    .select { PlayerGames.playerId eq playerId }
                    .toList()
                    .map { it.mapPlayerGame() }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun ResultRow.mapPlayerGame(): GamePlayerData {
        return GamePlayerData(
            playerId = this[playerId],
            gameId = this[gameId],
            number = this[number],
            name = this[Players.name],
            role = GamePlayerRole.ofName(this[role]),
            isWinner = this[isWinner],
            isAlive = this[isAlive],
            isDeleted = this[isDeleted],
            actions = this[actions].bytes.toString(Charsets.UTF_8).let { json ->
                jsonArray.decodeFromString(actionSerializer, json)
            },
            bestMove = if (this[bestMove].isNotEmpty()) {
                this[bestMove].split(SEPARATOR).map { it.toInt() }
            } else {
                emptyList()
            }
        )
    }
}