package com.example.computerclub.data.remote

import com.example.computerclub.data.model.ErrorResponseDto
import com.google.gson.Gson
import retrofit2.Response

object ApiErrorParser {
    private val gson = Gson()

    fun <T> message(response: Response<T>): String {
        val fallback = "Ошибка сервера: ${response.code()}"
        val body = response.errorBody()?.string().orEmpty()
        if (body.isBlank()) return fallback

        return runCatching {
            gson.fromJson(body, ErrorResponseDto::class.java).message ?: fallback
        }.getOrElse { fallback }
    }
}
