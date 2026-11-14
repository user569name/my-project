package com.example.tarot.data

import android.content.Context
import com.example.tarot.data.remote.ApiException
import com.example.tarot.data.remote.ReadingCardDto
import com.example.tarot.data.remote.ReadingDto
import com.example.tarot.data.remote.TarotCardDto
import com.example.tarot.data.remote.UserDto
import com.example.tarot.domain.SpreadOption
import com.example.tarot.domain.TarotSelector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

/**
 * Локальный источник данных приложения.
 *
 * Колода карт лежит в assets, а история раскладов хранится на устройстве.
 * Приложению не нужен сервер, поэтому гадания работают без интернета.
 */
class TarotRepository(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val readingsType = object : TypeToken<List<ReadingDto>>() {}.type
    private val lock = Any()

    @Volatile
    private var cachedCards: List<TarotCardDto>? = null

    val isLoggedIn: Boolean = true

    private val localUser = UserDto(
        id = "local",
        email = "local@mystic.tarot",
        displayName = "путник",
        createdAt = "2026-01-01T00:00:00Z",
    )

    suspend fun register(email: String, password: String, displayName: String): UserDto = localUser

    suspend fun login(email: String, password: String): UserDto = localUser

    fun logout() = Unit

    suspend fun currentUser(): UserDto = localUser

    suspend fun draw(question: String, spread: SpreadOption): ReadingDto =
        withContext(Dispatchers.IO) {
            val positions = spread.positions
            val deck = loadCards()
            check(deck.size >= positions.size) { "В колоде недостаточно карт" }

            val trimmedQuestion = question.trim()
            val theme = TarotSelector.detectTheme(trimmedQuestion, spread.category)
            val picked = TarotSelector.pick(deck, spread, trimmedQuestion)
            val reversedChance = TarotSelector.reversedChance(trimmedQuestion)
            val cards = picked.mapIndexed { index, card ->
                ReadingCardDto(
                    position = index + 1,
                    positionName = positions[index],
                    reversed = Random.nextFloat() < reversedChance,
                    card = card,
                )
            }

            val reading = ReadingDto(
                id = UUID.randomUUID().toString(),
                question = trimmedQuestion,
                spread = spread.apiValue,
                spreadLabel = spread.title,
                createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                cards = cards,
                theme = theme.label,
            )

            synchronized(lock) {
                writeReadings((listOf(reading) + readReadings()).take(MAX_READINGS))
            }
            reading
        }

    suspend fun readings(limit: Int = 50): List<ReadingDto> = withContext(Dispatchers.IO) {
        val saved = synchronized(lock) { readReadings() }
        saved.take(limit.coerceAtLeast(0))
    }

    suspend fun reading(id: String): ReadingDto = withContext(Dispatchers.IO) {
        val found = synchronized(lock) { readReadings() }.firstOrNull { it.id == id }
        found ?: throw ApiException("Расклад не найден")
    }

    suspend fun deleteReading(id: String) {
        withContext(Dispatchers.IO) {
            synchronized(lock) {
                writeReadings(readReadings().filterNot { it.id == id })
            }
        }
    }

    private fun loadCards(): List<TarotCardDto> {
        cachedCards?.let { return it }
        return synchronized(lock) {
            cachedCards ?: run {
                val type = object : TypeToken<List<TarotCardDto>>() {}.type
                val json = appContext.assets
                    .open(CARDS_ASSET)
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
                val cards: List<TarotCardDto> = gson.fromJson(json, type) ?: emptyList()
                cards.also { cachedCards = it }
            }
        }
    }

    private fun readReadings(): List<ReadingDto> {
        val json = prefs.getString(KEY_READINGS, null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<ReadingDto>>(json, readingsType) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    private fun writeReadings(items: List<ReadingDto>) {
        prefs.edit().putString(KEY_READINGS, gson.toJson(items, readingsType)).apply()
    }

    private companion object {
        const val PREFS = "mystic_tarot_local"
        const val KEY_READINGS = "readings"
        const val CARDS_ASSET = "cards.json"
        const val MAX_READINGS = 100
    }
}
