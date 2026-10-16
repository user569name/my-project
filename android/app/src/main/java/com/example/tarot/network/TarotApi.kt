package com.example.tarot.network

import retrofit2.http.GET
import retrofit2.http.Query

data class TarotCardDto(val id: Int, val name: String, val meaning: String, val image_url: String)
data class PredictionResponseDto(val card: TarotCardDto, val prediction_text: String, val created_at: String)

interface TarotApi {
    @GET("api/v1/tarot/draw")
    suspend fun drawCard(@Query("user_id") userId: Int): PredictionResponseDto
}
