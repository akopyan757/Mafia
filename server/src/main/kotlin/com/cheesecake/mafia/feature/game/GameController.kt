package com.cheesecake.mafia.feature.game

import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameSaveResponse
import com.cheesecake.mafia.database.Games
import com.cheesecake.mafia.database.PlayerGames
import com.cheesecake.mafia.database.Players
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

class GameController(private val call: ApplicationCall) {

    private val jsonArray = Json { useArrayPolymorphism = true }
    suspend fun saveGame() {
        val json = call.receive<String>()
        val game = jsonArray.decodeFromString(GameData.serializer(), json).generateIds()
        transaction {
            Games.insert(game)
            PlayerGames.insert(game.players)
            Players.insert(game.newPlayers())
        }
        call.respond(GameSaveResponse(game.id))
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