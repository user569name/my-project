package com.example.tarot.domain

enum class PracticeCategory(
    val label: String,
    val description: String,
    val accent: Long,
) {
    NUMEROLOGY(
        label = "Нумерология",
        description = "Дата рождения как карта характера, ресурса и повторяющихся сюжетов.",
        accent = 0xFFE8C87C,
    ),
    ASTROLOGY(
        label = "Астрология",
        description = "Символические расчёты неба: без точных эфемерид, но с ясными ориентирами.",
        accent = 0xFFCABFFF,
    ),
    ARCHETYPES(
        label = "Архетипы, сны и имена",
        description = "Истории души, язык сновидений и сила имени.",
        accent = 0xFFEFA6BD,
    ),
}

enum class PracticeFieldType { DATE, TIME, TEXT, TEXTAREA, SELECT }

data class PracticeField(
    val key: String,
    val label: String,
    val type: PracticeFieldType,
    val placeholder: String = "",
    val hint: String? = null,
    val required: Boolean = false,
    val options: List<Pair<String, String>> = emptyList(),
)

data class PracticeMode(
    val id: String,
    val label: String,
)

data class Practice(
    val id: String,
    val category: PracticeCategory,
    val title: String,
    val subtitle: String,
    val symbol: String,
    val accent: Long,
    val description: String,
    val chips: List<String>,
    val fields: List<PracticeField>,
    val action: String,
    val modes: List<PracticeMode>,
)

data class PracticeBlock(
    val label: String,
    val value: String,
    val text: String = "",
)

sealed interface PracticeChart {
    data class Pythagoras(val cells: List<PythagorasCell>) : PracticeChart
    data class Fate(val series: FateSeries) : PracticeChart
    data class Hexagram(val lines: List<HexLine>) : PracticeChart
    data class Pillars(val pillars: List<PillarInfo>) : PracticeChart
    data class Cities(val items: List<CityScore>) : PracticeChart
    data class Spheres(val items: List<SphereScore>) : PracticeChart
}

data class PythagorasCell(val digit: Int, val count: Int)

data class FateSeries(
    val points: List<Int>,
    val labels: List<String>,
)

data class HexLine(
    val yang: Boolean,
    val changing: Boolean,
)

data class PillarInfo(
    val label: String,
    val stem: String,
    val branch: String,
    val element: String,
)

data class CityScore(
    val name: String,
    val country: String,
    val score: Int,
)

data class SphereScore(
    val name: String,
    val value: Int,
)

data class PracticeResult(
    val title: String,
    val subtitle: String,
    val blocks: List<PracticeBlock>,
    val note: String,
    val chart: PracticeChart? = null,
)