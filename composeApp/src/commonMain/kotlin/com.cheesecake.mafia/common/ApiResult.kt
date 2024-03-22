package com.cheesecake.mafia.common


sealed class ApiResult<out T> {
    data object Loading: ApiResult<Nothing>()
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val exception: Throwable): ApiResult<Nothing>()
}