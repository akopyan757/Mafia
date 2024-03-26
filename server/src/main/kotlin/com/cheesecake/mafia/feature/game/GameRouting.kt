package com.cheesecake.mafia.feature.game

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureGameRouting() {
    routing {
        post("/game/save") {
            GameController(call).saveGame()
        }
        delete("/game/delete/{id}") {
            GameController(call).deleteGame()
        }
        get("/game/all") {
            GameController(call).fetchAllGames()
        }
        get("/game/{id}") {
            GameController(call).fetchGameById()
        }
        get("/game/player/{id}") {
            GameController(call).fetchPlayersGamesById()
        }
    }
}