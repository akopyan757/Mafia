package com.cheesecake.mafia.repository

import com.cheesecake.mafia.data.ApiResult
import com.cheesecake.mafia.data.GameData
import com.cheesecake.mafia.data.GameSaveResponse
import com.cheesecake.mafia.database.Database
import com.cheesecake.mafia.database.IDriverFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class ManageGameRepositoryImpl(
    driverFactory: IDriverFactory,
    private val client: HttpClient,
) : ManageGameRepository {

    private val jsonArray = Json { useArrayPolymorphism = true }

    override suspend fun insert(item: GameData) = withContext(Dispatchers.IO) {
        try {
            val requestBody = jsonArray.encodeToString(GameData.serializer(), item)
            println("game: save: request: start")
            val response = client.post("/game/save") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val body = response.bodyAsText().let {
                jsonArray.decodeFromString(GameSaveResponse.serializer(), it)
            }
            ApiResult.Success(body.id)
        } catch (e: Exception) {
            println("insert")
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

    override suspend fun deleteById(id: Long): ApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            client.delete("game/delete/$id")
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}