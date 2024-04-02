package com.cheesecake.mafia.feature.game

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameSaveResponse
import com.cheesecake.mafia.database.Games
import com.cheesecake.mafia.database.PlayerGames
import com.cheesecake.mafia.database.Players
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

class GameController(private val call: ApplicationCall) {

    private val jsonArray = Json { useArrayPolymorphism = true }
    suspend fun saveGame() {
        call.application.log.info("saveGame: request: start")
        val requestJson = call.receiveText()
        call.application.log.info("saveGame: request: body=$requestJson")
        try {
            val game = jsonArray.decodeFromString(GameData.serializer(), requestJson).generateIds()
            transaction {
                Games.insert(game)
                PlayerGames.insert(game.players)
                Players.insert(game.newPlayers())
            }
            val response = GameSaveResponse(game.id)
            val responseJson = jsonArray.encodeToString(GameSaveResponse.serializer(), response)
            call.application.log.info("saveGame: response: body=$responseJson")
            call.respond(responseJson)
        } catch (e: Exception) {
            call.application.log.error("saveGame: error:", e)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    suspend fun deleteGame() {
        val gameId = call.parameters["id"]?.toLongOrNull()
        if (gameId == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }
        transaction {
            Games.deleteGame(gameId)
            PlayerGames.delete(gameId)
        }
        call.respond(gameId)
    }

    suspend fun fetchAllGames() {
        val games = Games.fetchAll()
        val data = jsonArray.encodeToString(ListSerializer(GameData.serializer()), games)
        call.respond(data)
    }

    suspend fun fetchPlayersGamesById() {
        val playerId = call.parameters["id"]?.toLongOrNull()
        if (playerId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        val playerGames = PlayerGames.fetchByPlayer(playerId)
        call.respond(playerGames)
    }

    suspend fun fetchGameById() {
        val gameId = call.parameters["id"]?.toLongOrNull()
        if (gameId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        val game = Games.fetchById(gameId)?.copy(players =  PlayerGames.fetchByGame(gameId))
        if (game != null) {
            call.respond(jsonArray.encodeToString(GameData.serializer(), game))
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}