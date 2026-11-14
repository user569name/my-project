package com.example.tarot

import com.example.tarot.data.remote.TarotCardDto
import com.example.tarot.domain.SpreadCategory
import com.example.tarot.domain.SpreadOption
import com.example.tarot.domain.TarotSelector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class TarotSelectorTest {

    private val deck: List<TarotCardDto> by lazy {
        val candidates = listOf(
            File("src/main/assets/cards.json"),
            File("app/src/main/assets/cards.json"),
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("cards.json not found in ${candidates.map { it.absolutePath }}")
        val type = object : TypeToken<List<TarotCardDto>>() {}.type
        Gson().fromJson<List<TarotCardDto>>(file.readText(Charsets.UTF_8), type)
    }

    @Test
    fun `deck contains 78 unique cards`() {
        assertEquals(78, deck.size)
        assertEquals(78, deck.map { it.id }.distinct().size)
    }

    @Test
    fun `love question is detected`() {
        val theme = TarotSelector.detectTheme(
            question = "Что он ко мне чувствует и люблю ли я его?",
            category = SpreadCategory.LOVE,
        )
        assertTrue(theme.topics.contains("love"))
        assertTrue(theme.label.contains("любовь"))
    }

    @Test
    fun `money question is detected`() {
        val theme = TarotSelector.detectTheme(
            question = "Как увеличить доход и закрыть долги в этом году?",
            category = SpreadCategory.MONEY,
        )
        assertTrue(theme.topics.contains("money"))
        assertTrue(theme.label.contains("деньги"))
    }

    @Test
    fun `neutral question falls back to general theme`() {
        val theme = TarotSelector.detectTheme(
            question = "Расскажи мне что-нибудь важное",
            category = SpreadCategory.GENERAL,
        )
        assertEquals("общий вопрос", theme.label)
        assertTrue(theme.topics.isEmpty())
    }

    @Test
    fun `pick returns one unique card per position`() {
        SpreadOption.entries.forEach { spread ->
            val picked = TarotSelector.pick(
                deck = deck,
                spread = spread,
                question = "Что меня ждёт?",
                random = Random(spread.ordinal),
            )
            assertEquals(spread.positions.size, picked.size)
            assertEquals(spread.positions.size, picked.map { it.id }.distinct().size)
        }
    }

    @Test
    fun `love question favors cups and major arcana`() {
        val counts = mutableMapOf<String, Int>()
        repeat(120) { attempt ->
            val picked = TarotSelector.pick(
                deck = deck,
                spread = SpreadOption.SPARK,
                question = "Что он ко мне чувствует и есть ли у нас отношения?",
                random = Random(attempt),
            )
            picked.forEach { card ->
                val key = if (card.arcana == "major") "major" else card.suit.orEmpty()
                counts[key] = (counts[key] ?: 0) + 1
            }
        }

        val love = (counts["cups"] ?: 0) + (counts["major"] ?: 0)
        val pentacles = counts["pentacles"] ?: 0
        assertTrue("cups+major=$love, pentacles=$pentacles", love > pentacles * 3)
    }

    @Test
    fun `money question favors pentacles`() {
        val counts = mutableMapOf<String, Int>()
        repeat(100) { attempt ->
            val picked = TarotSelector.pick(
                deck = deck,
                spread = SpreadOption.HORSESHOE,
                question = "Как увеличить доход и накопления?",
                random = Random(attempt),
            )
            picked.forEach { card ->
                val key = if (card.arcana == "major") "major" else card.suit.orEmpty()
                counts[key] = (counts[key] ?: 0) + 1
            }
        }

        val pentacles = counts["pentacles"] ?: 0
        val swords = counts["swords"] ?: 0
        assertTrue("pentacles=$pentacles, swords=$swords", pentacles > swords)
    }

    @Test
    fun `negative question increases reversed chance`() {
        val negative = TarotSelector.reversedChance("Меня тревожит долг и страх потерять работу")
        val neutral = TarotSelector.reversedChance("Что меня ждёт в этом месяце?")
        assertTrue(negative > neutral)
    }
}