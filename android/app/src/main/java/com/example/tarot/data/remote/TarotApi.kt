package com.example.tarot.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TarotApi {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/v1/auth/me")
    suspend fun me(): UserDto

    @GET("api/v1/tarot/cards")
    suspend fun cards(): List<TarotCardDto>

    @POST("api/v1/tarot/draw")
    suspend fun draw(@Body request: DrawRequest): ReadingDto

    @GET("api/v1/tarot/readings")
    suspend fun readings(@Query("limit") limit: Int = 50): List<ReadingDto>

    @GET("api/v1/tarot/readings/{id}")
    suspend fun reading(@Path("id") id: String): ReadingDto

    @DELETE("api/v1/tarot/readings/{id}")
    suspend fun deleteReading(@Path("id") id: String)
}
