package com.example.tarot.domain

enum class SpreadOption(
    val apiValue: String,
    val title: String,
    val description: String,
) {
    SINGLE(
        apiValue = "single",
        title = "Одна карта",
        description = "Быстрый ответ на один вопрос",
    ),
    THREE(
        apiValue = "three",
        title = "Три карты",
        description = "Прошлое, настоящее и будущее",
    ),
    CELTIC(
        apiValue = "celtic",
        title = "Кельтский крест",
        description = "Глубокий разбор ситуации из 10 карт",
    ),
}
