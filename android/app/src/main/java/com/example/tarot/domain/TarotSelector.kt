package com.example.tarot.domain

import com.example.tarot.data.remote.TarotCardDto
import kotlin.math.max
import kotlin.random.Random

/**
 * Тема вопроса, распознанная по тексту и категории расклада.
 *
 * Темы используются для взвешенного выбора карт: колода остаётся полной,
 * но карты, созвучные вопросу, выпадают чаще.
 */
data class TarotTheme(
    val topics: List<String>,
    val label: String,
) {
    companion object {
        val General = TarotTheme(topics = emptyList(), label = "общий вопрос")
    }
}

object TarotSelector {

    private data class TopicRule(
        val label: String,
        val words: List<String>,
        val keywords: List<String>,
        val suits: Map<String, Double>,
    )

    private val TOPIC_RULES: Map<String, TopicRule> = linkedMapOf(
        "love" to TopicRule(
            label = "любовь и отношения",
            words = listOf(
                "любов", "отнош", "партн", "брак", "свадь", "свидан", "развод", "ревн",
                "изменя", "изменил", "измену", "влюб", "роман", "муж", "жен", "девуш",
                "парен", "бывш", "вмест", "близк", "семей", "сердц", "поцел", "флирт",
                "симпат", "расстават", "расстал", "чувств",
            ),
            keywords = listOf(
                "отношения", "партнёрство", "союз", "чувства", "желание", "искра",
                "исцеление", "гармония", "забота", "страхи",
            ),
            suits = mapOf("cups" to 1.5, "major" to 0.5),
        ),
        "career" to TopicRule(
            label = "дело и карьера",
            words = listOf(
                "работ", "карьер", "проект", "бизнес", "начальник", "коллег", "собесед",
                "увол", "офис", "професс", "служб", "подчин", "команд", "стартап",
                "задач", "должност", "повышен", "переговор", "клиент", "сотрудник",
            ),
            keywords = listOf(
                "мастерство", "призвание", "действие", "структура", "правила", "победа",
                "сотрудничество", "проявление", "потенциал", "стабильность", "власть", "успех",
            ),
            suits = mapOf("wands" to 1.3, "pentacles" to 0.8, "swords" to 0.4),
        ),
        "money" to TopicRule(
            label = "деньги и ресурс",
            words = listOf(
                "деньг", "доход", "зарплат", "финанс", "кредит", "долг", "инвест",
                "покуп", "продаж", "богат", "бюджет", "накоплен", "оплат", "расход",
                "прибыл", "убыл", "цен",
            ),
            keywords = listOf(
                "деньги", "изобилие", "стабильность", "структура", "последствия",
                "успех", "мастерство", "ценности", "баланс",
            ),
            suits = mapOf("pentacles" to 1.8, "major" to 0.35),
        ),
        "future" to TopicRule(
            label = "будущее и перемены",
            words = listOf(
                "будущ", "что будет", "прогноз", "перспектив", "гряд", "ждать", "скоро",
                "когда", "изменится", "впереди", "шанс", "возможност", "предстоит", "сбудется",
            ),
            keywords = listOf(
                "судьба", "циклы", "движение", "начало", "надежда", "потенциал",
                "перемены", "обновление", "рост", "прорыв", "пробуждение", "призвание",
                "успех", "итоги",
            ),
            suits = mapOf("major" to 1.2, "wands" to 0.7, "cups" to 0.2),
        ),
        "decision" to TopicRule(
            label = "выбор и решение",
            words = listOf(
                "выбор", "решит", "решаю", "стоит ли", "начать ли", "уйти ли", "согласит",
                "между", "вариант", "совет", "как поступит", "что делать", "не знаю",
                "сомнева", "правильно ли",
            ),
            keywords = listOf(
                "выбор", "ясность", "истина", "внутренний голос", "последствия", "баланс",
                "пауза", "новый взгляд", "поиск истины", "испытание", "мысли",
            ),
            suits = mapOf("swords" to 1.2, "major" to 0.9),
        ),
        "self" to TopicRule(
            label = "внутренний путь",
            words = listOf(
                "себя", "предназнач", "смысл", "душ", "развит", "страх", "тревог",
                "счаст", "уверен", "энерг", "устал", "выгор", "здоров", "самопозна",
                "внутренн", "род", "карм", "духовн",
            ),
            keywords = listOf(
                "трансформация", "ценности", "тень", "пробуждение", "подсознание",
                "уединение", "мудрость", "внутренний голос", "исцеление", "целостность",
                "судьба", "терпение", "страхи", "баланс", "гармония", "завершение", "отпускание",
            ),
            suits = mapOf("major" to 1.1, "cups" to 0.7),
        ),
        "family" to TopicRule(
            label = "семья и близкие",
            words = listOf(
                "семь", "мам", "пап", "родител", "ребён", "ребен", "дочь", "дочер", "сын",
                "родн", "брат", "сестр", "бабушк", "дедушк", "род",
            ),
            keywords = listOf(
                "забота", "традиции", "стабильность", "отношения", "союз", "опыт",
                "терпение", "дом",
            ),
            suits = mapOf("cups" to 1.2, "pentacles" to 0.7),
        ),
        "travel" to TopicRule(
            label = "дорога и перемены",
            words = listOf(
                "путешеств", "поездк", "переезд", "страна", "виз", "билет", "уехать",
                "эмиграц", "за границ", "отпуск", "место силы", "город",
            ),
            keywords = listOf(
                "движение", "свобода", "скорость", "новый взгляд", "перемены",
                "поиск истины", "начало",
            ),
            suits = mapOf("wands" to 1.1, "swords" to 0.7, "major" to 0.4),
        ),
        "study" to TopicRule(
            label = "учёба и знания",
            words = listOf(
                "учёб", "учеб", "экзамен", "школ", "универ", "обучен", "курс", "знани", "диплом",
            ),
            keywords = listOf(
                "мысли", "поиск истины", "мастерство", "правила", "наставничество",
                "потенциал", "опыт", "ясность", "терпение",
            ),
            suits = mapOf("swords" to 1.2, "major" to 0.5),
        ),
    )

    private val TOPIC_BY_CATEGORY: Map<SpreadCategory, String> = mapOf(
        SpreadCategory.LOVE to "love",
        SpreadCategory.CAREER to "career",
        SpreadCategory.MONEY to "money",
        SpreadCategory.FUTURE to "future",
        SpreadCategory.DECISION to "decision",
    )

    private val SPREAD_AFFINITY: Map<SpreadCategory, Map<String, Double>> = mapOf(
        SpreadCategory.LOVE to mapOf("cups" to 1.5, "major" to 0.5),
        SpreadCategory.CAREER to mapOf("wands" to 1.2, "pentacles" to 0.7, "swords" to 0.4),
        SpreadCategory.MONEY to mapOf("pentacles" to 1.7, "major" to 0.4),
        SpreadCategory.FUTURE to mapOf("major" to 1.1, "wands" to 0.6),
        SpreadCategory.DECISION to mapOf("swords" to 1.0, "major" to 0.9),
        SpreadCategory.GENERAL to emptyMap(),
    )

    private val NEGATIVE_WORDS = listOf(
        "страх", "проблем", "кризис", "ссор", "боль", "потер", "конфликт", "устал",
        "тревог", "долг", "развод", "расстават", "препятств", "мешает", "тяжел",
    )

    private val ADVICE_POSITION = Regex(
        "совет|что делать|следующий шаг|что поможет|что сблизит|что изменит|что важно знать",
    )
    private val OUTCOME_POSITION = Regex("итог|результат|перспектив|куда вед|финал")
    private val PAST_POSITION = Regex("прошл")
    private val FUTURE_POSITION = Regex("будущ")
    private val HIDDEN_POSITION = Regex("подсозна|скрыт|надежд|страх|настораж|что мешает")

    fun detectTheme(question: String, category: SpreadCategory): TarotTheme {
        val text = normalize(question)
        val scores = mutableMapOf<String, Double>()
        TOPIC_RULES.forEach { (topic, rule) ->
            var score = 0.0
            rule.words.forEach { word ->
                if (text.contains(word)) score += 1.0
            }
            if (score > 0.0) scores[topic] = score
        }
        TOPIC_BY_CATEGORY[category]?.let { topic ->
            scores[topic] = (scores[topic] ?: 0.0) + 0.75
        }
        val topics = scores.entries
            .sortedByDescending { it.value }
            .take(2)
            .map { it.key }
        if (topics.isEmpty()) return TarotTheme.General
        return TarotTheme(
            topics = topics,
            label = topics.mapNotNull { TOPIC_RULES[it]?.label }.joinToString(" · "),
        )
    }

    fun reversedChance(question: String): Float {
        val text = normalize(question)
        return if (NEGATIVE_WORDS.any { text.contains(it) }) 0.4f else 0.27f
    }

    /**
     * Выбирает карты под позиции расклада.
     *
     * Для каждой позиции считается вес карты: тема вопроса, масть расклада
     * и смысл позиции. Затем карта выбирается взвешенным случайным образом
     * и убирается из колоды, чтобы в раскладе не было повторов.
     */
    fun pick(
        deck: List<TarotCardDto>,
        spread: SpreadOption,
        question: String,
        random: Random = Random.Default,
    ): List<TarotCardDto> {
        if (deck.isEmpty()) return emptyList()
        val theme = detectTheme(question, spread.category)
        val pool = deck.toMutableList()
        val picked = ArrayList<TarotCardDto>(spread.positions.size)

        spread.positions.forEach { position ->
            if (pool.isEmpty()) return@forEach
            val weights = DoubleArray(pool.size) { index ->
                cardWeight(pool[index], position, theme, spread, random)
            }
            var ticket = random.nextDouble(weights.sum())
            var chosen = pool.lastIndex
            for (index in weights.indices) {
                ticket -= weights[index]
                if (ticket <= 0.0) {
                    chosen = index
                    break
                }
            }
            picked += pool.removeAt(chosen)
        }
        return picked
    }

    private fun cardWeight(
        card: TarotCardDto,
        positionName: String,
        theme: TarotTheme,
        spread: SpreadOption,
        random: Random,
    ): Double {
        var weight = 1.0
        theme.topics.forEach { topic ->
            weight += topicAffinity(card, topic) * 2.4
        }

        val affinity = SPREAD_AFFINITY[spread.category].orEmpty()
        weight += affinity[suitKey(card)] ?: 0.0

        val position = normalize(positionName)
        if (ADVICE_POSITION.containsMatchIn(position)) {
            weight += if (card.arcana == "major") 1.4 else 0.25
        }
        if (OUTCOME_POSITION.containsMatchIn(position)) {
            weight += if (card.arcana == "major") 0.8 else 0.2
        }
        if (PAST_POSITION.containsMatchIn(position) && card.suit in setOf("cups", "pentacles")) {
            weight += 0.4
        }
        if (FUTURE_POSITION.containsMatchIn(position) && card.suit == "wands") {
            weight += 0.45
        }
        if (HIDDEN_POSITION.containsMatchIn(position) && card.suit in setOf("cups", "swords")) {
            weight += 0.45
        }

        val jitter = 0.82 + random.nextDouble() * 0.42
        return max(0.05, weight * jitter)
    }

    private fun topicAffinity(card: TarotCardDto, topic: String): Double {
        val rule = TOPIC_RULES[topic] ?: return 0.0
        var score = if (keywordHit(card, rule.keywords)) 1.0 else 0.0
        score += rule.suits[suitKey(card)] ?: 0.0
        return score
    }

    private fun keywordHit(card: TarotCardDto, stems: List<String>): Boolean {
        val words = card.keywordsUpright + card.keywordsReversed
        return stems.any { stem -> words.any { normalize(it).contains(stem) } }
    }

    private fun suitKey(card: TarotCardDto): String =
        if (card.arcana == "major") "major" else card.suit.orEmpty()

    private fun normalize(value: String): String =
        value.lowercase().replace('ё', 'е')
}