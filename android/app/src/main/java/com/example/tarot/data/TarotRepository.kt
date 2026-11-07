package com.example.tarot.data

import com.example.tarot.data.remote.DrawRequest
import com.example.tarot.data.remote.LoginRequest
import com.example.tarot.data.remote.ReadingDto
import com.example.tarot.data.remote.RegisterRequest
import com.example.tarot.data.remote.TarotApi
import com.example.tarot.data.remote.UserDto
import com.example.tarot.domain.SpreadOption

class TarotRepository(
    private val api: TarotApi,
    private val tokenStore: TokenStore,
) {

    val isLoggedIn: Boolean
        get() = tokenStore.isLoggedIn

    suspend fun register(email: String, password: String, displayName: String): UserDto {
        val response = api.register(
            RegisterRequest(
                email = email.trim(),
                password = password,
                displayName = displayName.trim(),
            )
        )
        tokenStore.token = response.accessToken
        return response.user
    }

    suspend fun login(email: String, password: String): UserDto {
        val response = api.login(LoginRequest(email = email.trim(), password = password))
        tokenStore.token = response.accessToken
        return response.user
    }

    fun logout() {
        tokenStore.clear()
    }

    suspend fun currentUser(): UserDto = api.me()

    suspend fun draw(question: String, spread: SpreadOption): ReadingDto =
        api.draw(DrawRequest(question = question.trim(), spread = spread.apiValue))

    suspend fun readings(limit: Int = 50): List<ReadingDto> = api.readings(limit)

    suspend fun reading(id: String): ReadingDto = api.reading(id)

    suspend fun deleteReading(id: String) {
        api.deleteReading(id)
    }
}
