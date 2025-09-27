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
            val rawResponse = response.body<String>()
            println("SafeCall: Raw response body: $rawResponse")

            // Parse the response as the expected type
            val body = try {
                // Re-parse the raw response as the expected type T with ignoreUnknownKeys
                val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                json.decodeFromString<T>(rawResponse)
            } catch (e: Exception) {
                println("SafeCall: Failed to parse response: ${e.message}")
                throw e
            }

            println("SafeCall: Response body parsed successfully: $body")
            ApiResult.Success(body)
        } else {
            println("SafeCall: Error response - Status: ${response.status.value}, Description: ${response.status.description}")

            // Handle unauthorized (401) responses specially
            if (response.status == HttpStatusCode.Unauthorized) {
                println("SafeCall: 401 Unauthorized - triggering re-authentication")
                // Note: We'll need to inject UserSessionManager to handle this properly
                // For now, just return the error and let callers handle it
            }

            ApiResult.Error(response.status.value, response.status.description)
        }
    } catch (e: CancellationException) {
        println("SafeCall: Request was cancelled")
        throw e // Coroutine'in iptal edilmesi durumu
    } catch (e: Throwable) {
        println("SafeCall: Exception occurred: ${e.message}")

        // Check if it's a connection or timeout related exception
        when {
            e.message?.contains("cancelled", ignoreCase = true) == true -> {
                println("SafeCall: Request was cancelled during execution")
                return ApiResult.Error(-1, "The operation couldn't be completed")
            }
            e.message?.contains("timeout", ignoreCase = true) == true -> {
                println("SafeCall: Request timeout")
                return ApiResult.Error(-2, "Request timeout")
            }
            e.message?.contains("connection", ignoreCase = true) == true -> {
                println("SafeCall: Connection error")
                return ApiResult.Error(-3, "Connection error")
            }
            else -> {
                e.printStackTrace()
                ApiResult.Exception(e)
            }
        }
    }
}