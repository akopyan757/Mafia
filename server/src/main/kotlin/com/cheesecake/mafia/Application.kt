package com.cheesecake.mafia

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
        url = System.getenv("DATABASE_CONNECTION_STRING"),
        driver = System.getenv("DATABASE_DRIVER"),
        user = System.getenv("POSTGRES_USER"),
        password = System.getenv("POSTGRES_PASSWORD"),
    )

    embeddedServer(
        factory = Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureGameRouting()
    configurePlayerRouting()
}