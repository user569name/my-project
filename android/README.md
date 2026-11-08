# Mystic Tarot — Android

Клиент для сервиса раскладов Таро: Kotlin, Jetpack Compose (Material 3), Navigation Compose, ViewModel + StateFlow, Retrofit/OkHttp/Gson. Один Activity, ручное внедрение зависимостей.

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

### Адрес API

По умолчанию используется `http://10.0.2.2:8000/` — так эмулятор обращается к `localhost` компьютера. Для физического устройства нужен IP компьютера в той же сети:

```bash
./gradlew assembleDebug -PapiBaseUrl=http://192.168.1.10:8000/
```

Свойство попадает в `BuildConfig.API_BASE_URL` при сборке. Backend должен быть запущен и слушать `0.0.0.0:8000` (Docker Compose делает это по умолчанию).

### Если эмулятор не подключается к `10.0.2.2`

На некоторых конфигурациях Windows (WHPX-гипервизор + Docker Desktop) трафик эмулятора к `10.0.2.2:8000` не проходит — OkHttp падает с `SocketTimeoutException: failed to connect to /10.0.2.2 (port 8000)`. Признак: `curl`/браузер на самом ПК к `localhost:8000` работают, а запросы приложения в backend-логи не приходят.

Надёжное обходное решение — `adb reverse`: приложение обращается к `127.0.0.1:8000` внутри эмулятора, а adb заворачивает этот порт напрямую в порт хоста:

```bash
# один раз перед запуском приложения (после каждого перезапуска эмулятора)
adb reverse tcp:8000 tcp:8000

# и собрать APK с локальным адресом
./gradlew assembleDebug -PapiBaseUrl=http://127.0.0.1:8000/
```

Backend при этом продолжает слушать `0.0.0.0:8000`.

В манифесте включён `android:usesCleartextTraffic="true"` для HTTP при локальной разработке. Для production используйте HTTPS и отключите cleartext.

## Структура

```text
app/src/main/java/com/example/tarot/
├── TarotApplication.kt        # создаёт AppContainer
├── MainActivity.kt            # setContent { AppRoot() }
├── data/
│   ├── AppContainer.kt        # ручной DI: TokenStore + Retrofit + Repository
│   ├── TokenStore.kt          # SharedPreferences с JWT
│   ├── TarotRepository.kt     # операции API
│   └── remote/
│       ├── ApiClient.kt       # Retrofit/OkHttp
│       ├── AuthInterceptor.kt # Bearer-токен
│       ├── ApiModels.kt       # DTO с @SerializedName
│       ├── ApiErrors.kt       # русские сообщения об ошибках
│       └── TarotApi.kt        # endpoints
├── domain/SpreadOption.kt     # single / three / celtic
├── ui/
│   ├── AppRoot.kt             # Scaffold + нижняя панель
│   ├── navigation/AppNavHost.kt
│   ├── auth/                  # вход и регистрация
│   ├── home/                  # вопрос и новый расклад
│   ├── history/               # история и детали
│   ├── components/            # общие composable
│   └── theme/                 # тёмная тема
└── util/DateFormatter.kt
```

### Как устроен поток данных

1. `TarotApplication` создаёт `AppContainer`.
2. `AppContainer` собирает `TokenStore`, `ApiClient` и `TarotRepository`.
3. Экран получает ViewModel через фабрику, ViewModel вызывает repository.
4. Repository обращается к Retrofit; `AuthInterceptor` подставляет JWT.
5. ViewModel обновляет `StateFlow`, Compose перерисовывает состояние.
6. `ApiErrors.toUserMessage()` превращает `HttpException`/`IOException` в понятный текст.

Токен хранится в `SharedPreferences` и переживает перезапуск приложения. Выход очищает токен и возвращает на экран входа.

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
| Приложение не подключается к API | Проверьте `10.0.2.2` для эмулятора, IP для устройства, `usesCleartextTraffic` и запущенный backend. Если запросы так и не доходят до backend, используйте `adb reverse tcp:8000 tcp:8000` и пересоберите с `-PapiBaseUrl=http://127.0.0.1:8000/` |
| `HTTP 401` | Токен истёк/недействителен — выйдите и войдите снова |
| `HTTP 409` при регистрации | Email уже занят |
| `HTTP 422` | Серверная валидация: вопрос 3–500 символов, пароль ≥ 8, имя ≥ 2 |

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
