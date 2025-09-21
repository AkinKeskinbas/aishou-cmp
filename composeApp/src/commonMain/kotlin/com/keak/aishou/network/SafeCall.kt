package com.keak.aishou.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlin.coroutines.cancellation.CancellationException


// API sonuçları için genel bir sarmalayıcı
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String?) : ApiResult<Nothing>()
    data class Exception(val exception: Throwable) : ApiResult<Nothing>()
}

suspend fun <T : Any> ApiResult<T>.onSuccess(executable: suspend (T) -> Unit): ApiResult<T> =
    apply {
        if (this is ApiResult.Success) {
            executable(data)
        }
    }

suspend fun <T : Any> ApiResult<T>.onError(executable: suspend (code: Int, message: String?) -> Unit): ApiResult<T> =
    apply {
        if (this is ApiResult.Error) {
            executable(code, message)
        }
    }

suspend fun <T : Any> ApiResult<T>.onException(executable: suspend (Throwable) -> Unit): ApiResult<T> =
    apply {
        if (this is ApiResult.Exception) {
            executable(exception)
        }
    }

suspend inline fun <reified T : Any> handleApi(request: suspend () -> HttpResponse): ApiResult<T> {
    return try {
        val response = request()
        println("SafeCall: Response status: ${response.status.value} - ${response.status.description}")
        if (response.status.value in 200..299) {
            val body = response.body<T>()
            println("SafeCall: Response body parsed successfully")
            ApiResult.Success(body)
        } else {
            println("SafeCall: Error response - Status: ${response.status.value}, Description: ${response.status.description}")
            ApiResult.Error(response.status.value, response.status.description)
        }
    } catch (e: CancellationException) {
        throw e // Coroutine'in iptal edilmesi durumu
    } catch (e: Throwable) {
        println("SafeCall: Exception occurred: ${e.message}")
        e.printStackTrace()
        ApiResult.Exception(e)
    }
}