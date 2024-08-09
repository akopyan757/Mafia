package com.cheesecake.mafia.feature.player

import com.cheesecake.mafia.data.PlayerData
import com.cheesecake.mafia.database.Games
import com.cheesecake.mafia.database.PlayerGames
import com.cheesecake.mafia.database.Players
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class PlayerController(private val call: ApplicationCall) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyy", Locale.ENGLISH)

    suspend fun fetchAll() {
        call.application.log.info("players:call")
        val gamesCount = PlayerGames.gameId.countDistinct()
        val todayDate = dateFormat.format(Date.valueOf(LocalDate.now()))
        call.application.log.info("players:today=$todayDate")
        val players = transaction {
            val joinPlayers = Players
                .join(PlayerGames, JoinType.LEFT, Players.id, PlayerGames.playerId)
                .join(Games, JoinType.INNER, PlayerGames.gameId, Games.id)
            val todayPlayersIds = joinPlayers.slice(Players.id)
                .selectAll()
                .andWhere { Games.date eq todayDate }
                .groupBy(Players.id)
                .map { row -> row[Players.id] }
            val allPlayers = joinPlayers.slice(Players.id, Players.name, gamesCount)
                .selectAll()
                .groupBy(Players.id)
            allPlayers.map { row ->
                PlayerData(
                    id = row[Players.id],
                    name = row[Players.name],
                    gamesCount = row[gamesCount].toInt(),
                    hasPlayedToday = row[Players.id] in todayPlayersIds,
                )
            }
        }
        call.respond(players)
    }
}