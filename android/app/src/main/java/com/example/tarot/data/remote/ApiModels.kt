package com.example.tarot.data.remote

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("display_name") val displayName: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("created_at") val createdAt: String,
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: UserDto,
)

data class TarotCardDto(
    val id: Int,
    val slug: String,
    val name: String,
    @SerializedName("name_ru") val nameRu: String,
    val arcana: String,
    val suit: String?,
    val number: Int,
    val symbol: String,
    @SerializedName("keywords_upright") val keywordsUpright: List<String>,
    @SerializedName("keywords_reversed") val keywordsReversed: List<String>,
    @SerializedName("meaning_upright") val meaningUpright: String,
    @SerializedName("meaning_reversed") val meaningReversed: String,
)

data class ReadingCardDto(
    val position: Int,
    @SerializedName("position_name") val positionName: String,
    val reversed: Boolean,
    val card: TarotCardDto,
)

data class ReadingDto(
    val id: String,
    val question: String,
    val spread: String,
    @SerializedName("spread_label") val spreadLabel: String,
    @SerializedName("created_at") val createdAt: String,
    val cards: List<ReadingCardDto>,
) {
    val orderedCards: List<ReadingCardDto>
        get() = cards.sortedBy { it.position }
}

data class DrawRequest(
    val question: String,
    val spread: String,
)
