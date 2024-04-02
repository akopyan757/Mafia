package com.cheesecake.mafia.feature.player

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configurePlayerRouting() {
    routing {
        get("/players") {
            PlayerController(call).fetchAll()
        }
    }
}