package com.example.tarot.domain

/**
 * Каталог символических практик.
 *
 * Все расчёты выполняются на устройстве и не требуют интернета.
 */
object PracticeCatalog {

    private val birthDate = PracticeField(
        key = "birthDate",
        label = "Дата рождения",
        type = PracticeFieldType.DATE,
        placeholder = "ДД.ММ.ГГГГ",
        required = true,
    )
    private val birthTime = PracticeField(
        key = "birthTime",
        label = "Время рождения",
        type = PracticeFieldType.TIME,
        placeholder = "ЧЧ:ММ",
        hint = "Если знаете — расчёт будет точнее",
    )
    private val birthPlace = PracticeField(
        key = "birthPlace",
        label = "Место рождения",
        type = PracticeFieldType.TEXT,
        placeholder = "Город",
    )
    private val partnerDate = PracticeField(
        key = "partnerDate",
        label = "Дата рождения партнёра",
        type = PracticeFieldType.DATE,
        placeholder = "ДД.ММ.ГГГГ",
        required = true,
    )
    private val childDate = PracticeField(
        key = "childDate",
        label = "Дата рождения ребёнка",
        type = PracticeFieldType.DATE,
        placeholder = "ДД.ММ.ГГГГ",
        required = true,
    )
    private val name = PracticeField(
        key = "name",
        label = "Имя",
        type = PracticeFieldType.TEXT,
        placeholder = "Например, Анна",
        required = true,
    )
    private val partnerName = PracticeField(
        key = "partnerName",
        label = "Имя партнёра",
        type = PracticeFieldType.TEXT,
        placeholder = "Например, Александр",
        required = true,
    )
    private val question = PracticeField(
        key = "question",
        label = "Ваш вопрос",
        type = PracticeFieldType.TEXTAREA,
        placeholder = "Сформулируйте вопрос как можно честнее",
        required = true,
    )
    private val dream = PracticeField(
        key = "dream",
        label = "Ваш сон",
        type = PracticeFieldType.TEXTAREA,
        placeholder = "Опишите сон: место, людей, ощущения, детали",
        required = true,
    )
    private val gender = PracticeField(
        key = "gender",
        label = "Кому подбираем имя",
        type = PracticeFieldType.SELECT,
        required = true,
        options = listOf("female" to "Девочке", "male" to "Мальчику"),
    )

    val all: List<Practice> = listOf(
        Practice(
            id = "matrix",
            category = PracticeCategory.NUMEROLOGY,
            title = "Матрица судьбы",
            subtitle = "Характер, деньги и любовь по дате",
            symbol = "◈",
            accent = 0xFFE8C87C,
            description = "Ключевые аркана даты рождения: характер, ресурс, отношения и предназначение.",
            chips = listOf("характер", "финансы", "отношения"),
            fields = listOf(birthDate),
            action = "Открыть матрицу",
            modes = listOf(
                PracticeMode("base", "База"),
                PracticeMode("year", "Личный год"),
                PracticeMode("finance", "Финансы"),
                PracticeMode("child", "Ребёнок"),
                PracticeMode("couple", "Пара"),
            ),
        ),
        Practice(
            id = "pythagoras",
            category = PracticeCategory.NUMEROLOGY,
            title = "Квадрат Пифагора",
            subtitle = "Психоматрица по дате рождения",
            symbol = "▦",
            accent = 0xFFD8C07A,
            description = "Девять ячеек о характере, энергии, здоровье, логике и жизненной опоре.",
            chips = listOf("психоматрица", "сильные стороны", "ресурс"),
            fields = listOf(birthDate),
            action = "Построить квадрат",
            modes = listOf(
                PracticeMode("base", "База"),
                PracticeMode("child", "Ребёнок"),
                PracticeMode("couple", "Пара"),
            ),
        ),
        Practice(
            id = "fate-graph",
            category = PracticeCategory.NUMEROLOGY,
            title = "График судьбы",
            subtitle = "Линия жизни по годам",
            symbol = "✦",
            accent = 0xFFE4B96D,
            description = "Семь точек жизненного цикла: где накапливается сила, а где нужен отдых.",
            chips = listOf("циклы", "повороты", "прогноз"),
            fields = listOf(birthDate),
            action = "Построить график",
            modes = listOf(
                PracticeMode("base", "Личный"),
                PracticeMode("couple", "Пара"),
            ),
        ),
        Practice(
            id = "energy",
            category = PracticeCategory.NUMEROLOGY,
            title = "Энергетический тип",
            subtitle = "Как вы обмениваетесь энергией",
            symbol = "◉",
            accent = 0xFFD7A85F,
            description = "Четыре способа быть в контакте с силой: источник, проводник, хранитель, преобразователь.",
            chips = listOf("энергия", "границы", "совместимость"),
            fields = listOf(birthDate),
            action = "Определить тип",
            modes = listOf(
                PracticeMode("base", "Тип"),
                PracticeMode("compatibility", "Совместимость"),
            ),
        ),
        Practice(
            id = "destiny-number",
            category = PracticeCategory.NUMEROLOGY,
            title = "Число судьбы",
            subtitle = "Главная тема жизненного пути",
            symbol = "∞",
            accent = 0xFFE8C87C,
            description = "Число даты и число имени: о чём ваша жизнь просит говорить громче.",
            chips = listOf("путь", "имя", "предназначение"),
            fields = listOf(birthDate),
            action = "Рассчитать число",
            modes = listOf(
                PracticeMode("base", "Дата"),
                PracticeMode("name", "Дата и имя"),
            ),
        ),
        Practice(
            id = "natal",
            category = PracticeCategory.ASTROLOGY,
            title = "Натальная карта",
            subtitle = "Небо минуты вашего рождения",
            symbol = "☉",
            accent = 0xFFCABFFF,
            description = "Солнце, Луна и символический асцендент — три опоры вашей астрологической карты.",
            chips = listOf("солнце", "луна", "асцендент"),
            fields = listOf(birthDate, birthTime, birthPlace),
            action = "Открыть карту",
            modes = listOf(
                PracticeMode("base", "Карта"),
                PracticeMode("solar", "Соляр"),
                PracticeMode("synastry", "Синастрия"),
            ),
        ),
        Practice(
            id = "transits",
            category = PracticeCategory.ASTROLOGY,
            title = "Транзиты",
            subtitle = "Энергия дня и символический прогноз",
            symbol = "☌",
            accent = 0xFFB9A7E6,
            description = "Солнце, Луна и лунный день: мягкий ориентир для решений и отдыха.",
            chips = listOf("день", "луна", "совет"),
            fields = emptyList(),
            action = "Поймать энергию дня",
            modes = listOf(PracticeMode("base", "Сегодня")),
        ),
        Practice(
            id = "bazi",
            category = PracticeCategory.ASTROLOGY,
            title = "Бацзы",
            subtitle = "Четыре столпа судьбы",
            symbol = "易",
            accent = 0xFFB9A7E6,
            description = "Год, месяц, день и час рождения в китайской традиции: стихии и опора.",
            chips = listOf("стихии", "столпы", "баланс"),
            fields = listOf(birthDate, birthTime),
            action = "Собрать столпы",
            modes = listOf(PracticeMode("base", "Четыре столпа")),
        ),
        Practice(
            id = "iching",
            category = PracticeCategory.ASTROLOGY,
            title = "Книга перемен",
            subtitle = "Шесть бросков монет",
            symbol = "☰",
            accent = 0xFFCABFFF,
            description = "Две триграммы складываются в образ: что происходит и куда стоит направить внимание.",
            chips = listOf("гексаграмма", "перемены", "совет"),
            fields = emptyList(),
            action = "Бросить монеты",
            modes = listOf(PracticeMode("coins", "Бросок")),
        ),
        Practice(
            id = "human-design",
            category = PracticeCategory.ASTROLOGY,
            title = "Дизайн человека",
            subtitle = "Тип и стратегия",
            symbol = "▲",
            accent = 0xFFB9A7E6,
            description = "Символический расчёт типа, стратегии, авторитета и активных центров.",
            chips = listOf("тип", "стратегия", "центры"),
            fields = listOf(birthDate, birthTime),
            action = "Открыть бодиграф",
            modes = listOf(PracticeMode("base", "Бодиграф")),
        ),
        Practice(
            id = "hologenetics",
            category = PracticeCategory.ASTROLOGY,
            title = "Хологенетика",
            subtitle = "11 сфер и ключи",
            symbol = "✧",
            accent = 0xFFCABFFF,
            description = "Одиннадцать сфер опыта: где у вас врождённая сила, а где нужен новый выбор.",
            chips = listOf("11 сфер", "ключи", "ресурс"),
            fields = listOf(birthDate),
            action = "Открыть сферы",
            modes = listOf(PracticeMode("base", "Сферы")),
        ),
        Practice(
            id = "astrocart",
            category = PracticeCategory.ASTROLOGY,
            title = "Астрокарта",
            subtitle = "Города силы",
            symbol = "⊕",
            accent = 0xFFB9A7E6,
            description = "Символическая карта мест: где легче вдохновляться, работать и восстанавливаться.",
            chips = listOf("города", "места силы", "путь"),
            fields = listOf(birthDate),
            action = "Найти города",
            modes = listOf(PracticeMode("base", "Карта")),
        ),
        Practice(
            id = "three-archetypes",
            category = PracticeCategory.ARCHETYPES,
            title = "Три архетипа",
            subtitle = "Три аркана по дате рождения",
            symbol = "♆",
            accent = 0xFFEFA6BD,
            description = "Суть, сердце и путь: три образа, которые подсвечивают ваш характер.",
            chips = listOf("арканы", "характер", "путь"),
            fields = listOf(birthDate),
            action = "Открыть архетипы",
            modes = listOf(PracticeMode("base", "Три архетипа")),
        ),
        Practice(
            id = "dreams",
            category = PracticeCategory.ARCHETYPES,
            title = "Сны",
            subtitle = "Толкование символов сна",
            symbol = "☾",
            accent = 0xFFEFA6BD,
            description = "Введите сон — найдём повторяющиеся образы и мягкий совет из подсознания.",
            chips = listOf("сонник", "образы", "подсознание"),
            fields = listOf(dream),
            action = "Истолковать сон",
            modes = listOf(PracticeMode("base", "Символы")),
        ),
        Practice(
            id = "book-of-fate",
            category = PracticeCategory.ARCHETYPES,
            title = "Книга судеб",
            subtitle = "Ответ на вопрос одной строкой",
            symbol = "▤",
            accent = 0xFFEFA6BD,
            description = "Задайте вопрос — книга откроет страницу с короткой подсказкой.",
            chips = listOf("вопрос", "страница", "ответ"),
            fields = listOf(question),
            action = "Открыть книгу",
            modes = listOf(PracticeMode("base", "Страница")),
        ),
        Practice(
            id = "past-life",
            category = PracticeCategory.ARCHETYPES,
            title = "Прошлая жизнь",
            subtitle = "История души и урок",
            symbol = "◐",
            accent = 0xFFE39AB5,
            description = "Символическая история о прошлом воплощении: роль, урок и задача.",
            chips = listOf("душа", "урок", "карма"),
            fields = listOf(birthDate),
            action = "Вспомнить историю",
            modes = listOf(PracticeMode("base", "История")),
        ),
        Practice(
            id = "names",
            category = PracticeCategory.ARCHETYPES,
            title = "Мир имён",
            subtitle = "Значение имени и число",
            symbol = "✒",
            accent = 0xFFEFA6BD,
            description = "Число имени, его характер и совместимость с другим именем. Плюс подбор имени.",
            chips = listOf("имя", "значение", "совместимость"),
            fields = listOf(name),
            action = "Раскрыть имя",
            modes = listOf(
                PracticeMode("meaning", "Значение"),
                PracticeMode("compatibility", "Совместимость"),
                PracticeMode("selection", "Подбор"),
            ),
        ),
    )

    fun byId(id: String): Practice? = all.firstOrNull { it.id == id }

    fun byCategory(category: PracticeCategory): List<Practice> =
        all.filter { it.category == category }

    fun fieldsFor(practice: Practice, modeId: String): List<PracticeField> = when (practice.id) {
        "matrix" -> when (modeId) {
            "child" -> listOf(childDate)
            "couple" -> listOf(birthDate, partnerDate)
            else -> listOf(birthDate)
        }
        "pythagoras" -> when (modeId) {
            "child" -> listOf(childDate)
            "couple" -> listOf(birthDate, partnerDate)
            else -> listOf(birthDate)
        }
        "fate-graph", "energy" ->
            if (modeId == "couple" || modeId == "compatibility") listOf(birthDate, partnerDate)
            else listOf(birthDate)
        "natal" -> when (modeId) {
            "synastry" -> listOf(birthDate, partnerDate)
            "solar" -> listOf(birthDate)
            else -> listOf(birthDate, birthTime, birthPlace)
        }
        "destiny-number" -> if (modeId == "name") listOf(birthDate, name) else listOf(birthDate)
        "names" -> when (modeId) {
            "compatibility" -> listOf(name, partnerName)
            "selection" -> listOf(birthDate, gender)
            else -> listOf(name)
        }
        else -> practice.fields
    }
}