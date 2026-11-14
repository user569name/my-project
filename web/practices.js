(function () {
  "use strict";

  /* ── Данные ─────────────────────────────────────────── */
  const ARCANA = [
    { n: 1, name: "Маг", light: "воля, ресурс и умение начинать", shadow: "манипуляция и страх заявить о себе" },
    { n: 2, name: "Верховная Жрица", light: "интуиция, тишина и внутреннее знание", shadow: "отрыв от реальности и скрытность" },
    { n: 3, name: "Императрица", light: "забота, изобилие и творческая сила", shadow: "гиперопека и застой" },
    { n: 4, name: "Император", light: "опора, границы и порядок", shadow: "жёсткость и контроль" },
    { n: 5, name: "Иерофант", light: "смысл, наставничество и традиция", shadow: "догмы и чужие правила" },
    { n: 6, name: "Влюблённые", light: "выбор, союз и верность ценностям", shadow: "сомнения и разрыв с собой" },
    { n: 7, name: "Колесница", light: "движение, победа и собранность", shadow: "спешка и борьба без цели" },
    { n: 8, name: "Сила", light: "мягкая сила, терпение и самообладание", shadow: "подавленные эмоции" },
    { n: 9, name: "Отшельник", light: "уединение, поиск и мудрость", shadow: "изоляция и бегство от людей" },
    { n: 10, name: "Колесо Фортуны", light: "циклы, поворот и удача", shadow: "ожидание чуда и потери контроля" },
    { n: 11, name: "Справедливость", light: "баланс, правда и последствия", shadow: "осуждение и холодность" },
    { n: 12, name: "Повешенный", light: "пауза, новый ракурс и доверие", shadow: "застревание и роль жертвы" },
    { n: 13, name: "Смерть", light: "завершение, трансформация и обновление", shadow: "страх перемен и цепляние за прошлое" },
    { n: 14, name: "Умеренность", light: "гармония, исцеление и баланс", shadow: "крайности и нетерпение" },
    { n: 15, name: "Дьявол", light: "сила желания и честность с тенью", shadow: "зависимость и самообман" },
    { n: 16, name: "Башня", light: "прорыв, правда и освобождение", shadow: "разрушение и сопротивление" },
    { n: 17, name: "Звезда", light: "надежда, вдохновение и исцеление", shadow: "иллюзии и разочарование" },
    { n: 18, name: "Луна", light: "воображение, чувства и глубина", shadow: "тревога и туман" },
    { n: 19, name: "Солнце", light: "радость, ясность и успех", shadow: "выгорание и желание нравиться" },
    { n: 20, name: "Суд", light: "пробуждение, зов и итог", shadow: "самокритика и откладывание" },
    { n: 21, name: "Мир", light: "целостность, завершение и путь", shadow: "страх финала и новый круг" },
    { n: 22, name: "Шут", light: "свобода, спонтанность и начало", shadow: "безответственность и хаос" }
  ];

  const SIGNS = [
    { name: "Овен", element: "fire", from: 321, to: 419, text: "импульс, смелость и желание действовать первым" },
    { name: "Телец", element: "earth", from: 420, to: 520, text: "устойчивость, чувственность и умение создавать опору" },
    { name: "Близнецы", element: "air", from: 521, to: 620, text: "любопытство, слово и лёгкость связей" },
    { name: "Рак", element: "water", from: 621, to: 722, text: "забота, память и потребность в безопасности" },
    { name: "Лев", element: "fire", from: 723, to: 822, text: "творчество, щедрость и право быть заметным" },
    { name: "Дева", element: "earth", from: 823, to: 922, text: "точность, польза и внимание к деталям" },
    { name: "Весы", element: "air", from: 923, to: 1022, text: "баланс, эстетика и искусство договора" },
    { name: "Скорпион", element: "water", from: 1023, to: 1121, text: "глубина, страсть и способность к трансформации" },
    { name: "Стрелец", element: "fire", from: 1122, to: 1221, text: "смысл, горизонт и вера в большое" },
    { name: "Козерог", element: "earth", from: 1222, to: 119, text: "цель, ответственность и долгая дистанция" },
    { name: "Водолей", element: "air", from: 120, to: 218, text: "свобода, идея и взгляд со стороны" },
    { name: "Рыбы", element: "water", from: 219, to: 320, text: "чувствительность, воображение и сострадание" }
  ];

  const ELEMENT_NAMES = { fire: "Огонь", earth: "Земля", air: "Воздух", water: "Вода" };
  const ELEMENT_ADVICE = {
    fire: "действуйте, но не сжигайте себя: сегодня важен ритм, а не скорость.",
    earth: "закрепите результат делом: тело, деньги и порядок вернут опору.",
    air: "проговорите важное и проверьте факты: ясность приходит через обмен.",
    water: "не спешите отвечать: чувства скажут больше, чем первый вывод."
  };
  const ELEMENT_COMPAT = {
    fire: { fire: 8, air: 10, earth: 4, water: 3 },
    earth: { fire: 4, air: 3, earth: 9, water: 10 },
    air: { fire: 10, air: 8, earth: 3, water: 4 },
    water: { fire: 3, air: 4, earth: 10, water: 9 }
  };

  const NUMBER_MEANINGS = {
    1: { title: "Первопроходец", text: "главная тема — самостоятельность, воля и право начать своё.", advice: "Не ждите разрешения: маленький первый шаг важнее идеального плана." },
    2: { title: "Связующее звено", text: "важны отношения, чувствительность и умение слышать другого.", advice: "Не растворяйтесь в чужих ожиданиях: ваш голос тоже имеет вес." },
    3: { title: "Творец", text: "энергия выражения, слова и радости через создание.", advice: "Покажите миру то, что откладывали: творчество лечит." },
    4: { title: "Строитель", text: "тема устойчивости, труда, порядка и долгих результатов.", advice: "Стройте по кирпичу: стабильность любит регулярность." },
    5: { title: "Искатель", text: "перемены, свобода, опыт и вкус к новому.", advice: "Не путайте движение с бегством: выбирайте то, что расширяет вас." },
    6: { title: "Хранитель", text: "забота, дом, ответственность и красота отношений.", advice: "Сначала наполните себя: из пустоты трудно любить." },
    7: { title: "Исследователь", text: "глубина, анализ, уединение и поиск смысла.", advice: "Доверяйте тишине, но не превращайте её в крепость." },
    8: { title: "Распорядитель", text: "воля, деньги, власть и умение держать масштаб.", advice: "Сила растёт там, где есть честность с собой." },
    9: { title: "Проводник", text: "смысл, отдача, завершение циклов и широкий взгляд.", advice: "Отпускайте то, что уже отслужило: место освобождается для нового." }
  };

  const STEMS = [
    { name: "Цзя", element: "Дерево" }, { name: "И", element: "Дерево" },
    { name: "Бин", element: "Огонь" }, { name: "Дин", element: "Огонь" },
    { name: "У", element: "Земля" }, { name: "Цзи", element: "Земля" },
    { name: "Гэн", element: "Металл" }, { name: "Синь", element: "Металл" },
    { name: "Жэнь", element: "Вода" }, { name: "Гуй", element: "Вода" }
  ];
  const BRANCHES = [
    { name: "Цзы", animal: "Крыса", element: "Вода" }, { name: "Чоу", animal: "Бык", element: "Земля" },
    { name: "Инь", animal: "Тигр", element: "Дерево" }, { name: "Мао", animal: "Кролик", element: "Дерево" },
    { name: "Чэнь", animal: "Дракон", element: "Земля" }, { name: "Сы", animal: "Змея", element: "Огонь" },
    { name: "У", animal: "Лошадь", element: "Огонь" }, { name: "Вэй", animal: "Коза", element: "Земля" },
    { name: "Шэнь", animal: "Обезьяна", element: "Металл" }, { name: "Ю", animal: "Петух", element: "Металл" },
    { name: "Сюй", animal: "Собака", element: "Земля" }, { name: "Хай", animal: "Свинья", element: "Вода" }
  ];

  const TRIGRAMS = {
    "111": { name: "Цянь", image: "Небо", text: "сила, ясность и творческий импульс" },
    "000": { name: "Кунь", image: "Земля", text: "принятие, забота и способность выдержать" },
    "100": { name: "Чжэнь", image: "Гром", text: "пробуждение, движение и первый толчок" },
    "001": { name: "Гэнь", image: "Гора", text: "остановка, границы и внутренняя опора" },
    "010": { name: "Кань", image: "Вода", text: "глубина, риск и умение проходить трудное" },
    "101": { name: "Ли", image: "Огонь", text: "ясность, страсть и внимание к главному" },
    "011": { name: "Сюнь", image: "Ветер", text: "мягкое проникновение, перемены и контакт" },
    "110": { name: "Дуй", image: "Озеро", text: "радость, обмен и открытость" }
  };

  const SPHERES = [
    { name: "Единство", text: "ощущение целостности и связи с миром" },
    { name: "Творение", text: "способность создавать и проявляться" },
    { name: "Воля", text: "умение выбирать и держать направление" },
    { name: "Любовь", text: "тепло, близость и принятие" },
    { name: "Слово", text: "голос, правда и умение говорить" },
    { name: "Память", text: "род, прошлое и опора на опыт" },
    { name: "Путь", text: "движение, развитие и горизонт" },
    { name: "Свет", text: "ясность, надежда и вдохновение" },
    { name: "Тень", text: "то, что просит внимания и честности" },
    { name: "Дар", text: "талант, ресурс и щедрость" },
    { name: "Дом", text: "безопасность, тело и место силы" }
  ];

  const CITIES = [
    { name: "Москва", country: "Россия" }, { name: "Санкт-Петербург", country: "Россия" },
    { name: "Казань", country: "Россия" }, { name: "Екатеринбург", country: "Россия" },
    { name: "Новосибирск", country: "Россия" }, { name: "Сочи", country: "Россия" },
    { name: "Тбилиси", country: "Грузия" }, { name: "Стамбул", country: "Турция" },
    { name: "Дубай", country: "ОАЭ" }, { name: "Лиссабон", country: "Португалия" },
    { name: "Бали", country: "Индонезия" }, { name: "Нью-Йорк", country: "США" }
  ];

  const DREAM_SYMBOLS = [
    { keys: ["вод", "река", "море", "океан", "дожд", "купа"], title: "Вода", text: "чувства, очищение и глубина подсознания. Важно, какой была вода: спокойной или тревожной." },
    { keys: ["полёт", "лета", "пари", "крыл"], title: "Полёт", text: "стремление к свободе и взгляду сверху. Сон подсказывает, что вы готовы выйти за привычные рамки." },
    { keys: ["зме", "кольц"], title: "Змея", text: "мудрость, обновление и скрытая сила. Иногда знак перемен, которые уже начались внутри." },
    { keys: ["дом", "квартир", "комнат", "стро"], title: "Дом", text: "ваше внутреннее пространство и чувство безопасности. Обратите внимание на состояние дома во сне." },
    { keys: ["смерт", "погреб", "гроб", "умир"], title: "Смерть", text: "не буквальный знак, а завершение этапа. Что-то освобождает место для нового." },
    { keys: ["погон", "преслед", "убега", "бежат"], title: "Погоня", text: "напряжение и то, от чего вы стараетесь уйти. Полезно назвать страх по имени." },
    { keys: ["зуб", "паден", "пад"], title: "Падение", text: "потеря опоры или страх не справиться. Сон приглашает вернуть себе опору малыми шагами." },
    { keys: ["ребён", "младен", "беремен"], title: "Ребёнок", text: "новая часть себя, невинность и начало. Дайте этому ростку время." },
    { keys: ["лес", "дерев", "парк"], title: "Лес", text: "путь через неизвестное и связь с природной частью себя. Ищите тропу, а не идеальную карту." },
    { keys: ["огон", "пожар", "план"], title: "Огонь", text: "страсть, гнев или очищение. Если огонь согревает — энергия на подъёме." },
    { keys: ["кот", "кошк"], title: "Кот", text: "независимость, чувственность и право на своё пространство." },
    { keys: ["собак", "щен"], title: "Собака", text: "верность, дружба и защита. Возможно, рядом есть тот, на кого можно опереться." },
    { keys: ["свадь", "брак", "венчан"], title: "Свадьба", text: "союз частей себя или важное обещание. Не всегда про отношения с другим человеком." },
    { keys: ["деньг", "золот", "монет", "богат"], title: "Деньги", text: "ценность, ресурс и чувство защищённости. Сон может говорить о самооценке." },
    { keys: ["двер", "вход", "выйти"], title: "Дверь", text: "возможность и порог выбора. Закрытая дверь не всегда отказ — иногда она про время." },
    { keys: ["зеркал", "отраж"], title: "Зеркало", text: "встреча с собой и вопрос о том, насколько вы себя узнаёте." },
    { keys: ["час", "врем", "опозд"], title: "Время", text: "ощущение темпа и страх не успеть. Сон предлагает вернуть себе свой ритм." },
    { keys: ["лун", "ноч"], title: "Луна", text: "интуиция, скрытые чувства и то, что ещё не стало ясным." },
    { keys: ["солнц", "свет", "рассвет"], title: "Солнце", text: "ясность, жизненная сила и признание. Хороший знак для нового начинания." },
    { keys: ["дорог", "шоссе", "путь"], title: "Дорога", text: "направление и выбор. Важно, куда вы шли и с кем." },
    { keys: ["машин", "авто", "поезд"], title: "Транспорт", text: "скорость и управление собственной жизнью. Кто был за рулём?" },
    { keys: ["рыб", "океан"], title: "Рыбы", text: "глубина, изобилие и доверие потоку." },
    { keys: ["птиц", "перь"], title: "Птицы", text: "вести, свобода и связь с небом. Прислушайтесь к первому чувству." },
    { keys: ["кров", "ран", "болит"], title: "Кровь", text: "жизненная сила и уязвимость. Сон может указывать на усталость." }
  ];

  const FATE_LINES = [
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
    "Всё, что вы ищете, уже есть в вас — но это нужно прожить."
  ];

  const PAST_LIVES = [
    { era: "Древний Египет", role: "хранитель знаний", lesson: "учились доверять внутреннему знанию и не прятать его от мира.", task: "говорить о том, что вы видите, даже если вас не сразу понимают." },
    { era: "Античная Греция", role: "философ и ремесленник", lesson: "искали истину и учились ценить простое.", task: "соединить мысль и дело: не только объяснять, но и создавать." },
    { era: "Римская империя", role: "строитель и стратег", lesson: "понимали цену порядка и границ.", task: "строить не из страха, а из желания оставить после себя тепло." },
    { era: "Средневековье", role: "врачеватель и монах", lesson: "учились состраданию и тишине.", task: "не спасать всех ценой себя; забота начинается с собственного тела." },
    { era: "Ренессанс", role: "художник и изобретатель", lesson: "искали гармонию между красотой и смыслом.", task: "разрешить себе творить без разрешения и без идеального результата." },
    { era: "Эпоха мореплавания", role: "исследователь", lesson: "учились доверять горизонту и команде.", task: "не бояться нового маршрута, даже если карта неполная." },
    { era: "Древний Восток", role: "мастер и учитель", lesson: "постигали терпение и силу ритуала.", task: "делиться знанием так, чтобы другой становился сильнее, а не зависел." },
    { era: "XIX век", role: "инженер и революционер", lesson: "боролись за право менять мир.", task: "направлять огонь в созидание, а не только в протест." },
    { era: "Начало XX века", role: "артист и врач", lesson: "соединяли чувство и служение.", task: "не прятать талант за скромностью; ваш голос нужен." },
    { era: "Середина XX века", role: "дипломат и учитель", lesson: "учились говорить между разными языками.", task: "мирить не только других, но и собственные противоречия." },
    { era: "Недавнее прошлое", role: "хранитель семьи", lesson: "собирали опыт рода и учились отпускать.", task: "завершить повторяющийся сценарий, выбрав новый ответ." },
    { era: "Будущее", role: "проводник и исследователь", lesson: "пришли с идеей, которой ещё нет места в привычном.", task: "воплощать её постепенно, не ожидая всеобщего понимания." }
  ];

  const NAME_MEANINGS = {
    "александр": "защитник людей, человек с сильным чувством справедливости.",
    "алексей": "спокойная сила, умение брать ответственность на себя.",
    "андрей": "смелость и стремление к новому, внутренний стержень.",
    "анна": "мягкость с характером, умение заботиться и не терять себя.",
    "мария": "глубина, верность и способность давать тепло.",
    "елена": "свет, обаяние и умение видеть красоту в деталях.",
    "ольга": "воля, самостоятельность и острый ум.",
    "татьяна": "устойчивость, организаторский дар и верность.",
    "наталья": "родная душа, эмоциональность и умение объединять.",
    "ирина": "спокойствие, вкус и способность создавать уют.",
    "дмитрий": "практичность, воля и умение доводить дело до конца.",
    "сергей": "наблюдательность, ответственность и чувство долга.",
    "михаил": "масштаб, независимость и желание дойти до сути.",
    "екатерина": "ясность, достоинство и творческая сила.",
    "юлия": "лёгкость, эмоциональность и умение вдохновлять.",
    "павел": "надёжность, спокойствие и основательность.",
    "иван": "щедрость, открытость и внутренняя простота.",
    "софия": "мудрость, глубина и умение слышать других.",
    "виктория": "победа, харизма и способность вести за собой.",
    "максим": "настойчивость, сила и умение держать удар.",
    "дарья": "энергия, смелость и вкус к жизни.",
    "артём": "целеустремлённость, ясность и практичность.",
    "полина": "нежность, артистизм и умение мечтать.",
    "кирилл": "лидерство, логика и желание создавать новое."
  };

  const NAME_LIST = [
    { name: "Александр", gender: "male" }, { name: "Алексей", gender: "male" },
    { name: "Андрей", gender: "male" }, { name: "Артём", gender: "male" },
    { name: "Дмитрий", gender: "male" }, { name: "Иван", gender: "male" },
    { name: "Кирилл", gender: "male" }, { name: "Максим", gender: "male" },
    { name: "Михаил", gender: "male" }, { name: "Павел", gender: "male" },
    { name: "Сергей", gender: "male" }, { name: "Тимофей", gender: "male" },
    { name: "Анна", gender: "female" }, { name: "Алиса", gender: "female" },
    { name: "Виктория", gender: "female" }, { name: "Дарья", gender: "female" },
    { name: "Екатерина", gender: "female" }, { name: "Елена", gender: "female" },
    { name: "Ирина", gender: "female" }, { name: "Мария", gender: "female" },
    { name: "Наталья", gender: "female" }, { name: "Полина", gender: "female" },
    { name: "София", gender: "female" }, { name: "Юлия", gender: "female" }
  ];

  const LETTER_VALUES = {
    "А": 1, "Б": 2, "В": 3, "Г": 4, "Д": 5, "Е": 6, "Ё": 7, "Ж": 8, "З": 9,
    "И": 1, "Й": 2, "К": 3, "Л": 4, "М": 5, "Н": 6, "О": 7, "П": 8, "Р": 9,
    "С": 1, "Т": 2, "У": 3, "Ф": 4, "Х": 5, "Ц": 6, "Ч": 7, "Ш": 8, "Щ": 9,
    "Ъ": 1, "Ы": 2, "Ь": 3, "Э": 4, "Ю": 5, "Я": 6
  };

  const CATEGORY_META = {
    numerology: { label: "Нумерология", accent: "#e8c87c", description: "Дата рождения как карта характера, ресурса и повторяющихся сюжетов." },
    astrology: { label: "Астрология", accent: "#cabfff", description: "Символические расчёты неба: без точных эфемерид, но с ясными ориентирами." },
    archetypes: { label: "Архетипы, сны и имена", accent: "#efa6bd", description: "Истории души, язык сновидений и сила имени." }
  };

  const F = {
    birth: { key: "birthDate", label: "Дата рождения", type: "date", required: true },
    time: { key: "birthTime", label: "Время рождения", type: "time", required: false, hint: "Если знаете — точнее асцендент" },
    place: { key: "birthPlace", label: "Место рождения", type: "text", placeholder: "Город", required: false },
    partner: { key: "partnerDate", label: "Дата рождения партнёра", type: "date", required: true },
    child: { key: "childDate", label: "Дата рождения ребёнка", type: "date", required: true },
    name: { key: "name", label: "Имя", type: "text", placeholder: "Например, Анна", required: true },
    partnerName: { key: "partnerName", label: "Имя партнёра", type: "text", placeholder: "Например, Александр", required: true },
    question: { key: "question", label: "Ваш вопрос", type: "textarea", placeholder: "Сформулируйте вопрос как можно честнее", required: true },
    dream: { key: "dream", label: "Ваш сон", type: "textarea", placeholder: "Опишите сон: место, людей, ощущения, детали", required: true },
    gender: { key: "gender", label: "Кому подбираем имя", type: "select", options: [["female", "Девочке"], ["male", "Мальчику"]], required: true }
  };

  const PRACTICES = [
    {
      id: "matrix", category: "numerology", title: "Матрица судьбы", subtitle: "Характер, деньги и любовь по дате",
      symbol: "◈", accent: "#e8c87c", description: "Ключевые аркана даты рождения: характер, ресурс, отношения и предназначение.",
      chips: ["характер", "финансы", "отношения"], fields: [F.birth], action: "Открыть матрицу",
      modes: [{ id: "base", label: "База" }, { id: "year", label: "Личный год" }, { id: "finance", label: "Финансы" }, { id: "child", label: "Ребёнок" }, { id: "couple", label: "Пара" }]
    },
    {
      id: "pythagoras", category: "numerology", title: "Квадрат Пифагора", subtitle: "Психоматрица по дате рождения",
      symbol: "▦", accent: "#d8c07a", description: "Девять ячеек о характере, энергии, здоровье, логике и жизненной опоре.",
      chips: ["психоматрица", "сильные стороны", "ресурс"], fields: [F.birth], action: "Построить квадрат",
      modes: [{ id: "base", label: "База" }, { id: "child", label: "Ребёнок" }, { id: "couple", label: "Пара" }]
    },
    {
      id: "fate-graph", category: "numerology", title: "График судьбы", subtitle: "Линия жизни по годам",
      symbol: "✦", accent: "#e4b96d", description: "Семь точек жизненного цикла: где накапливается сила, а где нужен отдых.",
      chips: ["циклы", "повороты", "прогноз"], fields: [F.birth], action: "Построить график",
      modes: [{ id: "base", label: "Личный" }, { id: "couple", label: "Пара" }]
    },
    {
      id: "energy", category: "numerology", title: "Энергетический тип", subtitle: "Как вы обмениваетесь энергией",
      symbol: "◉", accent: "#d7a85f", description: "Четыре способа быть в контакте с силой: источник, проводник, хранитель, преобразователь.",
      chips: ["энергия", "границы", "совместимость"], fields: [F.birth], action: "Определить тип",
      modes: [{ id: "base", label: "Тип" }, { id: "compatibility", label: "Совместимость" }]
    },
    {
      id: "destiny-number", category: "numerology", title: "Число судьбы", subtitle: "Главная тема жизненного пути",
      symbol: "∞", accent: "#e8c87c", description: "Число даты и число имени: о чём ваша жизнь просит говорить громче.",
      chips: ["путь", "имя", "предназначение"], fields: [F.birth], action: "Рассчитать число",
      modes: [{ id: "base", label: "Дата" }, { id: "name", label: "Дата и имя" }]
    },

    {
      id: "natal", category: "astrology", title: "Натальная карта", subtitle: "Небо минуты вашего рождения",
      symbol: "☉", accent: "#cabfff", description: "Солнце, Луна и символический асцендент — три опоры вашей астрологической карты.",
      chips: ["солнце", "луна", "асцендент"], fields: [F.birth, F.time, F.place], action: "Открыть карту",
      modes: [{ id: "base", label: "Карта" }, { id: "solar", label: "Соляр" }, { id: "synastry", label: "Синастрия" }]
    },
    {
      id: "transits", category: "astrology", title: "Транзиты", subtitle: "Энергия дня и символический прогноз",
      symbol: "☌", accent: "#b9a7e6", description: "Солнце, Луна и лунный день: мягкий ориентир для решений и отдыха.",
      chips: ["день", "луна", "совет"], fields: [], action: "Поймать энергию дня",
      modes: [{ id: "base", label: "Сегодня" }]
    },
    {
      id: "bazi", category: "astrology", title: "Бацзы", subtitle: "Четыре столпа судьбы",
      symbol: "易", accent: "#b9a7e6", description: "Год, месяц, день и час рождения в китайской традиции: стихии и опора.",
      chips: ["стихии", "столпы", "баланс"], fields: [F.birth, F.time], action: "Собрать столпы",
      modes: [{ id: "base", label: "Четыре столпа" }]
    },
    {
      id: "iching", category: "astrology", title: "Книга перемен", subtitle: "Шесть бросков монет",
      symbol: "☰", accent: "#cabfff", description: "Две триграммы складываются в образ: что происходит и куда стоит направить внимание.",
      chips: ["гексаграмма", "перемены", "совет"], fields: [], action: "Бросить монеты",
      modes: [{ id: "coins", label: "Бросок" }]
    },
    {
      id: "human-design", category: "astrology", title: "Дизайн человека", subtitle: "Тип и стратегия",
      symbol: "▲", accent: "#b9a7e6", description: "Символический расчёт типа, стратегии, авторитета и активных центров.",
      chips: ["тип", "стратегия", "центры"], fields: [F.birth, F.time], action: "Открыть бодиграф",
      modes: [{ id: "base", label: "Бодиграф" }]
    },
    {
      id: "hologenetics", category: "astrology", title: "Хологенетика", subtitle: "11 сфер и ключи",
      symbol: "✧", accent: "#cabfff", description: "Одиннадцать сфер опыта: где у вас врождённая сила, а где нужен новый выбор.",
      chips: ["11 сфер", "ключи", "ресурс"], fields: [F.birth], action: "Открыть сферы",
      modes: [{ id: "base", label: "Сферы" }]
    },
    {
      id: "astrocart", category: "astrology", title: "Астрокарта", subtitle: "Города силы",
      symbol: "⊕", accent: "#b9a7e6", description: "Символическая карта мест: где легче вдохновляться, работать и восстанавливаться.",
      chips: ["города", "места силы", "путь"], fields: [F.birth], action: "Найти города",
      modes: [{ id: "base", label: "Карта" }]
    },

    {
      id: "three-archetypes", category: "archetypes", title: "Три архетипа", subtitle: "Три аркана по дате рождения",
      symbol: "♆", accent: "#efa6bd", description: "Суть, сердце и путь: три образа, которые подсвечивают ваш характер.",
      chips: ["арканы", "характер", "путь"], fields: [F.birth], action: "Открыть архетипы",
      modes: [{ id: "base", label: "Три архетипа" }]
    },
    {
      id: "dreams", category: "archetypes", title: "Сны", subtitle: "Толкование символов сна",
      symbol: "☾", accent: "#efa6bd", description: "Введите сон — найдём повторяющиеся образы и мягкий совет из подсознания.",
      chips: ["сонник", "образы", "подсознание"], fields: [F.dream], action: "Истолковать сон",
      modes: [{ id: "base", label: "Символы" }]
    },
    {
      id: "book-of-fate", category: "archetypes", title: "Книга судеб", subtitle: "Ответ на вопрос одной строкой",
      symbol: "▤", accent: "#efa6bd", description: "Задайте вопрос — книга откроет страницу с короткой подсказкой.",
      chips: ["вопрос", "страница", "ответ"], fields: [F.question], action: "Открыть книгу",
      modes: [{ id: "base", label: "Страница" }]
    },
    {
      id: "past-life", category: "archetypes", title: "Прошлая жизнь", subtitle: "История души и урок",
      symbol: "◐", accent: "#e39ab5", description: "Символическая история о прошлом воплощении: роль, урок и задача.",
      chips: ["душа", "урок", "карма"], fields: [F.birth], action: "Вспомнить историю",
      modes: [{ id: "base", label: "История" }]
    },
    {
      id: "names", category: "archetypes", title: "Мир имён", subtitle: "Значение имени и число",
      symbol: "✒", accent: "#efa6bd", description: "Число имени, его характер и совместимость с другим именем. Плюс подбор имени.",
      chips: ["имя", "значение", "совместимость"], fields: [F.name], action: "Раскрыть имя",
      modes: [{ id: "meaning", label: "Значение" }, { id: "compatibility", label: "Совместимость" }, { id: "selection", label: "Подбор" }]
    }
  ];

  const state = { practice: null, mode: null, values: {} };
  const $ = (id) => document.getElementById(id);
  const esc = (value) => String(value == null ? "" : value).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

  /* ── Вспомогательные функции ────────────────────────── */
  function digitsOf(n) { return String(Math.abs(Math.trunc(n))).split("").map(Number); }
  function digitSum(n) { return digitsOf(n).reduce((a, b) => a + b, 0); }
  function mod(n, m) { return ((n % m) + m) % m; }
  function reduce9(n) { n = Math.abs(Math.trunc(n)); while (n > 9) n = digitSum(n); return n; }
  function reduce22(n) { n = Math.abs(Math.trunc(n)); if (n === 0) return 22; while (n > 22) n = digitSum(n); return n; }
  function hashString(text) {
    let h = 2166136261;
    const s = String(text || "");
    for (let i = 0; i < s.length; i += 1) { h ^= s.charCodeAt(i); h = Math.imul(h, 16777619); }
    return h >>> 0;
  }
  function parseDate(value) {
    if (!value) return null;
    const s = String(value).trim();
    let y, m, d;
    let match = s.match(/^(\d{4})-(\d{1,2})-(\d{1,2})$/);
    if (match) { y = +match[1]; m = +match[2]; d = +match[3]; }
    else {
      match = s.match(/^(\d{1,2})[.\/,](\d{1,2})[.\/,](\d{4})$/);
      if (!match) return null;
      d = +match[1]; m = +match[2]; y = +match[3];
    }
    if (y < 1900 || y > 2100 || m < 1 || m > 12 || d < 1 || d > 31) return null;
    const dt = new Date(Date.UTC(y, m - 1, d));
    if (dt.getUTCFullYear() !== y || dt.getUTCMonth() !== m - 1 || dt.getUTCDate() !== d) return null;
    return { year: y, month: m, day: d, date: dt };
  }
  function formatDate(d) {
    return d ? d.date.toLocaleDateString("ru-RU", { day: "2-digit", month: "long", year: "numeric", timeZone: "UTC" }) : "";
  }
  function daysBetween(a, b) { return Math.round((b.getTime() - a.getTime()) / 86400000); }
  function signFor(date) {
    const value = (date.getUTCMonth() + 1) * 100 + date.getUTCDate();
    for (const sign of SIGNS) {
      if (sign.from <= sign.to) { if (value >= sign.from && value <= sign.to) return sign; }
      else if (value >= sign.from || value <= sign.to) return sign;
    }
    return SIGNS[0];
  }
  function moonSignFor(date) {
    const base = Date.UTC(2000, 0, 6);
    const days = daysBetween(new Date(base), date);
    const angle = mod(days * 13.176, 360);
    return SIGNS[Math.floor(angle / 30) % 12];
  }
  function risingSignFor(date, time) {
    const sunIndex = SIGNS.findIndex((s) => s.name === signFor(date).name);
    let hour = 12;
    if (time && /^\d{1,2}:\d{2}/.test(time)) {
      const parts = time.split(":");
      hour = Number(parts[0]) + Number(parts[1]) / 60;
    }
    const index = mod(sunIndex + Math.floor(mod(hour - 6, 24) / 2), 12);
    return SIGNS[index];
  }
  function arcana(n) { return ARCANA[mod(n - 1, 22)]; }
  function arcanaValue(n) { const a = arcana(n); return n + ". " + a.name; }
  function arcanaText(n) { const a = arcana(n); return a.light + ". Тень: " + a.shadow + "."; }
  function matrixOf(d) {
    const left = reduce22(d.day);
    const top = reduce22(d.month);
    const right = reduce22(digitSum(d.year));
    const center = reduce22(left + top + right);
    return { left, top, right, center };
  }
  function nameNumber(name) {
    const upper = String(name || "").toUpperCase().replace(/[^А-ЯЁ]/g, "");
    let sum = 0;
    for (const ch of upper) sum += LETTER_VALUES[ch] || 0;
    return reduce9(sum || 1);
  }
  function julianDay(d) {
    const a = Math.floor((14 - d.month) / 12);
    const y = d.year + 4800 - a;
    const m = d.month + 12 * a - 3;
    return d.day + Math.floor((153 * m + 2) / 5) + 365 * y + Math.floor(y / 4) - Math.floor(y / 100) + Math.floor(y / 400) - 32045;
  }
  function elementScore(elements) {
    const result = { fire: 0, earth: 0, air: 0, water: 0 };
    elements.forEach((e) => { if (result[e] != null) result[e] += 1; });
    return result;
  }
  function dominantElement(elements) {
    const counts = elementScore(elements);
    return Object.keys(counts).sort((a, b) => counts[b] - counts[a])[0];
  }
  function block(label, value, text, wide) {
    return { label, value, text, wide: !!wide };
  }
  function chart(type, data) { return { type, ...data }; }

  /* ── Расчёты практик ────────────────────────────────── */
  function calculateMatrix(d, mode, values) {
    const m = matrixOf(d);
    if (mode === "year") {
      const personal = reduce9(d.day + d.month + new Date().getFullYear());
      return {
        title: "Личный год", subtitle: "Цикл от дня рождения до следующего дня рождения",
        blocks: [
          block("Число года", String(personal), NUMBER_MEANINGS[personal].text, true),
          block("Тема", NUMBER_MEANINGS[personal].title, NUMBER_MEANINGS[personal].advice, true),
          block("Опорный аркан", arcanaValue(m.center), arcanaText(m.center), true)
        ],
        note: "Личный год считается от даты рождения, а не от 1 января."
      };
    }
    if (mode === "finance") {
      const finance = reduce22(m.right + m.center);
      return {
        title: "Финансовый поток", subtitle: "Ресурс, точка роста и обращение с деньгами",
        blocks: [
          block("Аркан денег", arcanaValue(finance), arcanaText(finance), true),
          block("Что привлекает доход", arcanaValue(m.right), arcanaText(m.right), true),
          block("Что мешает потоку", arcanaValue(m.top), arcanaText(m.top), true),
          block("Следующий шаг", "Ясность", "Назовите конкретную сумму и срок. Деньги любят не мечту, а понятное действие.", true)
        ],
        note: "Финансовый аркан — символический ориентир, а не инвестиционная рекомендация."
      };
    }
    if (mode === "child") {
      const child = parseDate(values.childDate);
      if (!child) return null;
      const cm = matrixOf(child);
      return {
        title: "Матрица ребёнка", subtitle: formatDate(child),
        blocks: [
          block("Характер", arcanaValue(cm.left), arcanaText(cm.left), true),
          block("Энергия", arcanaValue(cm.top), arcanaText(cm.top), true),
          block("Опора", arcanaValue(cm.right), arcanaText(cm.right), true),
          block("Потенциал", arcanaValue(cm.center), arcanaText(cm.center), true)
        ],
        note: "Это язык образов для разговора о ребёнке, а не ярлык и не диагноз."
      };
    }
    if (mode === "couple") {
      const partner = parseDate(values.partnerDate);
      if (!partner) return null;
      const pm = matrixOf(partner);
      const union = reduce22(m.center + pm.center);
      return {
        title: "Матрица пары", subtitle: "Вы и партнёр",
        blocks: [
          block("Ваш центр", arcanaValue(m.center), arcanaText(m.center), true),
          block("Центр партнёра", arcanaValue(pm.center), arcanaText(pm.center), true),
          block("Аркан союза", arcanaValue(union), arcanaText(union), true),
          block("Совет", "Не сливаться", "Сохраняйте личные границы и общее пространство. Союз силён там, где есть два целых человека.", true)
        ],
        note: "Совместимость — не приговор, а описание динамики."
      };
    }
    const love = reduce22(m.top + m.right);
    return {
      title: "Матрица судьбы", subtitle: formatDate(d),
      blocks: [
        block("Характер", arcanaValue(m.left), arcanaText(m.left), true),
        block("Энергия", arcanaValue(m.top), arcanaText(m.top), true),
        block("Деньги", arcanaValue(m.right), arcanaText(m.right), true),
        block("Предназначение", arcanaValue(m.center), arcanaText(m.center), true),
        block("Отношения", arcanaValue(love), arcanaText(love), true),
        block("Формула", m.left + " · " + m.top + " · " + m.right + " · " + m.center, "Четыре опоры, из которых складывается ваш жизненный рисунок.", true)
      ],
      note: "Матрица судьбы — символическая система самопознания. Она не предсказывает события буквально."
    };
  }

  const PYTH_MEANINGS = {
    1: { label: "Характер", variants: ["мягкость и зависимость от мнений", "уверенность и чувство меры", "сильная воля и желание вести", "лидерство и ответственность за других"] },
    2: { label: "Энергия", variants: ["берегите силы и избегайте перегрузки", "достаточный запас энергии", "высокая выносливость", "мощная энергия, важно направлять её в дело"] },
    3: { label: "Интерес", variants: ["нужен внешний импульс", "есть устойчивый интерес", "яркая творческая жилка", "многогранность и тяга к новому"] },
    4: { label: "Здоровье", variants: ["нужен режим и восстановление", "устойчивость и хороший тонус", "выносливость и сила", "крепкое тело, но не забывайте про отдых"] },
    5: { label: "Логика", variants: ["доверяйте чувству и телу", "баланс логики и интуиции", "ясный ум и аналитика", "сильный аналитик, проверяйте выводы"] },
    6: { label: "Мастерство", variants: ["ищите дело по душе", "есть ремесло и трудолюбие", "мастерство и внимание к качеству", "талант создавать и доводить до конца"] },
    7: { label: "Удача", variants: ["создавайте поток сами", "удача приходит через действие", "сильный поток и поддержка", "везение там, где вы рискуете с умом"] },
    8: { label: "Долг", variants: ["свобода важнее обязательств", "чувство ответственности", "надёжность и опора", "вы держите слово и ведёте других"] },
    9: { label: "Память", variants: ["нужно обучение и опыт", "есть глубина и память", "сильная интуитивная память", "мудрость и связь с прошлым"] }
  };

  function calculatePythagoras(d, mode, values) {
    let target = d;
    if (mode === "child") target = parseDate(values.childDate);
    if (!target) return null;
    if (mode === "couple") {
      const partner = parseDate(values.partnerDate);
      if (!partner) return null;
      const a = pythagorasCore(target);
      const b = pythagorasCore(partner);
      const diff = Object.keys(a.counts).reduce((acc, key) => acc + Math.abs(a.counts[key] - b.counts[key]), 0);
      const score = Math.max(2, 10 - diff);
      return {
        title: "Психоматрица пары", subtitle: "Вы и партнёр",
        blocks: [
          block("Совместимость", score + " / 10", score >= 8 ? "много общего и лёгкое взаимопонимание." : score >= 5 ? "есть точки роста: важно говорить о разнице." : "сильная разница, которая требует зрелости и границ.", true),
          block("Ваш профиль", pythagorasSummary(a.counts), "", true),
          block("Профиль партнёра", pythagorasSummary(b.counts), "", true)
        ],
        chart: chart("pythagoras", { cells: a.cells, second: b.cells }),
        note: "Сравнение носит символический характер и не заменяет разговор."
      };
    }
    const core = pythagorasCore(target);
    const strong = Object.keys(core.counts).filter((k) => core.counts[k] >= 2).map((k) => PYTH_MEANINGS[k].label);
    const weak = Object.keys(core.counts).filter((k) => core.counts[k] === 0).map((k) => PYTH_MEANINGS[k].label);
    return {
      title: mode === "child" ? "Квадрат ребёнка" : "Квадрат Пифагора",
      subtitle: formatDate(target),
      blocks: [
        block("Сильные стороны", strong.length ? strong.join(" · ") : "баланс без ярких акцентов", "Эти качества можно развивать сознательно.", true),
        block("Точки роста", weak.length ? weak.join(" · ") : "все ячейки заполнены", "Здесь полезно действовать мягко и без давления.", true),
        block("Кратко", pythagorasSummary(core.counts), "", true)
      ],
      chart: chart("pythagoras", { cells: core.cells }),
      note: "В квадрате считается количество цифр в дате после рабочих чисел — это символическая психоматрица."
    };
  }

  function pythagorasCore(d) {
    const dayStr = String(d.day);
    const monthStr = String(d.month);
    const yearStr = String(d.year);
    const base = dayStr + monthStr + yearStr;
    const sum1 = base.split("").reduce((a, b) => a + Number(b), 0);
    const sum2 = reduce9(sum1);
    const first = Number(dayStr[0]) || 1;
    const sum3 = Math.abs(sum1 - 2 * first);
    const sum4 = reduce9(sum3);
    const all = (base + String(sum1) + String(sum2) + String(sum3) + String(sum4)).split("").filter((ch) => ch >= "1" && ch <= "9");
    const counts = {};
    for (let i = 1; i <= 9; i += 1) counts[i] = 0;
    all.forEach((ch) => { counts[Number(ch)] += 1; });
    const cells = Object.keys(counts).map((key) => ({ digit: Number(key), count: counts[key] }));
    return { counts, cells };
  }

  function pythagorasSummary(counts) {
    const strong = Object.keys(counts).filter((k) => counts[k] >= 2).map((k) => PYTH_MEANINGS[k].label);
    if (!strong.length) return "ровный профиль: силы распределены без крайностей.";
    return "ярче всего: " + strong.slice(0, 3).join(", ") + ".";
  }

  function fatePoints(d) {
    const y = digitSum(d.year);
    return [
      reduce22(d.day), reduce22(d.month), reduce22(y),
      reduce22(d.day + d.month), reduce22(d.month + y),
      reduce22(d.day + y), reduce22(d.day + d.month + y)
    ];
  }

  function calculateFate(d, mode, values) {
    const points = fatePoints(d);
    const partner = mode === "couple" ? parseDate(values.partnerDate) : null;
    if (mode === "couple" && !partner) return null;
    const partnerPoints = partner ? fatePoints(partner) : null;
    const max = Math.max(...points);
    const min = Math.min(...points);
    const maxIndex = points.indexOf(max);
    const minIndex = points.indexOf(min);
    const labels = ["Исток", "Детство", "Юность", "Поворот", "Зрелость", "Расцвет", "Итог"];
    return {
      title: "График судьбы", subtitle: formatDate(d) + (partner ? " · пара" : ""),
      blocks: [
        block("Пик", labels[maxIndex] + " · " + max, "Здесь жизненная сила и возможности проявляются ярче.", true),
        block("Точка отдыха", labels[minIndex] + " · " + min, "В этом периоде важно не форсировать события и беречь ресурс.", true),
        block("Финал", labels[6] + " · " + points[6], "Это не конец, а точка сборки опыта: что вы забираете с собой.", true),
        block("Совет", "Ритм", "Сверяйтесь с графиком как с картой: он показывает не приговор, а смену темпа.", true)
      ],
      chart: chart("fate", { points, partnerPoints, labels }),
      note: "График строится по цифрам даты рождения; это символическая периодизация."
    };
  }

  const ENERGY_TYPES = [
    { name: "Источник", text: "вы заряжаете пространство и людей, но легко отдаёте больше, чем остаётся вам.", strong: "щедрость, тепло, способность вдохновить", risk: "выгорание и забытые собственные желания" },
    { name: "Проводник", text: "вы чувствуете фон и умеете соединять людей, идеи и возможности.", strong: "эмпатия, вкус, умение договариваться", risk: "потеря себя в чужих ожиданиях" },
    { name: "Хранитель", text: "вы создаёте устойчивость и безопасность, на вас можно опереться.", strong: "надёжность, забота, верность", risk: "сопротивление переменам и контроль" },
    { name: "Преобразователь", text: "вы меняете реальность вокруг, проходя через напряжение и обновление.", strong: "воля, глубина, способность к трансформации", risk: "жизнь на пределе и резкие развороты" }
  ];

  function calculateEnergy(d, mode, values) {
    const center = matrixOf(d).center;
    const typeIndex = mod(center, 4);
    const type = ENERGY_TYPES[typeIndex];
    if (mode === "compatibility") {
      const partner = parseDate(values.partnerDate);
      if (!partner) return null;
      const pTypeIndex = mod(matrixOf(partner).center, 4);
      const pType = ENERGY_TYPES[pTypeIndex];
      const score = Math.max(2, 10 - Math.abs(typeIndex - pTypeIndex) * 2);
      return {
        title: "Энергетическая совместимость", subtitle: type.name + " + " + pType.name,
        blocks: [
          block("Вы", type.name, type.text, true),
          block("Партнёр", pType.name, pType.text, true),
          block("Совместимость", score + " / 10", score >= 8 ? "вам легко быть рядом, но важно не сливаться." : score >= 5 ? "разные ритмы: помогает честный разговор о границах." : "сильное притяжение и такая же сильная разница: нужна зрелость.", true),
          block("Совет", "Обмен", "Договоритесь, кто и когда восстанавливается. Энергия — общий ресурс, а не соревнование.", true)
        ],
        note: "Тип определяется по центру матрицы судьбы; это символическая модель."
      };
    }
    return {
      title: "Энергетический тип", subtitle: formatDate(d),
      blocks: [
        block("Ваш тип", type.name, type.text, true),
        block("Сильная сторона", type.strong, "Опирайтесь на это, когда нужно вернуть себе устойчивость.", true),
        block("Зона риска", type.risk, "Заметьте, где вы теряете энергию, и верните себе паузу.", true),
        block("Практика", "5 минут тишины", "Спросите себя: что сейчас даёт мне силу, а что забирает? Ответ — уже начало.", true)
      ],
      note: "Не делите людей на «доноров» и «вампиров»: энергия зависит от контекста и границ."
    };
  }

  function calculateDestiny(d, mode, values) {
    const number = reduce9(d.day + d.month + d.year);
    const meaning = NUMBER_MEANINGS[number];
    const blocks = [
      block("Число даты", String(number), meaning.text, true),
      block("Тема", meaning.title, meaning.advice, true)
    ];
    if (mode === "name" && values.name) {
      const nn = nameNumber(values.name);
      const nm = NUMBER_MEANINGS[nn];
      blocks.push(block("Число имени", nn + " · " + nm.title, nm.text + " " + nm.advice, true));
    }
    return {
      title: "Число судьбы", subtitle: formatDate(d) + (mode === "name" && values.name ? " · " + values.name : ""),
      blocks,
      note: "Число судьбы — символический ориентир, а не жёсткая программа."
    };
  }

  function calculateNatal(d, mode, values) {
    if (mode === "solar") {
      const year = new Date().getFullYear();
      const solarNumber = reduce9(d.day + d.month + year);
      return {
        title: "Соляр " + year, subtitle: "Год от дня рождения",
        blocks: [
          block("Солнце", signFor(d.date).name, signFor(d.date).text, true),
          block("Число года", String(solarNumber), NUMBER_MEANINGS[solarNumber].text, true),
          block("Тема года", NUMBER_MEANINGS[solarNumber].title, NUMBER_MEANINGS[solarNumber].advice, true)
        ],
        note: "Соляр — символический срез года, а не точный прогноз событий."
      };
    }
    if (mode === "synastry") {
      const partner = parseDate(values.partnerDate);
      if (!partner) return null;
      const a = signFor(d.date);
      const b = signFor(partner.date);
      const score = ELEMENT_COMPAT[a.element][b.element];
      return {
        title: "Синастрия", subtitle: a.name + " + " + b.name,
        blocks: [
          block("Ваше Солнце", a.name, a.text, true),
          block("Солнце партнёра", b.name, b.text, true),
          block("Совместимость", score + " / 10", score >= 8 ? "естественное притяжение и общий язык." : score >= 5 ? "есть притяжение и зоны, где нужен перевод." : "разные языки: учитесь говорить прямо и без обвинений.", true),
          block("Совет", "Перевод", "Не требуйте, чтобы партнёр чувствовал как вы. Спрашивайте, что для него значит забота.", true)
        ],
        note: "Синастрия по Солнцу — упрощённый срез; для точной карты нужны время и место рождения обоих."
      };
    }
    const time = values.birthTime || "";
    const sun = signFor(d.date);
    const moon = moonSignFor(d.date);
    const rising = risingSignFor(d.date, time);
    const elements = [sun.element, moon.element, rising.element];
    const dominant = dominantElement(elements);
    return {
      title: "Натальная карта", subtitle: formatDate(d) + (time ? " · " + time : ""),
      blocks: [
        block("Солнце", sun.name, sun.text, true),
        block("Луна", moon.name, "внутренняя жизнь: " + moon.text, true),
        block("Асцендент", rising.name, "как вас встречают: " + rising.text + (time ? "" : " (время не указано — символ условен)"), true),
        block("Стихия", ELEMENT_NAMES[dominant], "ведущая стихия карты. " + ELEMENT_ADVICE[dominant], true),
        block("Совет", "Соберите три опоры", "Солнце — что вас зажигает, Луна — что успокаивает, асцендент — как вы входите в контакт.", true)
      ],
      note: "Луна и асцендент рассчитаны приближённо. Точная карта требует эфемерид, времени и координат места рождения."
    };
  }

  function calculateTransits() {
    const today = new Date();
    const utc = new Date(Date.UTC(today.getFullYear(), today.getMonth(), today.getDate()));
    const sun = signFor(utc);
    const moon = moonSignFor(utc);
    const newMoon = Date.UTC(2000, 0, 6);
    const lunarDay = Math.floor(mod(daysBetween(new Date(newMoon), utc), 29.530588)) + 1;
    const dayNumber = reduce9(utc.getUTCDate() + utc.getUTCMonth() + 1 + utc.getUTCFullYear());
    return {
      title: "Энергия дня", subtitle: formatDate({ date: utc }),
      blocks: [
        block("Солнце", sun.name, sun.text, true),
        block("Луна", moon.name, "фон дня: " + moon.text, true),
        block("Лунный день", String(lunarDay), lunarDay <= 7 ? "начало цикла: закладывайте намерение." : lunarDay <= 14 ? "рост: действуйте и учитесь." : lunarDay <= 22 ? "зрелость: собирайте результат." : "завершение: отпускайте лишнее.", true),
        block("Совет", ELEMENT_NAMES[sun.element], ELEMENT_ADVICE[sun.element], true),
        block("Число дня", String(dayNumber), NUMBER_MEANINGS[dayNumber].advice, true)
      ],
      note: "Символический прогноз. Точные транзиты требуют полноценных эфемерид."
    };
  }

  function calculateBazi(d, values) {
    const time = values.birthTime || "12:00";
    const hour = Number(time.split(":")[0]) || 12;
    const yStem = mod(d.year - 4, 10);
    const yBranch = mod(d.year - 4, 12);
    const mBranch = mod(d.month, 12);
    const mStem = mod(yStem * 2 + d.month + 1, 10);
    const jdn = julianDay(d);
    const dStem = mod(jdn + 9, 10);
    const dBranch = mod(jdn + 1, 12);
    const hBranch = Math.floor(mod(hour + 1, 24) / 2) % 12;
    const hStem = mod(dStem * 2 + hBranch, 10);
    const pillars = [
      { label: "Год", stem: STEMS[yStem], branch: BRANCHES[yBranch] },
      { label: "Месяц", stem: STEMS[mStem], branch: BRANCHES[mBranch] },
      { label: "День", stem: STEMS[dStem], branch: BRANCHES[dBranch] },
      { label: "Час", stem: STEMS[hStem], branch: BRANCHES[hBranch] }
    ];
    const elements = pillars.flatMap((p) => [p.stem.element, p.branch.element]);
    const counts = {};
    elements.forEach((e) => { counts[e] = (counts[e] || 0) + 1; });
    const dominant = Object.keys(counts).sort((a, b) => counts[b] - counts[a])[0];
    const missing = ["Дерево", "Огонь", "Земля", "Металл", "Вода"].filter((e) => !counts[e]);
    return {
      title: "Четыре столпа", subtitle: formatDate(d) + " · " + time,
      blocks: [
        block("Ведущая стихия", dominant, "она задаёт основной способ действовать и восстанавливаться.", true),
        block("Чего не хватает", missing.length ? missing.join(" · ") : "все стихии присутствуют", "Это не приговор: недостающее можно развивать средой и привычками.", true),
        block("Опора", pillars[2].stem.name + " " + pillars[2].branch.name, "столп дня часто читают как центр личности.", true)
      ],
      chart: chart("pillars", { pillars }),
      note: "Упрощённый расчёт по солнечному календарю. Точные столпы требуют перевода в китайский календарь и учёта солнечных терминов."
    };
  }

  function throwHexagram() {
    const lines = [];
    for (let i = 0; i < 6; i += 1) {
      let sum = 0;
      for (let c = 0; c < 3; c += 1) sum += Math.random() < 0.5 ? 2 : 3;
      lines.push({ value: sum, yang: sum === 7 || sum === 9, changing: sum === 6 || sum === 9 });
    }
    const lowerBits = lines.slice(0, 3).map((l) => (l.yang ? "1" : "0")).join("");
    const upperBits = lines.slice(3, 6).map((l) => (l.yang ? "1" : "0")).join("");
    const lower = TRIGRAMS[lowerBits];
    const upper = TRIGRAMS[upperBits];
    const changing = lines.filter((l) => l.changing).length;
    return {
      title: "Знак перемен", subtitle: upper.image + " над " + lower.image,
      blocks: [
        block("Верхняя триграмма", upper.name, upper.text, true),
        block("Нижняя триграмма", lower.name, lower.text, true),
        block("Образ", upper.image + " над " + lower.image, "Внутри — " + lower.text + ". Снаружи — " + upper.text + ".", true),
        block("Совет", changing === 0 ? "Устойчивость" : changing === 1 ? "Один поворот" : changing <= 3 ? "Несколько уроков" : "Большая перемена", changing === 0 ? "Ситуация стабильна: не форсируйте, наблюдайте." : "Заметьте, что уже меняется. Не цепляйтесь за прежнюю форму.", true)
      ],
      chart: chart("hexagram", { lines }),
      note: "Упрощённая практика: знак собран из двух триграмм. Каноническая Книга перемен использует 64 гексаграммы."
    };
  }

  const HD_TYPES = [
    { name: "Генератор", strategy: "Ждать отклика", text: "ваша сила включается там, где есть живой отклик, а не должно." },
    { name: "Проектор", strategy: "Ждать приглашения", text: "вы видите систему целиком, но ваша мудрость раскрывается в приглашении." },
    { name: "Манифестор", strategy: "Информировать", text: "вы начинаете импульс и меняете пространство, когда предупреждаете других." },
    { name: "Рефлектор", strategy: "Проживать лунный цикл", text: "вы зеркалите среду и лучше видите себя через смену состояний." }
  ];

  function calculateHumanDesign(d, values) {
    const seed = hashString(d.year + "-" + d.month + "-" + d.day + "-hd");
    const type = HD_TYPES[seed % 4];
    const authority = type.name === "Генератор" ? "Сакральный" : type.name === "Проектор" ? "Селезёночный" : type.name === "Манифестор" ? "Эмоциональный" : "Лунный";
    const p1 = (d.day % 6) + 1;
    const p2 = (d.month % 6) + 1;
    const centers = ["Голова", "Аджна", "Горло", "G-центр", "Сердце", "Сакрал", "Селезёнка", "Солнечное сплетение", "Корень"];
    const defined = centers.filter((_, index) => ((seed >> index) & 1) === 1);
    const open = centers.filter((_, index) => ((seed >> index) & 1) === 0);
    return {
      title: "Дизайн человека", subtitle: formatDate(d) + (values.birthTime ? " · " + values.birthTime : ""),
      blocks: [
        block("Тип", type.name, type.text, true),
        block("Стратегия", type.strategy, "попробуйте принимать решения через эту стратегию.", true),
        block("Авторитет", authority, "внутренний способ проверять правильность решения.", true),
        block("Профиль", p1 + "/" + p2, "сочетание ролей: как вы учитесь и как входите в контакт.", true),
        block("Определённые центры", defined.length ? defined.join(" · ") : "нет ярко выраженных", "Здесь энергия работает стабильно.", true),
        block("Открытые центры", open.length ? open.slice(0, 4).join(" · ") : "нет", "Здесь вы чувствительны к среде и можете набираться опыта.", true)
      ],
      note: "Символический бодиграф. Настоящий Дизайн человека требует точных планетарных расчётов."
    };
  }

  function calculateHologenetics(d) {
    const seed = d.day + d.month + d.year;
    const items = SPHERES.map((sphere, index) => ({ name: sphere.name, value: reduce9(seed + index * 7), text: sphere.text }));
    const top = items.slice().sort((a, b) => b.value - a.value).slice(0, 4);
    return {
      title: "Сферы хологенетики", subtitle: formatDate(d),
      blocks: top.map((item) => block(item.name, String(item.value), item.text, true)).concat([
        block("Совет", "Выберите одну сферу", "Не пытайтесь развивать всё сразу. Одна сфера, один маленький шаг на этой неделе.", true)
      ]),
      chart: chart("spheres", { items }),
      note: "Хологенетика — авторская система; здесь используется символическая интерпретация, а не оригинальная методология."
    };
  }

  function calculateAstrocart(d) {
    const seed = d.day + d.month + d.year;
    const items = CITIES.map((city) => ({ ...city, score: reduce9(seed + hashString(city.name) % 97) })).sort((a, b) => b.score - a.score);
    const top = items.slice(0, 4);
    return {
      title: "Города силы", subtitle: formatDate(d),
      blocks: [
        block("Главная точка", top[0].name, top[0].country + ". " + (top[0].score >= 8 ? "Сильный фон для вдохновения и нового." : "Мягкая поддержка: хорошо для восстановления."), true),
        block("Второй город", top[1].name, top[1].country + ". " + (top[1].score >= 7 ? "Полезен для обучения и контактов." : "Нейтральный фон: важнее ваши намерения."), true),
        block("Третий город", top[2].name, top[2].country + ". " + (top[2].score >= 6 ? "Хорош для короткой перезагрузки." : "Не ждите чуда: сила места зависит от действия."), true),
        block("Совет", "Проверьте телом", "Представьте себя в городе на 3 дня. Легче ли дышится? Есть ли желание гулять и делать?", true)
      ],
      chart: chart("cities", { items: top }),
      note: "Это символическая карта мест, а не астрологическая астрокартография."
    };
  }

  function calculateArchetypes(d) {
    const a = reduce22(d.day);
    const b = reduce22(d.month);
    const c = reduce22(digitSum(d.year));
    return {
      title: "Три архетипа", subtitle: formatDate(d),
      blocks: [
        block("Суть", arcanaValue(a), arcanaText(a), true),
        block("Сердце", arcanaValue(b), arcanaText(b), true),
        block("Путь", arcanaValue(c), arcanaText(c), true),
        block("Формула", arcana(a).name + " · " + arcana(b).name + " · " + arcana(c).name, "Сочетание этих образов описывает вашу внутреннюю команду.", true)
      ],
      note: "Архетипы — язык образов. Они не определяют вас полностью."
    };
  }

  function calculateDreams(values) {
    const text = String(values.dream || "").toLowerCase();
    const matches = DREAM_SYMBOLS.filter((symbol) => symbol.keys.some((key) => text.includes(key)));
    let selected = matches.slice(0, 4);
    if (!selected.length) {
      const seed = hashString(text);
      selected = [DREAM_SYMBOLS[seed % DREAM_SYMBOLS.length], DREAM_SYMBOLS[(seed + 7) % DREAM_SYMBOLS.length], DREAM_SYMBOLS[(seed + 13) % DREAM_SYMBOLS.length]];
    }
    return {
      title: "Символы сна", subtitle: matches.length ? "Найдено " + matches.length + " знакомых образов" : "Прямых совпадений нет — вот три возможных образа",
      blocks: selected.map((item) => block(item.title, "образ", item.text, true)).concat([
        block("Совет", "Запишите чувство", "Сон важнее расшифровывать через эмоцию, а не через буквальное событие. Что вы почувствовали при пробуждении?", true)
      ]),
      note: "Толкование снов носит символический и развлекательный характер."
    };
  }

  function calculateBook(values) {
    const question = String(values.question || "").trim();
    const seed = hashString(question || "страница");
    const line = FATE_LINES[seed % FATE_LINES.length];
    const page = (seed % 999) + 1;
    return {
      title: "Страница " + page, subtitle: "Вопрос: «" + question + "»",
      blocks: [
        block("Строка книги", line, "Прочитайте её медленно и заметьте первое чувство.", true),
        block("Совет", "Один шаг", "Не ищите весь маршрут. Сделайте одно действие, которое приближает к ответу.", true)
      ],
      note: "Книга судеб — символическая практика, а не предсказание."
    };
  }

  function calculatePastLife(d) {
    const seed = hashString(d.year + "-" + d.month + "-" + d.day + "-past");
    const story = PAST_LIVES[seed % PAST_LIVES.length];
    return {
      title: story.era, subtitle: "Символическая история души",
      blocks: [
        block("Роль", story.role, "в этой истории вы учились быть собой через эту роль.", true),
        block("Урок", "Опыт", story.lesson, true),
        block("Задача", "Сегодня", story.task, true),
        block("Память", "Что отзывается?", "Заметьте, какая деталь истории кажется знакомой. Это может быть ключом к вашему сегодняшнему выбору.", true)
      ],
      note: "Прошлая жизнь здесь — метафора для исследования, а не исторический факт."
    };
  }

  function calculateNames(values, mode) {
    const name = String(values.name || "").trim();
    if (mode === "selection") {
      const birth = parseDate(values.birthDate);
      if (!birth) return null;
      const destiny = reduce9(birth.day + birth.month + birth.year);
      const gender = values.gender || "female";
      const suggestions = NAME_LIST.filter((item) => item.gender === gender)
        .map((item) => ({ ...item, number: nameNumber(item.name) }))
        .sort((a, b) => Math.abs(a.number - destiny) - Math.abs(b.number - destiny))
        .slice(0, 5);
      return {
        title: "Подбор имени", subtitle: "Ориентир — число судьбы " + destiny,
        blocks: suggestions.map((item) => block(item.name, String(item.number), NAME_MEANINGS[item.name.toLowerCase()] || NUMBER_MEANINGS[item.number].text, true)).concat([
          block("Совет", "Слушайте звучание", "Имя должно нравиться вам вслух. Число — только один из ориентиров.", true)
        ]),
        note: "Подбор имени — символическая практика; окончательный выбор всегда за вами."
      };
    }
    const n1 = nameNumber(name);
    const meaning = NAME_MEANINGS[name.toLowerCase()] || (NUMBER_MEANINGS[n1].title + ": " + NUMBER_MEANINGS[n1].text);
    if (mode === "compatibility") {
      const partnerName = String(values.partnerName || "").trim();
      if (!partnerName) return null;
      const n2 = nameNumber(partnerName);
      const score = Math.max(2, 10 - Math.abs(n1 - n2));
      return {
        title: "Совместимость имён", subtitle: name + " + " + partnerName,
        blocks: [
          block(name, String(n1), meaning, true),
          block(partnerName, String(n2), NAME_MEANINGS[partnerName.toLowerCase()] || NUMBER_MEANINGS[n2].text, true),
          block("Совместимость", score + " / 10", score >= 8 ? "лёгкое взаимопонимание и общий ритм." : score >= 5 ? "есть разница, которая делает союз живым." : "разные языки: важно говорить прямо и не додумывать.", true),
          block("Совет", "Слушайте имя", "Как звучит имя партнёра, когда вы его произносите? В этом звуке больше правды, чем в числе.", true)
        ],
        note: "Совместимость имён — символическая игра, а не оценка отношений."
      };
    }
    return {
      title: "Мир имён", subtitle: name,
      blocks: [
        block("Число имени", String(n1), NUMBER_MEANINGS[n1].text, true),
        block("Значение", NUMBER_MEANINGS[n1].title, meaning, true),
        block("Совет", "Произнесите вслух", "Имя — это звук, которым вас зовут. Заметьте, что оно пробуждает в теле.", true)
      ],
      note: "Значение имени здесь символическое и не является словарной этимологией."
    };
  }

  function calculate(practiceId, mode, values) {
    const childMode = (practiceId === "matrix" || practiceId === "pythagoras") && mode === "child";
    const birth = parseDate(childMode ? values.childDate : values.birthDate);
    if (practiceId === "transits") return calculateTransits();
    if (practiceId === "iching") return throwHexagram();
    if (practiceId === "dreams") return calculateDreams(values);
    if (practiceId === "book-of-fate") return calculateBook(values);
    if (practiceId === "names") return calculateNames(values, mode);
    if (!birth) return null;
    switch (practiceId) {
      case "matrix": return calculateMatrix(birth, mode, values);
      case "pythagoras": return calculatePythagoras(birth, mode, values);
      case "fate-graph": return calculateFate(birth, mode, values);
      case "energy": return calculateEnergy(birth, mode, values);
      case "destiny-number": return calculateDestiny(birth, mode, values);
      case "natal": return calculateNatal(birth, mode, values);
      case "bazi": return calculateBazi(birth, values);
      case "human-design": return calculateHumanDesign(birth, values);
      case "hologenetics": return calculateHologenetics(birth);
      case "astrocart": return calculateAstrocart(birth);
      case "three-archetypes": return calculateArchetypes(birth);
      case "past-life": return calculatePastLife(birth);
      default: return null;
    }
  }

  /* ── Отрисовка ──────────────────────────────────────── */
  function practiceById(id) { return PRACTICES.find((p) => p.id === id); }

  function renderCatalog() {
    const root = $("practice-groups");
    if (!root) return;
    root.innerHTML = ["numerology", "astrology", "archetypes"].map((category) => {
      const meta = CATEGORY_META[category];
      const items = PRACTICES.filter((p) => p.category === category);
      return '<div class="practice-group" style="--group-accent:' + meta.accent + '">' +
        '<div class="practice-group-head">' +
          '<h3>' + esc(meta.label) + '</h3>' +
          '<span class="group-line"></span>' +
          '<span class="group-count">' + items.length + ' практик</span>' +
        '</div>' +
        '<p class="practice-group-desc">' + esc(meta.description) + '</p>' +
        '<div class="practice-grid">' + items.map(renderPracticeCard).join("") + '</div>' +
      '</div>';
    }).join("");
    root.querySelectorAll("[data-practice]").forEach((el) => {
      el.addEventListener("click", () => openPractice(el.dataset.practice));
    });
  }

  function renderPracticeCard(practice) {
    return '<button class="practice-card" type="button" data-practice="' + esc(practice.id) + '" style="--accent:' + practice.accent + '" aria-label="Открыть практику ' + esc(practice.title) + '">' +
      '<span class="practice-sigil" aria-hidden="true">' +
        '<span class="practice-star s1">✦</span><span class="practice-star s2">✧</span><span class="practice-star s3">·</span>' +
        '<span class="practice-symbol">' + esc(practice.symbol) + '</span>' +
      '</span>' +
      '<span class="practice-body">' +
        '<span class="practice-title">' + esc(practice.title) + '</span>' +
        '<span class="practice-sub">' + esc(practice.subtitle) + '</span>' +
        '<span class="practice-desc">' + esc(practice.description) + '</span>' +
        '<span class="practice-chips">' + practice.chips.map((chip) => '<span>' + esc(chip) + '</span>').join("") + '</span>' +
      '</span>' +
      '<span class="practice-open" aria-hidden="true">↗</span>' +
    '</button>';
  }

  function openPractice(id) {
    const practice = practiceById(id);
    if (!practice) return;
    state.practice = practice;
    state.mode = practice.modes[0].id;
    state.values = {};
    const modal = $("practice-modal");
    modal.hidden = false;
    modal.innerHTML = '<div class="practice-dialog" role="dialog" aria-modal="true" aria-labelledby="practice-title" style="--accent:' + practice.accent + '">' +
      '<button class="practice-close" type="button" aria-label="Закрыть">✕</button>' +
      '<div class="practice-dialog-head">' +
        '<span class="practice-dialog-symbol" aria-hidden="true">' + esc(practice.symbol) + '</span>' +
        '<div><span class="practice-kicker">' + esc(CATEGORY_META[practice.category].label) + '</span>' +
        '<h2 id="practice-title">' + esc(practice.title) + '</h2><p>' + esc(practice.subtitle) + '</p></div>' +
      '</div>' +
      '<div id="practice-form-root"></div>' +
      '<div id="practice-result" aria-live="polite"></div>' +
    '</div>';
    document.body.style.overflow = "hidden";
    modal.querySelector(".practice-close").addEventListener("click", closePractice);
    modal.addEventListener("click", (event) => { if (event.target === modal) closePractice(); }, { once: true });
    renderPracticeForm();
    document.addEventListener("keydown", onPracticeKeydown);
    const firstInput = modal.querySelector("input, textarea, select");
    if (firstInput) firstInput.focus({ preventScroll: true });
  }

  function closePractice() {
    const modal = $("practice-modal");
    if (!modal) return;
    modal.hidden = true;
    modal.innerHTML = "";
    document.body.style.overflow = "";
    document.removeEventListener("keydown", onPracticeKeydown);
    state.practice = null;
  }

  function onPracticeKeydown(event) {
    if (event.key === "Escape") closePractice();
  }

  function fieldsFor(practice, mode) {
    if (practice.id === "matrix") {
      if (mode === "child") return [F.child];
      if (mode === "couple") return [F.birth, F.partner];
      return [F.birth];
    }
    if (practice.id === "pythagoras") {
      if (mode === "child") return [F.child];
      if (mode === "couple") return [F.birth, F.partner];
      return [F.birth];
    }
    if (practice.id === "fate-graph") return mode === "couple" ? [F.birth, F.partner] : [F.birth];
    if (practice.id === "energy") return mode === "compatibility" ? [F.birth, F.partner] : [F.birth];
    if (practice.id === "destiny-number" && mode === "name") return [F.birth, F.name];
    if (practice.id === "natal") {
      if (mode === "synastry") return [F.birth, F.partner];
      if (mode === "solar") return [F.birth];
      return [F.birth, F.time, F.place];
    }
    if (practice.id === "names") {
      if (mode === "compatibility") return [F.name, F.partnerName];
      if (mode === "selection") return [F.birth, F.gender];
      return [F.name];
    }
    return practice.fields;
  }

  function renderPracticeForm() {
    const practice = state.practice;
    const root = $("practice-form-root");
    if (!practice || !root) return;
    const modes = practice.modes.length > 1 ? '<div class="practice-modes">' + practice.modes.map((mode) => '<button class="practice-mode' + (mode.id === state.mode ? ' active' : '') + '" type="button" data-mode="' + esc(mode.id) + '">' + esc(mode.label) + '</button>').join("") + '</div>' : "";
    const fields = fieldsFor(practice, state.mode).map(renderField).join("");
    root.innerHTML = modes +
      '<form class="practice-form" id="practice-form">' + fields +
        '<div class="practice-actions">' +
          '<button class="practice-submit" type="submit">✦ ' + esc(practice.action) + '</button>' +
          '<button class="practice-reset" type="button">Сбросить</button>' +
        '</div>' +
        '<div class="practice-error" id="practice-error" role="alert"></div>' +
      '</form>';
    root.querySelectorAll(".practice-mode").forEach((button) => {
      button.addEventListener("click", () => {
        state.values = readFormValues();
        state.mode = button.dataset.mode;
        renderPracticeForm();
      });
    });
    const form = $("practice-form");
    form.addEventListener("submit", (event) => {
      event.preventDefault();
      submitPractice();
    });
    root.querySelector(".practice-reset").addEventListener("click", () => {
      state.values = {};
      renderPracticeForm();
      $("practice-result").innerHTML = "";
    });
  }

  function renderField(field) {
    const value = state.values[field.key] || "";
    const wide = field.type === "textarea" ? " full" : "";
    const hint = field.hint ? '<span class="practice-hint">' + esc(field.hint) + '</span>' : "";
    let control;
    if (field.type === "textarea") {
      control = '<textarea id="pf-' + field.key + '" data-key="' + field.key + '" placeholder="' + esc(field.placeholder || "") + '"' + (field.required ? ' required' : '') + '>' + esc(value) + '</textarea>';
    } else if (field.type === "select") {
      control = '<select id="pf-' + field.key + '" data-key="' + field.key + '">' + field.options.map(([optionValue, label]) => '<option value="' + esc(optionValue) + '"' + (value === optionValue ? ' selected' : '') + '>' + esc(label) + '</option>').join("") + '</select>';
    } else {
      control = '<input id="pf-' + field.key + '" data-key="' + field.key + '" type="' + field.type + '" value="' + esc(value) + '" placeholder="' + esc(field.placeholder || "") + '"' + (field.required ? ' required' : '') + (field.type === "date" ? ' max="' + new Date().toISOString().slice(0, 10) + '"' : '') + '>';
    }
    return '<div class="practice-field' + wide + '"><label for="pf-' + field.key + '">' + esc(field.label) + '</label>' + control + hint + '</div>';
  }

  function readFormValues() {
    const values = {};
    document.querySelectorAll("#practice-form [data-key]").forEach((input) => { values[input.dataset.key] = input.value; });
    return values;
  }

  function submitPractice() {
    const practice = state.practice;
    const error = $("practice-error");
    if (!practice || !error) return;
    const values = readFormValues();
    state.values = values;
    const fields = fieldsFor(practice, state.mode);
    for (const field of fields) {
      if (field.required && !String(values[field.key] || "").trim()) {
        error.textContent = "Заполните поле «" + field.label + "».";
        return;
      }
    }
    if (fields.some((field) => field.type === "date")) {
      for (const field of fields.filter((item) => item.type === "date")) {
        if (!parseDate(values[field.key])) { error.textContent = "Проверьте дату в формате ДД.ММ.ГГГГ."; return; }
      }
    }
    if (practice.id === "dreams" && String(values.dream || "").trim().length < 5) {
      error.textContent = "Опишите сон чуть подробнее (минимум 5 символов).";
      return;
    }
    if (practice.id === "book-of-fate" && String(values.question || "").trim().length < 3) {
      error.textContent = "Сформулируйте вопрос (минимум 3 символа).";
      return;
    }
    error.textContent = "";
    const result = calculate(practice.id, state.mode, values);
    if (!result) {
      error.textContent = "Не удалось рассчитать. Проверьте введённые данные.";
      return;
    }
    $("practice-result").innerHTML = renderResult(result, practice.accent);
    const dialog = document.querySelector(".practice-dialog");
    if (dialog) dialog.scrollTo({ top: dialog.scrollHeight, behavior: "smooth" });
  }

  function renderResult(result, accent) {
    const blocks = result.blocks.map((item) =>
      '<div class="result-block' + (item.wide ? ' wide' : '') + '">' +
        '<div class="result-label">' + esc(item.label) + '</div>' +
        '<div class="result-value">' + esc(item.value) + '</div>' +
        (item.text ? '<div class="result-text">' + esc(item.text) + '</div>' : '') +
      '</div>'
    ).join("");
    return '<div class="practice-result" style="--accent:' + accent + '">' +
      '<div class="practice-result-head"><span class="practice-kicker">Результат</span><h3>' + esc(result.title) + '</h3><p>' + esc(result.subtitle) + '</p></div>' +
      (result.chart ? '<div class="practice-chart">' + renderChart(result.chart) + '</div>' : '') +
      '<div class="practice-result-grid">' + blocks + '</div>' +
      (result.note ? '<div class="practice-note">' + esc(result.note) + '</div>' : '') +
    '</div>';
  }

  function renderChart(chart) {
    if (chart.type === "fate") return renderFateChart(chart);
    if (chart.type === "pythagoras") return renderPythagoras(chart);
    if (chart.type === "hexagram") return renderHexagram(chart);
    if (chart.type === "pillars") return renderPillars(chart);
    if (chart.type === "cities") return renderCities(chart);
    if (chart.type === "spheres") return renderSpheres(chart);
    return "";
  }

  function renderFateChart(data) {
    const width = 340, height = 150, padX = 22, padY = 18;
    const points = data.points;
    const all = data.partnerPoints ? points.concat(data.partnerPoints) : points;
    const max = Math.max(22, ...all);
    const coords = (values) => values.map((value, index) => {
      const x = padX + index * ((width - padX * 2) / 6);
      const y = height - padY - (value / max) * (height - padY * 2);
      return { x, y };
    });
    const main = coords(points);
    const second = data.partnerPoints ? coords(data.partnerPoints) : null;
    const line = (items) => items.map((p, i) => (i ? "L" : "M") + p.x.toFixed(1) + " " + p.y.toFixed(1)).join(" ");
    const dots = (items, className) => items.map((p) => '<circle class="fate-dot ' + className + '" cx="' + p.x.toFixed(1) + '" cy="' + p.y.toFixed(1) + '" r="3.2"></circle>').join("");
    const labels = data.labels.map((label, index) => '<text class="fate-label" x="' + (padX + index * ((width - padX * 2) / 6)).toFixed(1) + '" y="' + (height - 3) + '" text-anchor="middle">' + esc(label) + '</text>').join("");
    return '<svg class="fate-chart" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="График судьбы">' +
      [0, 1, 2, 3].map((i) => '<line class="grid-line" x1="' + padX + '" y1="' + (padY + i * ((height - padY * 2) / 3)) + '" x2="' + (width - padX) + '" y2="' + (padY + i * ((height - padY * 2) / 3)) + '"></line>').join("") +
      (second ? '<path class="fate-line secondary" d="' + line(second) + '"></path>' + dots(second, "secondary") : "") +
      '<path class="fate-line" d="' + line(main) + '"></path>' + dots(main, "") + labels +
    '</svg>';
  }

  function renderPythagoras(data) {
    return '<div class="pythagoras-grid">' + data.cells.map((cell) =>
      '<div class="pyth-cell' + (cell.count ? ' has' : '') + '"><span class="pyth-digit">' + cell.digit + '</span><span class="pyth-count">' + (cell.count ? '×' + cell.count : '·') + '</span></div>'
    ).join("") + '</div>';
  }

  function renderHexagram(data) {
    return '<div class="hexagram">' + data.lines.map((line) =>
      '<div class="hex-line' + (line.changing ? ' changing' : '') + '">' +
        (line.yang ? '<span class="hex-bar full"></span>' : '<span class="hex-bar half"></span><span class="hex-gap"></span><span class="hex-bar half"></span>') +
        '<span class="hex-mark">' + (line.changing ? '○' : '') + '</span>' +
      '</div>'
    ).join("") + '</div>';
  }

  function renderPillars(data) {
    return '<div class="pillars">' + data.pillars.map((pillar) =>
      '<div class="pillar"><small>' + esc(pillar.label) + '</small><b>' + esc(pillar.stem.name) + ' ' + esc(pillar.branch.name) + '</b><span>' + esc(pillar.stem.element) + ' · ' + esc(pillar.branch.animal) + '</span></div>'
    ).join("") + '</div>';
  }

  function renderCities(data) {
    return '<div class="city-list">' + data.items.map((city) =>
      '<div class="city-row"><b>' + esc(city.name) + '</b><span class="city-bar"><i style="width:' + (city.score * 10) + '%"></i></span><span>' + city.score + '/10</span></div>'
    ).join("") + '</div>';
  }

  function renderSpheres(data) {
    return '<div class="sphere-grid">' + data.items.map((item) =>
      '<div class="sphere"><b>' + esc(item.name) + '</b><span>' + item.value + '</span></div>'
    ).join("") + '</div>';
  }

  /* ── Запуск ─────────────────────────────────────────── */
  function init() {
    renderCatalog();
    const modal = $("practice-modal");
    if (modal) modal.hidden = true;
  }

  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
  else init();
})();