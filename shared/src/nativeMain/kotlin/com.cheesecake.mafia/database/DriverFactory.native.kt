package com.cheesecake.mafia.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual class DriverFactory: IDriverFactory {
    actual override fun createDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
        return TODO()//NativeSqliteDriver(Database.Schema, "test.db")
    }
}