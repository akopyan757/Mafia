package com.cheesecake.mafia

import DATABASE_CONNECTION_STRING
import POSTGRES_PASSWORD
import POSTGRES_USER
import SERVER_PORT
import com.cheesecake.mafia.feature.game.configureGameRouting
import com.cheesecake.mafia.feature.player.configurePlayerRouting
import com.cheesecake.mafia.plugins.configureRouting
import com.cheesecake.mafia.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect(
        url = DATABASE_CONNECTION_STRING,
        driver = "org.postgresql.Driver",
        user = POSTGRES_USER,
        password = POSTGRES_PASSWORD,

    )

    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
    configureGameRouting()
    configurePlayerRouting()
    configureRouting()
}