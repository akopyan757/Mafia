package com.cheesecake.mafia.di

import com.cheesecake.mafia.common.Const
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import org.koin.dsl.module

actual fun networkModule() = module {
    single {
        HttpClient(Apache5) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
                    host = Const.BASE_URL
                }
            }
        }
    }
}