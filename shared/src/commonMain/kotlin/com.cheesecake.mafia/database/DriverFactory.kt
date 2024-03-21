package com.cheesecake.mafia.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

interface IDriverFactory {
  fun createDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver
}

expect class DriverFactory: IDriverFactory {
  override fun createDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver
}