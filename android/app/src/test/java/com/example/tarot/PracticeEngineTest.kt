package com.example.tarot

import com.example.tarot.domain.PracticeCatalog
import com.example.tarot.domain.PracticeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeEngineTest {

    private val values = mapOf(
        "birthDate" to "1990-06-15",
        "birthTime" to "08:30",
        "birthPlace" to "Москва",
        "partnerDate" to "1988-11-02",
        "childDate" to "2018-03-10",
        "name" to "Анна",
        "partnerName" to "Александр",
        "dream" to "Я стою на берегу реки, вода спокойная, рядом летают птицы и светит солнце.",
        "question" to "Что мне важно понять про свою работу и деньги?",
        "gender" to "female",
    )

    @Test
    fun `catalog contains seventeen practices in three groups`() {
        assertEquals(17, PracticeCatalog.all.size)
        assertEquals(3, PracticeCatalog.all.map { it.category }.distinct().size)
        assertEquals(30, PracticeCatalog.all.sumOf { it.modes.size })
        assertEquals(17, PracticeCatalog.all.map { it.id }.distinct().size)
    }

    @Test
    fun `every practice and mode returns a filled result`() {
        val failures = mutableListOf<String>()

        PracticeCatalog.all.forEach { practice ->
            practice.modes.forEach { mode ->
                val result = PracticeEngine.calculate(practice.id, mode.id, values)
                if (result == null || result.blocks.isEmpty()) {
                    failures += "${practice.id}/${mode.id}"
                }
            }
        }

        assertEquals(emptyList<String>(), failures)
    }

    @Test
    fun `matrix of 1992-08-17 matches symbolic formula`() {
        val result = PracticeEngine.calculate(
            practiceId = "matrix",
            modeId = "base",
            values = values + ("birthDate" to "1992-08-17"),
        )
        assertNotNull(result)

        val blocks = result!!.blocks.associate { it.label to it.value }
        assertEquals("17. Звезда", blocks["Характер"])
        assertEquals("8. Сила", blocks["Энергия"])
        assertEquals("21. Мир", blocks["Деньги"])
        assertEquals("10. Колесо Фортуны", blocks["Предназначение"])
        assertEquals("11. Справедливость", blocks["Отношения"])
        assertEquals("17 · 8 · 21 · 10", blocks["Формула"])
    }

    @Test
    fun `name compatibility returns score and both names`() {
        val result = PracticeEngine.calculate("names", "compatibility", values)
        assertNotNull(result)

        val blocks = result!!.blocks.associate { it.label to it.value }
        assertTrue(blocks.containsKey("Анна"))
        assertTrue(blocks.containsKey("Александр"))
        assertTrue(blocks.getValue("Совместимость").endsWith("/ 10"))
    }

    @Test
    fun `dream finds known symbols`() {
        val result = PracticeEngine.calculate(
            practiceId = "dreams",
            modeId = "base",
            values = mapOf("dream" to "Мне снилась спокойная вода и полёт над рекой"),
        )
        assertNotNull(result)

        val labels = result!!.blocks.map { it.label }
        assertTrue(labels.contains("Вода"))
        assertTrue(labels.contains("Полёт"))
    }

    @Test
    fun `invalid date returns null instead of crashing`() {
        val result = PracticeEngine.calculate(
            practiceId = "matrix",
            modeId = "base",
            values = mapOf("birthDate" to "31.02.2020"),
        )
        assertEquals(null, result)
    }

    @Test
    fun `all date input formats are supported`() {
        assertNotNull(PracticeEngine.parseDate("1990-06-15"))
        assertNotNull(PracticeEngine.parseDate("15.06.1990"))
        assertNotNull(PracticeEngine.parseDate("15/06/1990"))
        assertEquals(null, PracticeEngine.parseDate("1990-13-40"))
    }
}