# Mystic Tarot — Android

Автономный клиент для раскладов Таро: Kotlin, Jetpack Compose (Material 3), Navigation Compose, ViewModel + StateFlow, Gson, SharedPreferences. Один Activity, ручное внедрение зависимостей. Сервер и интернет не нужны.

## Требования

| Компонент | Версия |
| --- | --- |
| JDK | 21 |
| Gradle | 9.8.1 (wrapper в репозитории) |
| Android Gradle Plugin | 9.5.1 |
| Kotlin | 2.4.21 (встроенный Kotlin AGP + Compose compiler plugin) |
| compileSdk / targetSdk | 37 |
| minSdk | 26 |
| Android SDK Platform | `platforms;android-37.0` |
| Build-Tools | `36.0.0` |

Проверить Java:

```bash
java -version
```

Gradle использует `JAVA_HOME`. Если команда `java` указывает на старую или сломанную установку, задайте JDK 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
```

```bash
export JAVA_HOME=/path/to/jdk-21
```

## Настройка SDK

Android Studio обычно создаёт `local.properties` автоматически. Если файла нет:

```properties
sdk.dir=C\:\\Users\\<user>\\AppData\\Local\\Android\\Sdk
```

Установка пакетов через классический sdkmanager:

```bash
sdkmanager "platforms;android-37.0" "build-tools;36.0.0" "platform-tools"
```

Новые cmdline-tools перенаправляют `sdkmanager` в Android CLI. Эквивалентная команда:

```bash
android sdk install "platforms/android-37.0" "build-tools/36.0.0" "platform-tools"
```

> Важно: пакет называется `platforms;android-37.0` (с `.0`), а не `platforms;android-37`. Старое имя в актуальном репозитории SDK отсутствует.

## Сборка и запуск

Из каталога `android`:

```bash
# Windows
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug

# Linux/macOS
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Готовый APK:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

Установка на подключённое устройство/эмулятор:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Автономный режим

Приложение не обращается к API: колода читается из `app/src/main/assets/cards.json`, история раскладов хранится в `SharedPreferences`. Вход и регистрация не используются; главный экран открывается сразу.

Проверить работу без сети можно в авиарежиме эмулятора:

```bash
adb shell cmd connectivity airplane-mode enable
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm clear com.example.tarot
adb shell am start -n com.example.tarot/.MainActivity
```

> Для локальной разработки в манифесте может оставаться `android:usesCleartextTraffic="true"`; приложение всё равно не делает сетевых запросов.

## Структура

```text
app/src/main/
├── assets/cards.json           # локальная колода из 78 карт
└── java/com/example/tarot/
    ├── TarotApplication.kt     # создаёт AppContainer
    ├── MainActivity.kt         # setContent { AppRoot() }
    ├── data/
    │   ├── AppContainer.kt     # ручной DI: Repository
    │   ├── TarotRepository.kt  # локальная колода, draw и история
    │   └── remote/ApiModels.kt # DTO, общие с backend-контрактом
    ├── domain/SpreadOption.kt  # 12 раскладов, категории и позиции
    ├── ui/
    │   ├── AppRoot.kt          # Scaffold + нижняя панель
    │   ├── navigation/AppNavHost.kt
    │   ├── home/               # главный экран-хаб
    │   ├── spreads/            # каталог с поиском и фильтрами
    │   ├── reading/            # экран гадания по выбранной схеме
    │   ├── history/            # история и детали
    │   ├── components/         # общие composable
    │   └── theme/              # тёмная тема
    └── util/DateFormatter.kt
```

### Как устроен поток данных

1. `TarotApplication` создаёт `AppContainer`.
2. `AppContainer` создаёт локальный `TarotRepository`.
3. Экран получает ViewModel через фабрику, ViewModel вызывает repository.
4. Repository читает `assets/cards.json`, перемешивает колоду и выбирает карты.
5. Результат сохраняется в `SharedPreferences` и возвращается как `ReadingDto`.
6. ViewModel обновляет `StateFlow`, Compose перерисовывает состояние.

История ограничена 100 раскладами; перевёрнутые карты выпадают с вероятностью 30%.

## Тесты и линт

```bash
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

- Unit-тесты: `app/src/test/java/com/example/tarot/ApiModelsTest.kt` — разбор snake_case JSON, сортировка карт, контракт раскладов.
- Lint-отчёт: `app/build/reports/lint-results-debug.html`.
- Отчёт тестов: `app/build/reports/tests/testDebugUnitTest/index.html`.

## Release-сборка

Без keystore:

```bash
.\gradlew.bat assembleRelease
```

Результат — `app/build/outputs/apk/release/app-release-unsigned.apk`. Такой APK нельзя публиковать.

С подписью (переменные окружения читает `app/build.gradle.kts`):

```bash
export ANDROID_KEYSTORE_PATH=/absolute/path/release-keystore.jks
export ANDROID_KEYSTORE_PASSWORD=...
export ANDROID_KEY_ALIAS=...
export ANDROID_KEY_PASSWORD=...
./gradlew assembleRelease
```

PowerShell:

```powershell
$env:ANDROID_KEYSTORE_PATH = "C:\keys\release-keystore.jks"
$env:ANDROID_KEYSTORE_PASSWORD = "..."
$env:ANDROID_KEY_ALIAS = "mystic-tarot"
$env:ANDROID_KEY_PASSWORD = "..."
.\gradlew.bat assembleRelease
```

Создание keystore:

```bash
keytool -genkeypair -v -keystore release-keystore.jks \
  -alias mystic-tarot -keyalg RSA -keysize 2048 -validity 10000
```

В CI/CD подпись настраивается секретами `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`. Если keystore не задан, release-шаг пропускается, а к GitHub Release прикладывается только debug APK.

## Частые проблемы

| Проблема | Решение |
| --- | --- |
| `SDK location not found` | Создайте `local.properties` с корректным `sdk.dir` |
| `Package platforms not found` | Используйте `platforms;android-37.0` или `android sdk install platforms/android-37.0` |
| `Unsupported class file major version` | Задайте `JAVA_HOME` на JDK 21 |
| `NavigationBarItem` не найден | Он является extension-функцией `RowScope`; вызывайте внутри `NavigationBar { ... }` или `RowScope` |
| История пустая после переустановки | `pm clear` или удаление приложения очищает `SharedPreferences`; это ожидаемо |
| Ошибка Kotlin-кэша после `clean` | Остановите демон `gradlew --stop`, удалите `app/build/kotlin` и повторите сборку |

## Полезные команды

```bash
# Полная очистка сборки
.\gradlew.bat clean

# Список задач
.\gradlew.bat tasks

# Зависимости приложения
.\gradlew.bat :app:dependencies --configuration debugRuntimeClasspath

# Проверка подписи APK
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```
