package com.example.tarot.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

/**
 * Символический расчёт практик.
 *
 * Движок не зависит от Android и работает полностью офлайн.
 */
object PracticeEngine {

    private data class Arcana(
        val name: String,
        val light: String,
        val shadow: String,
    )

    private data class Sign(
        val name: String,
        val element: String,
        val from: Int,
        val to: Int,
        val text: String,
    )

    private data class NumberMeaning(
        val title: String,
        val text: String,
        val advice: String,
    )

    private data class Stem(val name: String, val element: String)
    private data class Branch(val name: String, val animal: String, val element: String)

    private data class Trigram(
        val name: String,
        val image: String,
        val text: String,
    )

    private data class Sphere(val name: String, val text: String)
    private data class City(val name: String, val country: String)
    private data class DreamSymbol(val keys: List<String>, val title: String, val text: String)
    private data class PastLife(val era: String, val role: String, val lesson: String, val task: String)
    private data class EnergyType(
        val name: String,
        val text: String,
        val strong: String,
        val risk: String,
    )
    private data class HdType(val name: String, val strategy: String, val text: String)

    private val ARCANA = listOf(
        Arcana("Маг", "воля, ресурс и умение начинать", "манипуляция и страх заявить о себе"),
        Arcana("Верховная Жрица", "интуиция, тишина и внутреннее знание", "отрыв от реальности и скрытность"),
        Arcana("Императрица", "забота, изобилие и творческая сила", "гиперопека и застой"),
        Arcana("Император", "опора, границы и порядок", "жёсткость и контроль"),
        Arcana("Иерофант", "смысл, наставничество и традиция", "догмы и чужие правила"),
        Arcana("Влюблённые", "выбор, союз и верность ценностям", "сомнения и разрыв с собой"),
        Arcana("Колесница", "движение, победа и собранность", "спешка и борьба без цели"),
        Arcana("Сила", "мягкая сила, терпение и самообладание", "подавленные эмоции"),
        Arcana("Отшельник", "уединение, поиск и мудрость", "изоляция и бегство от людей"),
        Arcana("Колесо Фортуны", "циклы, поворот и удача", "ожидание чуда и потери контроля"),
        Arcana("Справедливость", "баланс, правда и последствия", "осуждение и холодность"),
        Arcana("Повешенный", "пауза, новый ракурс и доверие", "застревание и роль жертвы"),
        Arcana("Смерть", "завершение, трансформация и обновление", "страх перемен и цепляние за прошлое"),
        Arcana("Умеренность", "гармония, исцеление и баланс", "крайности и нетерпение"),
        Arcana("Дьявол", "сила желания и честность с тенью", "зависимость и самообман"),
        Arcana("Башня", "прорыв, правда и освобождение", "разрушение и сопротивление"),
        Arcana("Звезда", "надежда, вдохновение и исцеление", "иллюзии и разочарование"),
        Arcana("Луна", "воображение, чувства и глубина", "тревога и туман"),
        Arcana("Солнце", "радость, ясность и успех", "выгорание и желание нравиться"),
        Arcana("Суд", "пробуждение, зов и итог", "самокритика и откладывание"),
        Arcana("Мир", "целостность, завершение и путь", "страх финала и новый круг"),
        Arcana("Шут", "свобода, спонтанность и начало", "безответственность и хаос"),
    )

    private val SIGNS = listOf(
        Sign("Овен", "fire", 321, 419, "импульс, смелость и желание действовать первым"),
        Sign("Телец", "earth", 420, 520, "устойчивость, чувственность и умение создавать опору"),
        Sign("Близнецы", "air", 521, 620, "любопытство, слово и лёгкость связей"),
        Sign("Рак", "water", 621, 722, "забота, память и потребность в безопасности"),
        Sign("Лев", "fire", 723, 822, "творчество, щедрость и право быть заметным"),
        Sign("Дева", "earth", 823, 922, "точность, польза и внимание к деталям"),
        Sign("Весы", "air", 923, 1022, "баланс, эстетика и искусство договора"),
        Sign("Скорпион", "water", 1023, 1121, "глубина, страсть и способность к трансформации"),
        Sign("Стрелец", "fire", 1122, 1221, "смысл, горизонт и вера в большое"),
        Sign("Козерог", "earth", 1222, 119, "цель, ответственность и долгая дистанция"),
        Sign("Водолей", "air", 120, 218, "свобода, идея и взгляд со стороны"),
        Sign("Рыбы", "water", 219, 320, "чувствительность, воображение и сострадание"),
    )

    private val ELEMENT_NAMES = mapOf(
        "fire" to "Огонь",
        "earth" to "Земля",
        "air" to "Воздух",
        "water" to "Вода",
    )

    private val ELEMENT_ADVICE = mapOf(
        "fire" to "действуйте, но не сжигайте себя: сегодня важен ритм, а не скорость.",
        "earth" to "закрепите результат делом: тело, деньги и порядок вернут опору.",
        "air" to "проговорите важное и проверьте факты: ясность приходит через обмен.",
        "water" to "не спешите отвечать: чувства скажут больше, чем первый вывод.",
    )

    private val ELEMENT_COMPAT = mapOf(
        "fire" to mapOf("fire" to 8, "air" to 10, "earth" to 4, "water" to 3),
        "earth" to mapOf("fire" to 4, "air" to 3, "earth" to 9, "water" to 10),
        "air" to mapOf("fire" to 10, "air" to 8, "earth" to 3, "water" to 4),
        "water" to mapOf("fire" to 3, "air" to 4, "earth" to 10, "water" to 9),
    )

    private val NUMBER_MEANINGS = mapOf(
        1 to NumberMeaning("Первопроходец", "главная тема — самостоятельность, воля и право начать своё.", "Не ждите разрешения: маленький первый шаг важнее идеального плана."),
        2 to NumberMeaning("Связующее звено", "важны отношения, чувствительность и умение слышать другого.", "Не растворяйтесь в чужих ожиданиях: ваш голос тоже имеет вес."),
        3 to NumberMeaning("Творец", "энергия выражения, слова и радости через создание.", "Покажите миру то, что откладывали: творчество лечит."),
        4 to NumberMeaning("Строитель", "тема устойчивости, труда, порядка и долгих результатов.", "Стройте по кирпичу: стабильность любит регулярность."),
        5 to NumberMeaning("Искатель", "перемены, свобода, опыт и вкус к новому.", "Не путайте движение с бегством: выбирайте то, что расширяет вас."),
        6 to NumberMeaning("Хранитель", "забота, дом, ответственность и красота отношений.", "Сначала наполните себя: из пустоты трудно любить."),
        7 to NumberMeaning("Исследователь", "глубина, анализ, уединение и поиск смысла.", "Доверяйте тишине, но не превращайте её в крепость."),
        8 to NumberMeaning("Распорядитель", "воля, деньги, власть и умение держать масштаб.", "Сила растёт там, где есть честность с собой."),
        9 to NumberMeaning("Проводник", "смысл, отдача, завершение циклов и широкий взгляд.", "Отпускайте то, что уже отслужило: место освобождается для нового."),
    )

    private val STEMS = listOf(
        Stem("Цзя", "Дерево"), Stem("И", "Дерево"),
        Stem("Бин", "Огонь"), Stem("Дин", "Огонь"),
        Stem("У", "Земля"), Stem("Цзи", "Земля"),
        Stem("Гэн", "Металл"), Stem("Синь", "Металл"),
        Stem("Жэнь", "Вода"), Stem("Гуй", "Вода"),
    )

    private val BRANCHES = listOf(
        Branch("Цзы", "Крыса", "Вода"), Branch("Чоу", "Бык", "Земля"),
        Branch("Инь", "Тигр", "Дерево"), Branch("Мао", "Кролик", "Дерево"),
        Branch("Чэнь", "Дракон", "Земля"), Branch("Сы", "Змея", "Огонь"),
        Branch("У", "Лошадь", "Огонь"), Branch("Вэй", "Коза", "Земля"),
        Branch("Шэнь", "Обезьяна", "Металл"), Branch("Ю", "Петух", "Металл"),
        Branch("Сюй", "Собака", "Земля"), Branch("Хай", "Свинья", "Вода"),
    )

    private val TRIGRAMS = mapOf(
        "111" to Trigram("Цянь", "Небо", "сила, ясность и творческий импульс"),
        "000" to Trigram("Кунь", "Земля", "принятие, забота и способность выдержать"),
        "100" to Trigram("Чжэнь", "Гром", "пробуждение, движение и первый толчок"),
        "001" to Trigram("Гэнь", "Гора", "остановка, границы и внутренняя опора"),
        "010" to Trigram("Кань", "Вода", "глубина, риск и умение проходить трудное"),
        "101" to Trigram("Ли", "Огонь", "ясность, страсть и внимание к главному"),
        "011" to Trigram("Сюнь", "Ветер", "мягкое проникновение, перемены и контакт"),
        "110" to Trigram("Дуй", "Озеро", "радость, обмен и открытость"),
    )

    private val SPHERES = listOf(
        Sphere("Единство", "ощущение целостности и связи с миром"),
        Sphere("Творение", "способность создавать и проявляться"),
        Sphere("Воля", "умение выбирать и держать направление"),
        Sphere("Любовь", "тепло, близость и принятие"),
        Sphere("Слово", "голос, правда и умение говорить"),
        Sphere("Память", "род, прошлое и опора на опыт"),
        Sphere("Путь", "движение, развитие и горизонт"),
        Sphere("Свет", "ясность, надежда и вдохновение"),
        Sphere("Тень", "то, что просит внимания и честности"),
        Sphere("Дар", "талант, ресурс и щедрость"),
        Sphere("Дом", "безопасность, тело и место силы"),
    )

    private val CITIES = listOf(
        City("Москва", "Россия"), City("Санкт-Петербург", "Россия"),
        City("Казань", "Россия"), City("Тбилиси", "Грузия"),
        City("Стамбул", "Турция"), City("Дубай", "ОАЭ"),
        City("Лиссабон", "Португалия"), City("Нью-Йорк", "США"),
    )

    private val DREAM_SYMBOLS = listOf(
        DreamSymbol(listOf("вод", "река", "море", "океан", "дожд", "купа"), "Вода", "чувства, очищение и глубина подсознания. Важно, какой была вода: спокойной или тревожной."),
        DreamSymbol(listOf("полёт", "полет", "лета", "пари", "крыл"), "Полёт", "стремление к свободе и взгляду сверху. Сон подсказывает, что вы готовы выйти за привычные рамки."),
        DreamSymbol(listOf("зме"), "Змея", "мудрость, обновление и скрытая сила. Иногда знак перемен, которые уже начались внутри."),
        DreamSymbol(listOf("дом", "квартир", "комнат", "стро"), "Дом", "ваше внутреннее пространство и чувство безопасности. Обратите внимание на состояние дома во сне."),
        DreamSymbol(listOf("смерт", "погреб", "гроб", "умир"), "Смерть", "не буквальный знак, а завершение этапа. Что-то освобождает место для нового."),
        DreamSymbol(listOf("погон", "преслед", "убега", "бежат"), "Погоня", "напряжение и то, от чего вы стараетесь уйти. Полезно назвать страх по имени."),
        DreamSymbol(listOf("зуб", "паден", "пад"), "Падение", "потеря опоры или страх не справиться. Сон приглашает вернуть себе опору малыми шагами."),
        DreamSymbol(listOf("ребён", "ребен", "младен", "беремен"), "Ребёнок", "новая часть себя, невинность и начало. Дайте этому ростку время."),
        DreamSymbol(listOf("лес", "дерев", "парк"), "Лес", "путь через неизвестное и связь с природной частью себя. Ищите тропу, а не идеальную карту."),
        DreamSymbol(listOf("огон", "пожар", "план"), "Огонь", "страсть, гнев или очищение. Если огонь согревает — энергия на подъёме."),
        DreamSymbol(listOf("кот", "кошк"), "Кот", "независимость, чувственность и право на своё пространство."),
        DreamSymbol(listOf("собак", "щен"), "Собака", "верность, дружба и защита. Возможно, рядом есть тот, на кого можно опереться."),
        DreamSymbol(listOf("свадь", "брак", "венчан"), "Свадьба", "союз частей себя или важное обещание. Не всегда про отношения с другим человеком."),
        DreamSymbol(listOf("деньг", "золот", "монет", "богат"), "Деньги", "ценность, ресурс и чувство защищённости. Сон может говорить о самооценке."),
        DreamSymbol(listOf("двер", "вход", "выйти"), "Дверь", "возможность и порог выбора. Закрытая дверь не всегда отказ — иногда она про время."),
        DreamSymbol(listOf("зеркал", "отраж"), "Зеркало", "встреча с собой и вопрос о том, насколько вы себя узнаёте."),
        DreamSymbol(listOf("лун", "ноч"), "Луна", "интуиция, скрытые чувства и то, что ещё не стало ясным."),
        DreamSymbol(listOf("солнц", "свет", "рассвет"), "Солнце", "ясность, жизненная сила и признание. Хороший знак для нового начинания."),
        DreamSymbol(listOf("дорог", "шоссе", "путь"), "Дорога", "направление и выбор. Важно, куда вы шли и с кем."),
        DreamSymbol(listOf("машин", "авто", "поезд"), "Транспорт", "скорость и управление собственной жизнью. Кто был за рулём?"),
        DreamSymbol(listOf("птиц", "перь"), "Птицы", "вести, свобода и связь с небом. Прислушайтесь к первому чувству."),
        DreamSymbol(listOf("кров", "ран", "болит"), "Кровь", "жизненная сила и уязвимость. Сон может указывать на усталость."),
    )

    private val FATE_LINES = listOf(
        "Тише идите — и услышите ответ, который уже звучит внутри.",
        "Не всякая дверь закрыта. Иногда её просто нужно открыть иначе.",
        "То, что вы считаете задержкой, может быть защитой.",
        "Сначала выберите себя, потом выбирайте путь.",
        "Слово, сказанное вовремя, меняет больше, чем сто планов.",
        "Не просите у других разрешения на свою жизнь.",
        "Вернитесь к тому, что радовало вас до чужих ожиданий.",
        "Деньги любят ясность: назовите цифру и посмотрите ей в глаза.",
        "Прощение не оправдывает прошлое. Оно освобождает будущее.",
        "Там, где вы торопитесь, обычно спрятан страх.",
        "Рядом с вами есть человек, которого стоит слышать внимательнее.",
        "Маленький шаг сегодня сильнее большого обещания на завтра.",
        "Не бойтесь начать с нуля: у вас есть опыт, которого не было раньше.",
        "Вопрос не в том, что вы теряете, а в том, что освобождаете.",
        "Откажитесь от роли удобного человека. Вас любят и без неё.",
        "Ответ придёт через тело: заметьте, где становится легче.",
        "Не все сны про будущее. Некоторые возвращают вас к себе.",
        "Позвольте себе не знать. Ясность не обязана приходить сразу.",
        "Ваша сила не в скорости, а в умении не сворачивать.",
        "Скажите «нет» тому, что давно просит завершения.",
        "Дом начинается не с адреса, а с ощущения безопасности.",
        "Вам не нужно побеждать в каждом споре, чтобы быть правым.",
        "То, что кажется слабостью, иногда становится точкой роста.",
        "Посмотрите на ситуацию глазами человека, которого уважаете.",
        "Хороший вопрос важнее быстрого ответа.",
        "Верните долг — не только денежный, но и эмоциональный.",
        "Не сравнивайте свой первый шаг с чужой серединой пути.",
        "Красота рядом: позвольте себе заметить её сегодня.",
        "Ваш следующий уровень начинается с честного разговора.",
        "Всё, что вы ищете, уже есть в вас — но это нужно прожить.",
    )

    private val PAST_LIVES = listOf(
        PastLife("Древний Египет", "хранитель знаний", "учились доверять внутреннему знанию и не прятать его от мира.", "говорить о том, что вы видите, даже если вас не сразу понимают."),
        PastLife("Античная Греция", "философ и ремесленник", "искали истину и учились ценить простое.", "соединить мысль и дело: не только объяснять, но и создавать."),
        PastLife("Римская империя", "строитель и стратег", "понимали цену порядка и границ.", "строить не из страха, а из желания оставить после себя тепло."),
        PastLife("Средневековье", "врачеватель и монах", "учились состраданию и тишине.", "не спасать всех ценой себя; забота начинается с собственного тела."),
        PastLife("Ренессанс", "художник и изобретатель", "искали гармонию между красотой и смыслом.", "разрешить себе творить без разрешения и без идеального результата."),
        PastLife("Эпоха мореплавания", "исследователь", "учились доверять горизонту и команде.", "не бояться нового маршрута, даже если карта неполная."),
        PastLife("Древний Восток", "мастер и учитель", "постигали терпение и силу ритуала.", "делиться знанием так, чтобы другой становился сильнее, а не зависел."),
        PastLife("Начало XX века", "артист и врач", "соединяли чувство и служение.", "не прятать талант за скромностью; ваш голос нужен."),
        PastLife("Середина XX века", "дипломат и учитель", "учились говорить между разными языками.", "мирить не только других, но и собственные противоречия."),
        PastLife("Недавнее прошлое", "хранитель семьи", "собирали опыт рода и учились отпускать.", "завершить повторяющийся сценарий, выбрав новый ответ."),
    )

    private val NAME_MEANINGS = mapOf(
        "александр" to "защитник людей, человек с сильным чувством справедливости.",
        "алексей" to "спокойная сила, умение брать ответственность на себя.",
        "андрей" to "смелость и стремление к новому, внутренний стержень.",
        "анна" to "мягкость с характером, умение заботиться и не терять себя.",
        "мария" to "глубина, верность и способность давать тепло.",
        "елена" to "свет, обаяние и умение видеть красоту в деталях.",
        "ольга" to "воля, самостоятельность и острый ум.",
        "татьяна" to "устойчивость, организаторский дар и верность.",
        "наталья" to "родная душа, эмоциональность и умение объединять.",
        "ирина" to "спокойствие, вкус и способность создавать уют.",
        "дмитрий" to "практичность, воля и умение доводить дело до конца.",
        "сергей" to "наблюдательность, ответственность и чувство долга.",
        "михаил" to "масштаб, независимость и желание дойти до сути.",
        "екатерина" to "ясность, достоинство и творческая сила.",
        "юлия" to "лёгкость, эмоциональность и умение вдохновлять.",
        "павел" to "надёжность, спокойствие и основательность.",
        "иван" to "щедрость, открытость и внутренняя простота.",
        "софия" to "мудрость, глубина и умение слышать других.",
        "виктория" to "победа, харизма и способность вести за собой.",
        "максим" to "настойчивость, сила и умение держать удар.",
        "дарья" to "энергия, смелость и вкус к жизни.",
        "артём" to "целеустремлённость, ясность и практичность.",
        "полина" to "нежность, артистизм и умение мечтать.",
        "кирилл" to "лидерство, логика и желание создавать новое.",
    )

    private val NAME_LIST = listOf(
        "Александр" to "male", "Алексей" to "male", "Андрей" to "male",
        "Артём" to "male", "Дмитрий" to "male", "Иван" to "male",
        "Кирилл" to "male", "Максим" to "male", "Михаил" to "male",
        "Павел" to "male", "Сергей" to "male", "Тимофей" to "male",
        "Анна" to "female", "Алиса" to "female", "Виктория" to "female",
        "Дарья" to "female", "Екатерина" to "female", "Елена" to "female",
        "Ирина" to "female", "Мария" to "female", "Наталья" to "female",
        "Полина" to "female", "София" to "female", "Юлия" to "female",
    )

    private val LETTER_VALUES = mapOf(
        'А' to 1, 'Б' to 2, 'В' to 3, 'Г' to 4, 'Д' to 5, 'Е' to 6, 'Ё' to 7,
        'Ж' to 8, 'З' to 9, 'И' to 1, 'Й' to 2, 'К' to 3, 'Л' to 4, 'М' to 5,
        'Н' to 6, 'О' to 7, 'П' to 8, 'Р' to 9, 'С' to 1, 'Т' to 2, 'У' to 3,
        'Ф' to 4, 'Х' to 5, 'Ц' to 6, 'Ч' to 7, 'Ш' to 8, 'Щ' to 9, 'Ъ' to 1,
        'Ы' to 2, 'Ь' to 3, 'Э' to 4, 'Ю' to 5, 'Я' to 6,
    )

    private val ENERGY_TYPES = listOf(
        EnergyType("Источник", "вы заряжаете пространство и людей, но легко отдаёте больше, чем остаётся вам.", "щедрость, тепло, способность вдохновить", "выгорание и забытые собственные желания"),
        EnergyType("Проводник", "вы чувствуете фон и умеете соединять людей, идеи и возможности.", "эмпатия, вкус, умение договариваться", "потеря себя в чужих ожиданиях"),
        EnergyType("Хранитель", "вы создаёте устойчивость и безопасность, на вас можно опереться.", "надёжность, забота, верность", "сопротивление переменам и контроль"),
        EnergyType("Преобразователь", "вы меняете реальность вокруг, проходя через напряжение и обновление.", "воля, глубина, способность к трансформации", "жизнь на пределе и резкие развороты"),
    )

    private val HD_TYPES = listOf(
        HdType("Генератор", "Ждать отклика", "ваша сила включается там, где есть живой отклик, а не должно."),
        HdType("Проектор", "Ждать приглашения", "вы видите систему целиком, но ваша мудрость раскрывается в приглашении."),
        HdType("Манифестор", "Информировать", "вы начинаете импульс и меняете пространство, когда предупреждаете других."),
        HdType("Рефлектор", "Проживать лунный цикл", "вы зеркалите среду и лучше видите себя через смену состояний."),
    )

    private val PYTH_MEANINGS = mapOf(
        1 to ("Характер" to listOf("мягкость и зависимость от мнений", "уверенность и чувство меры", "сильная воля и желание вести", "лидерство и ответственность за других")),
        2 to ("Энергия" to listOf("берегите силы и избегайте перегрузки", "достаточный запас энергии", "высокая выносливость", "мощная энергия, важно направлять её в дело")),
        3 to ("Интерес" to listOf("нужен внешний импульс", "есть устойчивый интерес", "яркая творческая жилка", "многогранность и тяга к новому")),
        4 to ("Здоровье" to listOf("нужен режим и восстановление", "устойчивость и хороший тонус", "выносливость и сила", "крепкое тело, но не забывайте про отдых")),
        5 to ("Логика" to listOf("доверяйте чувству и телу", "баланс логики и интуиции", "ясный ум и аналитика", "сильный аналитик, проверяйте выводы")),
        6 to ("Мастерство" to listOf("ищите дело по душе", "есть ремесло и трудолюбие", "мастерство и внимание к качеству", "талант создавать и доводить до конца")),
        7 to ("Удача" to listOf("создавайте поток сами", "удача приходит через действие", "сильный поток и поддержка", "везение там, где вы рискуете с умом")),
        8 to ("Долг" to listOf("свобода важнее обязательств", "чувство ответственности", "надёжность и опора", "вы держите слово и ведёте других")),
        9 to ("Память" to listOf("нужно обучение и опыт", "есть глубина и память", "сильная интуитивная память", "мудрость и связь с прошлым")),
    )

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
    private val ISO_DATE = Regex("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$")
    private val LOCAL_DATE = Regex("^(\\d{1,2})[./-](\\d{1,2})[./-](\\d{4})$")

    // ── Публичный вход ─────────────────────────────────────

    fun calculate(practiceId: String, modeId: String, values: Map<String, String>): PracticeResult? {
        when (practiceId) {
            "transits" -> return calculateTransits()
            "iching" -> return throwHexagram()
            "dreams" -> return calculateDreams(values)
            "book-of-fate" -> return calculateBook(values)
            "names" -> return calculateNames(values, modeId)
        }
        val childMode = (practiceId == "matrix" || practiceId == "pythagoras") && modeId == "child"
        val birth = parseDate(if (childMode) values["childDate"] else values["birthDate"]) ?: return null
        return when (practiceId) {
            "matrix" -> calculateMatrix(birth, modeId, values)
            "pythagoras" -> calculatePythagoras(birth, modeId, values)
            "fate-graph" -> calculateFate(birth, modeId, values)
            "energy" -> calculateEnergy(birth, modeId, values)
            "destiny-number" -> calculateDestiny(birth, modeId, values)
            "natal" -> calculateNatal(birth, modeId, values)
            "bazi" -> calculateBazi(birth, values)
            "human-design" -> calculateHumanDesign(birth, values)
            "hologenetics" -> calculateHologenetics(birth)
            "astrocart" -> calculateAstrocart(birth)
            "three-archetypes" -> calculateArchetypes(birth)
            "past-life" -> calculatePastLife(birth)
            else -> null
        }
    }

    fun parseDate(value: String?): LocalDate? {
        val text = value?.trim().orEmpty()
        if (text.isEmpty()) return null

        ISO_DATE.find(text)?.let { match ->
            return safeDate(
                year = match.groupValues[1].toInt(),
                month = match.groupValues[2].toInt(),
                day = match.groupValues[3].toInt(),
            )
        }
        LOCAL_DATE.find(text)?.let { match ->
            return safeDate(
                year = match.groupValues[3].toInt(),
                month = match.groupValues[2].toInt(),
                day = match.groupValues[1].toInt(),
            )
        }

        val digits = text.filter { it.isDigit() }
        if (digits.length != 8) return null
        return safeDate(
            year = digits.substring(4).toInt(),
            month = digits.substring(2, 4).toInt(),
            day = digits.substring(0, 2).toInt(),
        )
    }

    private fun safeDate(year: Int, month: Int, day: Int): LocalDate? =
        runCatching { LocalDate.of(year, month, day) }
            .getOrNull()
            ?.takeIf { it.year in 1900..2100 }

    fun formatDate(date: LocalDate): String = date.format(DATE_FORMAT)

    // ── Вспомогательные функции ────────────────────────────

    private fun digitsOf(number: Int): List<Int> =
        number.toString().filter { it.isDigit() }.map { it.digitToInt() }

    private fun digitSum(number: Int): Int = digitsOf(number).sum()

    private fun mod(value: Int, modulus: Int): Int {
        val result = value % modulus
        return if (result < 0) result + modulus else result
    }

    private fun reduce9(number: Int): Int {
        var value = abs(number)
        while (value > 9) value = digitSum(value)
        return value
    }

    private fun reduce22(number: Int): Int {
        var value = abs(number)
        if (value == 0) return 22
        while (value > 22) value = digitSum(value)
        return value
    }

    private fun hashString(text: String): Int {
        var hash = 2166136261L
        for (char in text) {
            hash = hash xor char.code.toLong()
            hash = (hash * 16777619L) and 0xFFFFFFFFL
        }
        return (hash and 0x7FFFFFFF).toInt()
    }

    private fun normalize(text: String): String = text.lowercase().replace('ё', 'е')

    private fun arcana(number: Int): Arcana = ARCANA[mod(number - 1, 22)]

    private fun arcanaValue(number: Int): String = "$number. ${arcana(number).name}"

    private fun arcanaText(number: Int): String {
        val item = arcana(number)
        return "${item.light}. Тень: ${item.shadow}."
    }

    private fun block(label: String, value: String, text: String = ""): PracticeBlock =
        PracticeBlock(label = label, value = value, text = text)

    private fun matrixOf(date: LocalDate): Matrix {
        val left = reduce22(date.dayOfMonth)
        val top = reduce22(date.monthValue)
        val right = reduce22(digitSum(date.year))
        val center = reduce22(left + top + right)
        return Matrix(left, top, right, center)
    }

    private data class Matrix(val left: Int, val top: Int, val right: Int, val center: Int)

    private fun nameNumber(name: String): Int {
        val upper = name.uppercase(Locale.forLanguageTag("ru")).filter { it in 'А'..'Я' || it == 'Ё' }
        val sum = upper.sumOf { LETTER_VALUES[it] ?: 0 }
        return reduce9(if (sum == 0) 1 else sum)
    }

    private fun signFor(date: LocalDate): Sign {
        val value = date.monthValue * 100 + date.dayOfMonth
        return SIGNS.firstOrNull { sign ->
            if (sign.from <= sign.to) value in sign.from..sign.to
            else value >= sign.from || value <= sign.to
        } ?: SIGNS.first()
    }

    private fun moonSignFor(date: LocalDate): Sign {
        val days = ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 6), date)
        var angle = (days * 13.176) % 360.0
        if (angle < 0) angle += 360.0
        return SIGNS[floor(angle / 30.0).toInt() % 12]
    }

    private fun risingSignFor(date: LocalDate, time: String?): Sign {
        val sunIndex = SIGNS.indexOfFirst { it.name == signFor(date).name }
        var hour = 12.0
        if (!time.isNullOrBlank()) {
            val match = Regex("^(\\d{1,2}):(\\d{2})").find(time)
            if (match != null) {
                hour = match.groupValues[1].toDouble() + match.groupValues[2].toDouble() / 60.0
            }
        }
        val index = mod(sunIndex + floor(mod((hour - 6).toInt(), 24) / 2.0).toInt(), 12)
        return SIGNS[index]
    }

    private fun dominantElement(elements: List<String>): String =
        elements.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "air"

    private fun julianDay(date: LocalDate): Int {
        val a = (14 - date.monthValue) / 12
        val y = date.year + 4800 - a
        val m = date.monthValue + 12 * a - 3
        return date.dayOfMonth + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
    }

    private fun numberMeaning(number: Int): NumberMeaning =
        NUMBER_MEANINGS[number] ?: NUMBER_MEANINGS.getValue(1)

    // ── Матрица судьбы ─────────────────────────────────────

    private fun calculateMatrix(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult? {
        val matrix = matrixOf(date)
        when (mode) {
            "year" -> {
                val personal = reduce9(date.dayOfMonth + date.monthValue + LocalDate.now().year)
                val meaning = numberMeaning(personal)
                return PracticeResult(
                    title = "Личный год",
                    subtitle = "Цикл от дня рождения до следующего дня рождения",
                    blocks = listOf(
                        block("Число года", personal.toString(), meaning.text),
                        block("Тема", meaning.title, meaning.advice),
                        block("Опорный аркан", arcanaValue(matrix.center), arcanaText(matrix.center)),
                    ),
                    note = "Личный год считается от даты рождения, а не от 1 января.",
                )
            }
            "finance" -> {
                val finance = reduce22(matrix.right + matrix.center)
                return PracticeResult(
                    title = "Финансовый поток",
                    subtitle = "Ресурс, точка роста и обращение с деньгами",
                    blocks = listOf(
                        block("Аркан денег", arcanaValue(finance), arcanaText(finance)),
                        block("Что привлекает доход", arcanaValue(matrix.right), arcanaText(matrix.right)),
                        block("Что мешает потоку", arcanaValue(matrix.top), arcanaText(matrix.top)),
                        block("Следующий шаг", "Ясность", "Назовите конкретную сумму и срок. Деньги любят не мечту, а понятное действие."),
                    ),
                    note = "Финансовый аркан — символический ориентир, а не инвестиционная рекомендация.",
                )
            }
            "child" -> {
                val child = parseDate(values["childDate"]) ?: return null
                val childMatrix = matrixOf(child)
                return PracticeResult(
                    title = "Матрица ребёнка",
                    subtitle = formatDate(child),
                    blocks = listOf(
                        block("Характер", arcanaValue(childMatrix.left), arcanaText(childMatrix.left)),
                        block("Энергия", arcanaValue(childMatrix.top), arcanaText(childMatrix.top)),
                        block("Опора", arcanaValue(childMatrix.right), arcanaText(childMatrix.right)),
                        block("Потенциал", arcanaValue(childMatrix.center), arcanaText(childMatrix.center)),
                    ),
                    note = "Это язык образов для разговора о ребёнке, а не ярлык и не диагноз.",
                )
            }
            "couple" -> {
                val partner = parseDate(values["partnerDate"]) ?: return null
                val partnerMatrix = matrixOf(partner)
                val union = reduce22(matrix.center + partnerMatrix.center)
                return PracticeResult(
                    title = "Матрица пары",
                    subtitle = "Вы и партнёр",
                    blocks = listOf(
                        block("Ваш центр", arcanaValue(matrix.center), arcanaText(matrix.center)),
                        block("Центр партнёра", arcanaValue(partnerMatrix.center), arcanaText(partnerMatrix.center)),
                        block("Аркан союза", arcanaValue(union), arcanaText(union)),
                        block("Совет", "Не сливаться", "Сохраняйте личные границы и общее пространство. Союз силён там, где есть два целых человека."),
                    ),
                    note = "Совместимость — не приговор, а описание динамики.",
                )
            }
        }
        val love = reduce22(matrix.top + matrix.right)
        return PracticeResult(
            title = "Матрица судьбы",
            subtitle = formatDate(date),
            blocks = listOf(
                block("Характер", arcanaValue(matrix.left), arcanaText(matrix.left)),
                block("Энергия", arcanaValue(matrix.top), arcanaText(matrix.top)),
                block("Деньги", arcanaValue(matrix.right), arcanaText(matrix.right)),
                block("Предназначение", arcanaValue(matrix.center), arcanaText(matrix.center)),
                block("Отношения", arcanaValue(love), arcanaText(love)),
                block("Формула", "${matrix.left} · ${matrix.top} · ${matrix.right} · ${matrix.center}", "Четыре опоры, из которых складывается ваш жизненный рисунок."),
            ),
            note = "Матрица судьбы — символическая система самопознания. Она не предсказывает события буквально.",
        )
    }

    // ── Квадрат Пифагора ───────────────────────────────────

    private fun pythagorasCore(date: LocalDate): PythagorasCore {
        val day = date.dayOfMonth.toString()
        val month = date.monthValue.toString()
        val year = date.year.toString()
        val base = day + month + year
        val sum1 = base.sumOf { it.digitToInt() }
        val sum2 = reduce9(sum1)
        val first = day.first().digitToInt()
        val sum3 = abs(sum1 - 2 * first)
        val sum4 = reduce9(sum3)
        val all = (base + sum1 + sum2 + sum3 + sum4).filter { it in '1'..'9' }
        val counts = (1..9).associateWith { digit -> all.count { it == digit.digitToChar() } }
        val cells = counts.map { (digit, count) -> PythagorasCell(digit, count) }
        return PythagorasCore(counts, cells)
    }

    private data class PythagorasCore(
        val counts: Map<Int, Int>,
        val cells: List<PythagorasCell>,
    )

    private fun pythagorasSummary(counts: Map<Int, Int>): String {
        val strong = counts.filter { it.value >= 2 }.keys.mapNotNull { PYTH_MEANINGS[it]?.first }
        if (strong.isEmpty()) return "ровный профиль: силы распределены без крайностей."
        return "ярче всего: ${strong.take(3).joinToString(", ")}."
    }

    private fun calculatePythagoras(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult? {
        val target = if (mode == "child") parseDate(values["childDate"]) ?: return null else date
        if (mode == "couple") {
            val partner = parseDate(values["partnerDate"]) ?: return null
            val first = pythagorasCore(target)
            val second = pythagorasCore(partner)
            val difference = first.counts.keys.sumOf { key ->
                abs((first.counts[key] ?: 0) - (second.counts[key] ?: 0))
            }
            val score = maxOf(2, 10 - difference)
            return PracticeResult(
                title = "Психоматрица пары",
                subtitle = "Вы и партнёр",
                blocks = listOf(
                    block(
                        "Совместимость",
                        "$score / 10",
                        when {
                            score >= 8 -> "много общего и лёгкое взаимопонимание."
                            score >= 5 -> "есть точки роста: важно говорить о разнице."
                            else -> "сильная разница, которая требует зрелости и границ."
                        },
                    ),
                    block("Ваш профиль", pythagorasSummary(first.counts)),
                    block("Профиль партнёра", pythagorasSummary(second.counts)),
                ),
                chart = PracticeChart.Pythagoras(first.cells),
                note = "Сравнение носит символический характер и не заменяет разговор.",
            )
        }
        val core = pythagorasCore(target)
        val strong = core.counts.filter { it.value >= 2 }.keys.mapNotNull { PYTH_MEANINGS[it]?.first }
        val weak = core.counts.filter { it.value == 0 }.keys.mapNotNull { PYTH_MEANINGS[it]?.first }
        return PracticeResult(
            title = if (mode == "child") "Квадрат ребёнка" else "Квадрат Пифагора",
            subtitle = formatDate(target),
            blocks = listOf(
                block("Сильные стороны", strong.ifEmpty { listOf("баланс без ярких акцентов") }.joinToString(" · "), "Эти качества можно развивать сознательно."),
                block("Точки роста", weak.ifEmpty { listOf("все ячейки заполнены") }.joinToString(" · "), "Здесь полезно действовать мягко и без давления."),
                block("Кратко", pythagorasSummary(core.counts)),
            ),
            chart = PracticeChart.Pythagoras(core.cells),
            note = "В квадрате считается количество цифр в дате после рабочих чисел — это символическая психоматрица.",
        )
    }

    // ── График судьбы ──────────────────────────────────────

    private fun fatePoints(date: LocalDate): List<Int> {
        val year = digitSum(date.year)
        return listOf(
            reduce22(date.dayOfMonth),
            reduce22(date.monthValue),
            reduce22(year),
            reduce22(date.dayOfMonth + date.monthValue),
            reduce22(date.monthValue + year),
            reduce22(date.dayOfMonth + year),
            reduce22(date.dayOfMonth + date.monthValue + year),
        )
    }

    private fun calculateFate(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult? {
        val points = fatePoints(date)
        val partner = if (mode == "couple") parseDate(values["partnerDate"]) ?: return null else null
        val max = points.maxOrNull() ?: 0
        val min = points.minOrNull() ?: 0
        val labels = listOf("Исток", "Детство", "Юность", "Поворот", "Зрелость", "Расцвет", "Итог")
        return PracticeResult(
            title = "График судьбы",
            subtitle = formatDate(date) + if (partner != null) " · пара" else "",
            blocks = listOf(
                block("Пик", "${labels[points.indexOf(max)]} · $max", "Здесь жизненная сила и возможности проявляются ярче."),
                block("Точка отдыха", "${labels[points.indexOf(min)]} · $min", "В этом периоде важно не форсировать события и беречь ресурс."),
                block("Финал", "${labels[6]} · ${points[6]}", "Это не конец, а точка сборки опыта: что вы забираете с собой."),
                block("Совет", "Ритм", "Сверяйтесь с графиком как с картой: он показывает не приговор, а смену темпа."),
            ),
            chart = PracticeChart.Fate(FateSeries(points, labels)),
            note = "График строится по цифрам даты рождения; это символическая периодизация.",
        )
    }

    // ── Энергетический тип ─────────────────────────────────

    private fun calculateEnergy(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult? {
        val center = matrixOf(date).center
        val type = ENERGY_TYPES[mod(center, 4)]
        if (mode == "compatibility") {
            val partner = parseDate(values["partnerDate"]) ?: return null
            val partnerType = ENERGY_TYPES[mod(matrixOf(partner).center, 4)]
            val score = maxOf(2, 10 - abs(typeIndex(type) - typeIndex(partnerType)) * 2)
            return PracticeResult(
                title = "Энергетическая совместимость",
                subtitle = "${type.name} + ${partnerType.name}",
                blocks = listOf(
                    block("Вы", type.name, type.text),
                    block("Партнёр", partnerType.name, partnerType.text),
                    block(
                        "Совместимость",
                        "$score / 10",
                        when {
                            score >= 8 -> "вам легко быть рядом, но важно не сливаться."
                            score >= 5 -> "разные ритмы: помогает честный разговор о границах."
                            else -> "сильное притяжение и такая же сильная разница: нужна зрелость."
                        },
                    ),
                    block("Совет", "Обмен", "Договоритесь, кто и когда восстанавливается. Энергия — общий ресурс, а не соревнование."),
                ),
                note = "Тип определяется по центру матрицы судьбы; это символическая модель.",
            )
        }
        return PracticeResult(
            title = "Энергетический тип",
            subtitle = formatDate(date),
            blocks = listOf(
                block("Ваш тип", type.name, type.text),
                block("Сильная сторона", type.strong, "Опирайтесь на это, когда нужно вернуть себе устойчивость."),
                block("Зона риска", type.risk, "Заметьте, где вы теряете энергию, и верните себе паузу."),
                block("Практика", "5 минут тишины", "Спросите себя: что сейчас даёт мне силу, а что забирает? Ответ — уже начало."),
            ),
            note = "Не делите людей на «доноров» и «вампиров»: энергия зависит от контекста и границ.",
        )
    }

    private fun typeIndex(type: EnergyType): Int = ENERGY_TYPES.indexOf(type)

    // ── Число судьбы ───────────────────────────────────────

    private fun calculateDestiny(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult {
        val number = reduce9(date.dayOfMonth + date.monthValue + date.year)
        val meaning = numberMeaning(number)
        val blocks = mutableListOf(
            block("Число даты", number.toString(), meaning.text),
            block("Тема", meaning.title, meaning.advice),
        )
        val name = values["name"].orEmpty().trim()
        if (mode == "name" && name.isNotEmpty()) {
            val nameValue = nameNumber(name)
            val nameMeaning = numberMeaning(nameValue)
            blocks += block("Число имени", "$nameValue · ${nameMeaning.title}", "${nameMeaning.text} ${nameMeaning.advice}")
        }
        return PracticeResult(
            title = "Число судьбы",
            subtitle = formatDate(date) + if (mode == "name" && name.isNotEmpty()) " · $name" else "",
            blocks = blocks,
            note = "Число судьбы — символический ориентир, а не жёсткая программа.",
        )
    }

    // ── Натальная карта ────────────────────────────────────

    private fun calculateNatal(date: LocalDate, mode: String, values: Map<String, String>): PracticeResult? {
        if (mode == "solar") {
            val year = LocalDate.now().year
            val solarNumber = reduce9(date.dayOfMonth + date.monthValue + year)
            val meaning = numberMeaning(solarNumber)
            val sun = signFor(date)
            return PracticeResult(
                title = "Соляр $year",
                subtitle = "Год от дня рождения",
                blocks = listOf(
                    block("Солнце", sun.name, sun.text),
                    block("Число года", solarNumber.toString(), meaning.text),
                    block("Тема года", meaning.title, meaning.advice),
                ),
                note = "Соляр — символический срез года, а не точный прогноз событий.",
            )
        }
        if (mode == "synastry") {
            val partner = parseDate(values["partnerDate"]) ?: return null
            val first = signFor(date)
            val second = signFor(partner)
            val score = ELEMENT_COMPAT[first.element]?.get(second.element) ?: 5
            return PracticeResult(
                title = "Синастрия",
                subtitle = "${first.name} + ${second.name}",
                blocks = listOf(
                    block("Ваше Солнце", first.name, first.text),
                    block("Солнце партнёра", second.name, second.text),
                    block(
                        "Совместимость",
                        "$score / 10",
                        when {
                            score >= 8 -> "естественное притяжение и общий язык."
                            score >= 5 -> "есть притяжение и зоны, где нужен перевод."
                            else -> "разные языки: учитесь говорить прямо и без обвинений."
                        },
                    ),
                    block("Совет", "Перевод", "Не требуйте, чтобы партнёр чувствовал как вы. Спрашивайте, что для него значит забота."),
                ),
                note = "Синастрия по Солнцу — упрощённый срез; для точной карты нужны время и место рождения обоих.",
            )
        }
        val time = values["birthTime"].orEmpty()
        val sun = signFor(date)
        val moon = moonSignFor(date)
        val rising = risingSignFor(date, time)
        val dominant = dominantElement(listOf(sun.element, moon.element, rising.element))
        return PracticeResult(
            title = "Натальная карта",
            subtitle = formatDate(date) + if (time.isNotBlank()) " · $time" else "",
            blocks = listOf(
                block("Солнце", sun.name, sun.text),
                block("Луна", moon.name, "внутренняя жизнь: ${moon.text}"),
                block("Асцендент", rising.name, "как вас встречают: ${rising.text}" + if (time.isBlank()) " (время не указано — символ условен)" else ""),
                block("Стихия", ELEMENT_NAMES[dominant] ?: dominant, "ведущая стихия карты. ${ELEMENT_ADVICE[dominant].orEmpty()}"),
                block("Совет", "Соберите три опоры", "Солнце — что вас зажигает, Луна — что успокаивает, асцендент — как вы входите в контакт."),
            ),
            note = "Луна и асцендент рассчитаны приближённо. Точная карта требует эфемерид, времени и координат места рождения.",
        )
    }

    // ── Транзиты ───────────────────────────────────────────

    private fun calculateTransits(): PracticeResult {
        val today = LocalDate.now()
        val sun = signFor(today)
        val moon = moonSignFor(today)
        val lunarDay = (mod((ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 6), today) % 29.530588).toInt(), 30)) + 1
        val dayNumber = reduce9(today.dayOfMonth + today.monthValue + 1 + today.year)
        val meaning = numberMeaning(dayNumber)
        return PracticeResult(
            title = "Энергия дня",
            subtitle = formatDate(today),
            blocks = listOf(
                block("Солнце", sun.name, sun.text),
                block("Луна", moon.name, "фон дня: ${moon.text}"),
                block(
                    "Лунный день",
                    lunarDay.toString(),
                    when {
                        lunarDay <= 7 -> "начало цикла: закладывайте намерение."
                        lunarDay <= 14 -> "рост: действуйте и учитесь."
                        lunarDay <= 22 -> "зрелость: собирайте результат."
                        else -> "завершение: отпускайте лишнее."
                    },
                ),
                block("Совет", ELEMENT_NAMES[sun.element] ?: sun.element, ELEMENT_ADVICE[sun.element].orEmpty()),
                block("Число дня", dayNumber.toString(), meaning.advice),
            ),
            note = "Символический прогноз. Точные транзиты требуют полноценных эфемерид.",
        )
    }

    // ── Бацзы ──────────────────────────────────────────────

    private fun calculateBazi(date: LocalDate, values: Map<String, String>): PracticeResult {
        val time = values["birthTime"].takeUnless { it.isNullOrBlank() } ?: "12:00"
        val hour = time.substringBefore(":").toIntOrNull() ?: 12
        val yearStem = mod(date.year - 4, 10)
        val yearBranch = mod(date.year - 4, 12)
        val monthBranch = mod(date.monthValue, 12)
        val monthStem = mod(yearStem * 2 + date.monthValue + 1, 10)
        val jdn = julianDay(date)
        val dayStem = mod(jdn + 9, 10)
        val dayBranch = mod(jdn + 1, 12)
        val hourBranch = floor(mod(hour + 1, 24) / 2.0).toInt() % 12
        val hourStem = mod(dayStem * 2 + hourBranch, 10)
        val pillars = listOf(
            Triple("Год", STEMS[yearStem], BRANCHES[yearBranch]),
            Triple("Месяц", STEMS[monthStem], BRANCHES[monthBranch]),
            Triple("День", STEMS[dayStem], BRANCHES[dayBranch]),
            Triple("Час", STEMS[hourStem], BRANCHES[hourBranch]),
        )
        val counts = pillars.flatMap { listOf(it.second.element, it.third.element) }.groupingBy { it }.eachCount()
        val dominant = counts.maxByOrNull { it.value }?.key ?: "Земля"
        val missing = listOf("Дерево", "Огонь", "Земля", "Металл", "Вода").filter { it !in counts }
        return PracticeResult(
            title = "Четыре столпа",
            subtitle = "${formatDate(date)} · $time",
            blocks = listOf(
                block("Ведущая стихия", dominant, "она задаёт основной способ действовать и восстанавливаться."),
                block("Чего не хватает", missing.ifEmpty { listOf("все стихии присутствуют") }.joinToString(" · "), "Это не приговор: недостающее можно развивать средой и привычками."),
                block("Опора", "${pillars[2].second.name} ${pillars[2].third.name}", "столп дня часто читают как центр личности."),
            ),
            chart = PracticeChart.Pillars(
                pillars.map { (label, stem, branch) ->
                    PillarInfo(
                        label = label,
                        stem = stem.name,
                        branch = branch.name,
                        element = "${stem.element} · ${branch.animal}",
                    )
                },
            ),
            note = "Упрощённый расчёт по солнечному календарю. Точные столпы требуют перевода в китайский календарь и учёта солнечных терминов.",
        )
    }

    // ── Книга перемен ──────────────────────────────────────

    private fun throwHexagram(): PracticeResult {
        val lines = List(6) {
            var sum = 0
            repeat(3) { sum += if (Random.nextBoolean()) 3 else 2 }
            HexLine(yang = sum == 7 || sum == 9, changing = sum == 6 || sum == 9)
        }
        val lowerBits = lines.take(3).joinToString("") { if (it.yang) "1" else "0" }
        val upperBits = lines.drop(3).joinToString("") { if (it.yang) "1" else "0" }
        val lower = TRIGRAMS.getValue(lowerBits)
        val upper = TRIGRAMS.getValue(upperBits)
        val changing = lines.count { it.changing }
        return PracticeResult(
            title = "Знак перемен",
            subtitle = "${upper.image} над ${lower.image}",
            blocks = listOf(
                block("Верхняя триграмма", upper.name, upper.text),
                block("Нижняя триграмма", lower.name, lower.text),
                block("Образ", "${upper.image} над ${lower.image}", "Внутри — ${lower.text}. Снаружи — ${upper.text}."),
                block(
                    "Совет",
                    when {
                        changing == 0 -> "Устойчивость"
                        changing == 1 -> "Один поворот"
                        changing <= 3 -> "Несколько уроков"
                        else -> "Большая перемена"
                    },
                    if (changing == 0) "Ситуация стабильна: не форсируйте, наблюдайте." else "Заметьте, что уже меняется. Не цепляйтесь за прежнюю форму.",
                ),
            ),
            chart = PracticeChart.Hexagram(lines),
            note = "Упрощённая практика: знак собран из двух триграмм. Каноническая Книга перемен использует 64 гексаграммы.",
        )
    }

    // ── Дизайн человека ────────────────────────────────────

    private fun calculateHumanDesign(date: LocalDate, values: Map<String, String>): PracticeResult {
        val seed = hashString("${date.year}-${date.monthValue}-${date.dayOfMonth}-hd")
        val type = HD_TYPES[seed % 4]
        val authority = when (type.name) {
            "Генератор" -> "Сакральный"
            "Проектор" -> "Селезёночный"
            "Манифестор" -> "Эмоциональный"
            else -> "Лунный"
        }
        val first = (date.dayOfMonth % 6) + 1
        val second = (date.monthValue % 6) + 1
        val centers = listOf("Голова", "Аджна", "Горло", "G-центр", "Сердце", "Сакрал", "Селезёнка", "Солнечное сплетение", "Корень")
        val defined = centers.filterIndexed { index, _ -> ((seed shr index) and 1) == 1 }
        val open = centers.filterIndexed { index, _ -> ((seed shr index) and 1) == 0 }
        val time = values["birthTime"].orEmpty()
        return PracticeResult(
            title = "Дизайн человека",
            subtitle = formatDate(date) + if (time.isNotBlank()) " · $time" else "",
            blocks = listOf(
                block("Тип", type.name, type.text),
                block("Стратегия", type.strategy, "попробуйте принимать решения через эту стратегию."),
                block("Авторитет", authority, "внутренний способ проверять правильность решения."),
                block("Профиль", "$first/$second", "сочетание ролей: как вы учитесь и как входите в контакт."),
                block("Определённые центры", defined.ifEmpty { listOf("нет ярко выраженных") }.joinToString(" · "), "Здесь энергия работает стабильно."),
                block("Открытые центры", open.ifEmpty { listOf("нет") }.take(4).joinToString(" · "), "Здесь вы чувствительны к среде и можете набираться опыта."),
            ),
            note = "Символический бодиграф. Настоящий Дизайн человека требует точных планетарных расчётов.",
        )
    }

    // ── Хологенетика ───────────────────────────────────────

    private fun calculateHologenetics(date: LocalDate): PracticeResult {
        val seed = date.dayOfMonth + date.monthValue + date.year
        val items = SPHERES.mapIndexed { index, sphere ->
            SphereScore(sphere.name, reduce9(seed + index * 7))
        }
        val top = items.sortedByDescending { it.value }.take(4)
        val blocks = top.map { item ->
            block(item.name, item.value.toString(), SPHERES.first { it.name == item.name }.text)
        } + block("Совет", "Выберите одну сферу", "Не пытайтесь развивать всё сразу. Одна сфера, один маленький шаг на этой неделе.")
        return PracticeResult(
            title = "Сферы хологенетики",
            subtitle = formatDate(date),
            blocks = blocks,
            chart = PracticeChart.Spheres(items),
            note = "Хологенетика — авторская система; здесь используется символическая интерпретация, а не оригинальная методология.",
        )
    }

    // ── Астрокарта ─────────────────────────────────────────

    private fun calculateAstrocart(date: LocalDate): PracticeResult {
        val seed = date.dayOfMonth + date.monthValue + date.year
        val items = CITIES.map { city ->
            CityScore(city.name, city.country, reduce9(seed + hashString(city.name) % 97))
        }.sortedByDescending { it.score }.take(4)
        val first = items[0]
        val second = items[1]
        val third = items[2]
        return PracticeResult(
            title = "Города силы",
            subtitle = formatDate(date),
            blocks = listOf(
                block("Главная точка", first.name, "${first.country}. ${if (first.score >= 8) "Сильный фон для вдохновения и нового." else "Мягкая поддержка: хорошо для восстановления."}"),
                block("Второй город", second.name, "${second.country}. ${if (second.score >= 7) "Полезен для обучения и контактов." else "Нейтральный фон: важнее ваши намерения."}"),
                block("Третий город", third.name, "${third.country}. ${if (third.score >= 6) "Хорош для короткой перезагрузки." else "Не ждите чуда: сила места зависит от действия."}"),
                block("Совет", "Проверьте телом", "Представьте себя в городе на 3 дня. Легче ли дышится? Есть ли желание гулять и делать?"),
            ),
            chart = PracticeChart.Cities(items),
            note = "Это символическая карта мест, а не астрологическая астрокартография.",
        )
    }

    // ── Архетипы ───────────────────────────────────────────

    private fun calculateArchetypes(date: LocalDate): PracticeResult {
        val first = reduce22(date.dayOfMonth)
        val second = reduce22(date.monthValue)
        val third = reduce22(digitSum(date.year))
        return PracticeResult(
            title = "Три архетипа",
            subtitle = formatDate(date),
            blocks = listOf(
                block("Суть", arcanaValue(first), arcanaText(first)),
                block("Сердце", arcanaValue(second), arcanaText(second)),
                block("Путь", arcanaValue(third), arcanaText(third)),
                block("Формула", "${arcana(first).name} · ${arcana(second).name} · ${arcana(third).name}", "Сочетание этих образов описывает вашу внутреннюю команду."),
            ),
            note = "Архетипы — язык образов. Они не определяют вас полностью.",
        )
    }

    // ── Сны ────────────────────────────────────────────────

    private fun calculateDreams(values: Map<String, String>): PracticeResult {
        val text = normalize(values["dream"].orEmpty())
        val matches = DREAM_SYMBOLS.filter { symbol -> symbol.keys.any { text.contains(it) } }
        val selected = if (matches.isNotEmpty()) {
            matches.take(4)
        } else {
            val seed = hashString(text)
            listOf(
                DREAM_SYMBOLS[seed % DREAM_SYMBOLS.size],
                DREAM_SYMBOLS[(seed + 7) % DREAM_SYMBOLS.size],
                DREAM_SYMBOLS[(seed + 13) % DREAM_SYMBOLS.size],
            )
        }
        val blocks = selected.map { block(it.title, "образ", it.text) } +
            block("Совет", "Запишите чувство", "Сон важнее расшифровывать через эмоцию, а не через буквальное событие. Что вы почувствовали при пробуждении?")
        return PracticeResult(
            title = "Символы сна",
            subtitle = if (matches.isNotEmpty()) "Найдено ${matches.size} знакомых образов" else "Прямых совпадений нет — вот три возможных образа",
            blocks = blocks,
            note = "Толкование снов носит символический и развлекательный характер.",
        )
    }

    // ── Книга судеб ────────────────────────────────────────

    private fun calculateBook(values: Map<String, String>): PracticeResult {
        val question = values["question"].orEmpty().trim()
        val seed = hashString(question.ifBlank { "страница" })
        val line = FATE_LINES[seed % FATE_LINES.size]
        val page = (seed % 999) + 1
        return PracticeResult(
            title = "Страница $page",
            subtitle = "Вопрос: «$question»",
            blocks = listOf(
                block("Строка книги", line, "Прочитайте её медленно и заметьте первое чувство."),
                block("Совет", "Один шаг", "Не ищите весь маршрут. Сделайте одно действие, которое приближает к ответу."),
            ),
            note = "Книга судеб — символическая практика, а не предсказание.",
        )
    }

    // ── Прошлая жизнь ──────────────────────────────────────

    private fun calculatePastLife(date: LocalDate): PracticeResult {
        val seed = hashString("${date.year}-${date.monthValue}-${date.dayOfMonth}-past")
        val story = PAST_LIVES[seed % PAST_LIVES.size]
        return PracticeResult(
            title = story.era,
            subtitle = "Символическая история души",
            blocks = listOf(
                block("Роль", story.role, "в этой истории вы учились быть собой через эту роль."),
                block("Урок", "Опыт", story.lesson),
                block("Задача", "Сегодня", story.task),
                block("Память", "Что отзывается?", "Заметьте, какая деталь истории кажется знакомой. Это может быть ключом к вашему сегодняшнему выбору."),
            ),
            note = "Прошлая жизнь здесь — метафора для исследования, а не исторический факт.",
        )
    }

    // ── Имена ──────────────────────────────────────────────

    private fun calculateNames(values: Map<String, String>, mode: String): PracticeResult? {
        val name = values["name"].orEmpty().trim()
        if (mode == "selection") {
            val birth = parseDate(values["birthDate"]) ?: return null
            val destiny = reduce9(birth.dayOfMonth + birth.monthValue + birth.year)
            val gender = values["gender"].orEmpty().ifBlank { "female" }
            val suggestions = NAME_LIST
                .filter { it.second == gender }
                .map { it.first to nameNumber(it.first) }
                .sortedBy { abs(it.second - destiny) }
                .take(5)
            val blocks = suggestions.map { (suggestion, number) ->
                block(
                    suggestion,
                    number.toString(),
                    NAME_MEANINGS[suggestion.lowercase(Locale.forLanguageTag("ru"))] ?: numberMeaning(number).text,
                )
            } + block("Совет", "Слушайте звучание", "Имя должно нравиться вам вслух. Число — только один из ориентиров.")
            return PracticeResult(
                title = "Подбор имени",
                subtitle = "Ориентир — число судьбы $destiny",
                blocks = blocks,
                note = "Подбор имени — символическая практика; окончательный выбор всегда за вами.",
            )
        }
        val firstNumber = nameNumber(name)
        val meaning = NAME_MEANINGS[name.lowercase(Locale.forLanguageTag("ru"))]
            ?: "${numberMeaning(firstNumber).title}: ${numberMeaning(firstNumber).text}"
        if (mode == "compatibility") {
            val partnerName = values["partnerName"].orEmpty().trim()
            if (partnerName.isBlank()) return null
            val secondNumber = nameNumber(partnerName)
            val score = maxOf(2, 10 - abs(firstNumber - secondNumber))
            return PracticeResult(
                title = "Совместимость имён",
                subtitle = "$name + $partnerName",
                blocks = listOf(
                    block(name, firstNumber.toString(), meaning),
                    block(partnerName, secondNumber.toString(), NAME_MEANINGS[partnerName.lowercase(Locale.forLanguageTag("ru"))] ?: numberMeaning(secondNumber).text),
                    block(
                        "Совместимость",
                        "$score / 10",
                        when {
                            score >= 8 -> "лёгкое взаимопонимание и общий ритм."
                            score >= 5 -> "есть разница, которая делает союз живым."
                            else -> "разные языки: важно говорить прямо и не додумывать."
                        },
                    ),
                    block("Совет", "Слушайте имя", "Как звучит имя партнёра, когда вы его произносите? В этом звуке больше правды, чем в числе."),
                ),
                note = "Совместимость имён — символическая игра, а не оценка отношений.",
            )
        }
        return PracticeResult(
            title = "Мир имён",
            subtitle = name,
            blocks = listOf(
                block("Число имени", firstNumber.toString(), numberMeaning(firstNumber).text),
                block("Значение", numberMeaning(firstNumber).title, meaning),
                block("Совет", "Произнесите вслух", "Имя — это звук, которым вас зовут. Заметьте, что оно пробуждает в теле."),
            ),
            note = "Значение имени здесь символическое и не является словарной этимологией.",
        )
    }
}