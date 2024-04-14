package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.GameAction
import com.cheesecake.mafia.data.GamePlayerRole
import com.cheesecake.mafia.data.LiveGameData
import com.cheesecake.mafia.data.LivePlayerData
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.data.flowResult
import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.Game
import com.cheesecake.mafia.database.IDriverFactory
import com.cheesecake.mafia.database.PlayerGame
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class LiveGameRepositoryImpl(
    driverFactory: IDriverFactory,
): LiveGameRepository {

    private val database = Database(driverFactory.createDriver(Database.Schema))
    private val query = database.databaseQueries
    private val json = Json { useArrayPolymorphism = true }

    override suspend fun selectAll() = flowResult {
        query.transactionWithResult {
            query.selectAllGames().executeAsList().map {
                game -> game.mapGame().copy(players = selectPlayers(game.id))
            }
        }
    }

    override suspend fun selectById(id: Long) = flowResult {
        query.transactionWithResult {
            query.selectGameById(id).executeAsOneOrNull()?.mapGame()
                ?.copy(players = selectPlayers(id))
                ?: throw Exception("Game not found")
        }
    }

    private fun selectPlayers(gameId: Long): List<LivePlayerData> {
        return query.selectPlayersInGame(gameId).executeAsList().map { it.mapPlayer() }
    }

    override suspend fun insertOrUpdate(item: LiveGameData) = flowResult {
        query.transactionWithResult {
            query.insertGame(item.mapGame())
            item.players.forEach { player ->
                query.insertPlayerGame(player.copy(gameId = item.gameId).mapPlayer())
            }
            item.gameId
        }
    }

    override suspend fun deleteById(id: Long) = flowResult {
        query.transactionWithResult {
            query.deleteGameById(id)
            query.deletePlayerFromGame(id)
        }
    }

    private fun Game.mapGame(): LiveGameData {
        return LiveGameData(
            gameId = id,
            title = title,
            date = date,
            round = round.toByte(),
            firstSpeechPlayer = firstSpeechPlayer.toInt(),
            stage = json.decodeFromString(LiveStage.serializer(), stage),
            queueStage = json.decodeFromString(ListSerializer(LiveStage.serializer()), queueStage),
            voteCandidates = json.decodeFromString(ListSerializer(Int.serializer()), voteCandidates),
            totalTime = totalTime,
            nightActions = emptyMap(),
            players = emptyList(),
        )
    }

    private fun LiveGameData.mapGame(): Game {
        return Game(
            id = gameId,
            title = title,
            date = date,
            round = round.toLong(),
            firstSpeechPlayer = firstSpeechPlayer.toLong(),
            stage = json.encodeToString(LiveStage.serializer(), stage),
            queueStage = json.encodeToString(ListSerializer(LiveStage.serializer()), queueStage),
            voteCandidates = json.encodeToString(ListSerializer(Int.serializer()), voteCandidates),
            totalTime = totalTime,
        )
    }

    private fun PlayerGame.mapPlayer(): LivePlayerData {
        return LivePlayerData(
            playerId = playerId,
            gameId = gameId,
            number = number.toInt(),
            name = name,
            role = json.decodeFromString(GamePlayerRole.serializer(), role),
            isNewPlayer = isNewPlayer,
            isAlive = isAlive,
            isDeleted = isDeleted,
            isClient = isClient,
            isVoted = isVoted,
            isKilled = isKilled,
            actions = json.decodeFromString(ListSerializer(GameAction.serializer()), actions),
            fouls = fouls.toInt(),
            bestMove = bestMove?.let {
                json.decodeFromString(ListSerializer(Int.serializer()), bestMove)
            }.orEmpty()
        )
    }

    private fun LivePlayerData.mapPlayer(): PlayerGame {
        return PlayerGame(
            playerId = playerId,
            gameId = gameId,
            number = number.toLong(),
            name = name,
            role = json.encodeToString(GamePlayerRole.serializer(), role),
            isNewPlayer = isNewPlayer,
            isAlive = isAlive,
            isDeleted = isDeleted,
            isClient = isClient,
            isVoted = isVoted,
            isKilled = isKilled,
            actions = json.encodeToString(ListSerializer(GameAction.serializer()), actions),
            fouls = fouls.toLong(),
            bestMove = bestMove.takeIf { it.isNotEmpty() }?.let {
                json.encodeToString(ListSerializer(Int.serializer()), it)
            }
        )
    }
}