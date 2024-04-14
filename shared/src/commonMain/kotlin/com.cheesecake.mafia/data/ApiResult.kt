package com.cheesecake.mafia.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class ApiResult<out T> {
    data object Loading: ApiResult<Nothing>()
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val exception: Throwable): ApiResult<Nothing>()
}

fun <T> ApiResult<T>.onLoading(callback: () -> Unit): ApiResult<T> {
    if (this == ApiResult.Loading) callback()
    return this
}

fun <T> ApiResult<T>.onSuccess(callback: (T) -> Unit): ApiResult<T> {
    (this as? ApiResult.Success)?.let { callback(it.data) }
    return this
}

fun <T> ApiResult<T>.onError(callback: (Throwable) -> Unit): ApiResult<T> {
    (this as? ApiResult.Error)?.let { callback(it.exception) }
    return this
}

suspend inline fun <T> flowResult(
    crossinline callback: suspend () -> T
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading)
    try {
        emit(ApiResult.Success(callback()))
    } catch (throwable: Throwable) {
        emit(ApiResult.Error(throwable))
    }
}