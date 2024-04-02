package com.cheesecake.mafia.feature.player

import com.cheesecake.mafia.database.Players
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.response.respond

class PlayerController(private val call: ApplicationCall) {

    suspend fun fetchAll() {
        call.application.log.info("players:call")
        call.respond(Players.fetchAll())
    }
}