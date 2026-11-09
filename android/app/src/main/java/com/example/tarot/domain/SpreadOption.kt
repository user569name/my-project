package com.example.tarot.domain

enum class SpreadCategory(val label: String) {
    GENERAL("Общие"),
    LOVE("Любовь"),
    CAREER("Работа"),
    MONEY("Деньги"),
    FUTURE("Будущее"),
    DECISION("Решение"),
}

enum class SpreadOption(
    val apiValue: String,
    val title: String,
    val description: String,
    val category: SpreadCategory,
    val symbol: String,
    val positions: List<String>,
) {
    SINGLE(
        apiValue = "single",
        title = "Одна карта",
        description = "Короткий и ясный ответ на один конкретный вопрос.",
        category = SpreadCategory.GENERAL,
        symbol = "✦",
        positions = listOf("Ответ"),
    ),
    THREE(
        apiValue = "three",
        title = "Три карты",
        description = "Как ситуация развивалась, где вы сейчас и что ждёт впереди.",
        category = SpreadCategory.GENERAL,
        symbol = "☾",
        positions = listOf("Прошлое", "Настоящее", "Будущее"),
    ),
    CELTIC(
        apiValue = "celtic",
        title = "Кельтский крест",
        description = "Объёмный разбор: причины, препятствия, скрытые мотивы и итог.",
        category = SpreadCategory.GENERAL,
        symbol = "✚",
        positions = listOf(
            "Суть ситуации",
            "Вызов",
            "Прошлое",
            "Будущее",
            "Сознание",
            "Подсознание",
            "Совет",
            "Влияние извне",
            "Надежды и страхи",
            "Итог",
        ),
    ),
    HORSESHOE(
        apiValue = "horseshoe",
        title = "Подкова",
        description = "Семь карт о препятствиях, поддержке и вероятном исходе.",
        category = SpreadCategory.FUTURE,
        symbol = "⌒",
        positions = listOf(
            "Прошлое",
            "Настоящее",
            "Скрытые влияния",
            "Препятствие",
            "Окружение",
            "Совет",
            "Итог",
        ),
    ),
    CHOICE(
        apiValue = "choice",
        title = "Перекрёсток",
        description = "Сравните два варианта и поймите, какой путь ближе вам.",
        category = SpreadCategory.DECISION,
        symbol = "⚖",
        positions = listOf("Суть выбора", "Путь А", "Путь Б", "Что поможет", "Итог"),
    ),
    YES_NO(
        apiValue = "yesno",
        title = "Да или нет",
        description = "Быстрое решение: аргументы за, аргументы против и совет карт.",
        category = SpreadCategory.DECISION,
        symbol = "◐",
        positions = listOf("За", "Против", "Совет"),
    ),
    BRIDGE(
        apiValue = "bridge",
        title = "Мост между вами",
        description = "О чувствах, дистанции и том, что поможет сблизиться.",
        category = SpreadCategory.LOVE,
        symbol = "♥",
        positions = listOf(
            "Вы",
            "Партнёр",
            "Что между вами",
            "Что мешает",
            "Что сблизит",
            "Перспектива",
        ),
    ),
    SPARK(
        apiValue = "spark",
        title = "Искра",
        description = "О зарождающейся симпатии и о том, куда она может привести.",
        category = SpreadCategory.LOVE,
        symbol = "❥",
        positions = listOf(
            "Ваши чувства",
            "Чувства другого",
            "Что притягивает",
            "Что настораживает",
            "Куда ведёт",
        ),
    ),
    CAREER(
        apiValue = "career",
        title = "Карьерный компас",
        description = "Сильные стороны, скрытые возможности и следующий шаг в работе.",
        category = SpreadCategory.CAREER,
        symbol = "◈",
        positions = listOf(
            "Текущая ситуация",
            "Сильные стороны",
            "Скрытая возможность",
            "Препятствие",
            "Следующий шаг",
            "Результат",
        ),
    ),
    MONEY(
        apiValue = "money",
        title = "Денежный поток",
        description = "Откуда приходят деньги, куда уходят и где точка роста.",
        category = SpreadCategory.MONEY,
        symbol = "◍",
        positions = listOf(
            "Сейчас",
            "Что привлекает доход",
            "Куда утекает",
            "Что изменить",
            "Перспектива",
        ),
    ),
    MONTH(
        apiValue = "month",
        title = "Лунный месяц",
        description = "Прогноз на ближайший месяц: начало, середина, завершение и совет.",
        category = SpreadCategory.FUTURE,
        symbol = "☽",
        positions = listOf("Начало", "Середина", "Завершение", "Совет"),
    ),
    OPEN(
        apiValue = "open",
        title = "Открытая книга",
        description = "Что скрыто в ситуации и какой шаг прояснит её.",
        category = SpreadCategory.GENERAL,
        symbol = "▣",
        positions = listOf("Что скрыто", "Что важно знать", "Что делать", "Итог"),
    ),
    ;

    companion object {
        val featured: List<SpreadOption> = listOf(THREE, BRIDGE, CAREER, MONTH)

        fun fromApiValue(value: String): SpreadOption =
            entries.firstOrNull { it.apiValue == value } ?: THREE
    }
}
