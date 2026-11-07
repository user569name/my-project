package com.example.tarot

import com.example.tarot.data.remote.ReadingDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiModelsTest {

    private val gson = Gson()

    @Test
    fun `reading json is parsed including snake case fields`() {
        val json = """
            {
              "id": "b3d3f7a2-5e6a-4a4e-9a2b-1c2d3e4f5a6b",
              "question": "Стоит ли менять работу?",
              "spread": "single",
              "spread_label": "Одна карта",
              "created_at": "2026-11-08T12:00:00+00:00",
              "cards": [
                {
                  "position": 1,
                  "position_name": "Ответ",
                  "reversed": true,
                  "card": {
                    "id": 1,
                    "slug": "the-fool",
                    "name": "The Fool",
                    "name_ru": "Шут",
                    "arcana": "major",
                    "suit": null,
                    "number": 0,
                    "symbol": "0",
                    "keywords_upright": ["начало"],
                    "keywords_reversed": ["страх"],
                    "meaning_upright": "Новый путь",
                    "meaning_reversed": "Страх перемен"
                  }
                }
              ]
            }
        """.trimIndent()

        val reading = gson.fromJson(json, ReadingDto::class.java)

        assertEquals("Одна карта", reading.spreadLabel)
        assertEquals(1, reading.cards.size)
        assertTrue(reading.cards[0].reversed)
        assertEquals("Шут", reading.cards[0].card.nameRu)
        assertEquals("страх", reading.cards[0].card.keywordsReversed.first())
    }

    @Test
    fun `ordered cards sorts by position`() {
        val first = sampleCard(position = 2)
        val second = sampleCard(position = 1)
        val reading = ReadingDto(
            id = "id",
            question = "q",
            spread = "three",
            spreadLabel = "Три карты",
            createdAt = "2026-11-08T12:00:00+00:00",
            cards = listOf(first, second),
        )

        assertEquals(listOf(1, 2), reading.orderedCards.map { it.position })
    }

    @Test
    fun `spread option values match api contract`() {
        val values = com.example.tarot.domain.SpreadOption.entries.map { it.apiValue }
        assertEquals(listOf("single", "three", "celtic"), values)
        assertFalse(com.example.tarot.domain.SpreadOption.entries.any { it.title.isBlank() })
    }

    private fun sampleCard(position: Int): com.example.tarot.data.remote.ReadingCardDto {
        val card = com.example.tarot.data.remote.TarotCardDto(
            id = position,
            slug = "card-$position",
            name = "Card",
            nameRu = "Карта",
            arcana = "major",
            suit = null,
            number = position,
            symbol = "$position",
            keywordsUpright = emptyList(),
            keywordsReversed = emptyList(),
            meaningUpright = "upright",
            meaningReversed = "reversed",
        )
        return com.example.tarot.data.remote.ReadingCardDto(
            position = position,
            positionName = "Позиция",
            reversed = false,
            card = card,
        )
    }
}
