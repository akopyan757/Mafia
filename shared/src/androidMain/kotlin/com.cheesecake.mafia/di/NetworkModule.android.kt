package com.cheesecake.mafia.di

import com.cheesecake.mafia.common.Const
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import org.koin.dsl.module

actual fun networkModule() = module {
    single {
        HttpClient(Android) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
                    host = Const.EMULATOR_URL
                }
            }
        }
    }
}