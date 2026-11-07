package com.example.tarot.data.remote

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

class ApiException(message: String) : Exception(message)

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> message ?: DEFAULT_ERROR
    is HttpException -> parseHttpError(this)
    is IOException -> "Нет соединения с сервером. Проверьте интернет и адрес API."
    else -> "Не удалось выполнить запрос. Попробуйте ещё раз."
}

private const val DEFAULT_ERROR = "Не удалось выполнить запрос. Попробуйте ещё раз."

private fun parseHttpError(error: HttpException): String {
    val fallback = when (error.code()) {
        401 -> "Требуется вход в аккаунт. Попробуйте войти заново."
        403 -> "Нет доступа к этому действию."
        404 -> "Запрошенные данные не найдены."
        409 -> "Такие данные уже существуют."
        422 -> "Проверьте правильность заполнения полей."
        in 500..599 -> "Сервер временно недоступен. Попробуйте позже."
        else -> "Ошибка запроса (${error.code()})."
    }

    val body = runCatching { error.response()?.errorBody()?.string() }.getOrNull()
    if (body.isNullOrBlank()) return fallback

    return runCatching {
        val detail = JsonParser.parseString(body).asJsonObject.get("detail")
        when {
            detail == null || detail.isJsonNull -> fallback
            detail.isJsonPrimitive -> detail.asString
            detail.isJsonArray -> detail.asJsonArray
                .mapNotNull { item ->
                    runCatching { item.asJsonObject.get("msg")?.asString }.getOrNull()
                }
                .joinToString("; ")
                .ifBlank { fallback }
            else -> fallback
        }
    }.getOrDefault(fallback)
}
