# Как участвовать в разработке

Спасибо за интерес к Mystic Tarot! Ниже — требования, локальная настройка и правила, которые помогут быстро внести изменения и не сломать CI.

## 1. Требования

| Инструмент | Версия | Зачем |
| --- | --- | --- |
| Python | 3.11+ (CI — 3.12) | Backend |
| Docker Desktop / Docker Engine + Compose | любая актуальная | PostgreSQL и контейнеры |
| JDK | 21 | Сборка Android (CI использует Temurin 21) |
| Android Studio | актуальная | Эмулятор, SDK, IDE |
| Android SDK Platform | 37.0 (`platforms;android-37.0`) | `compileSdk = 37` |
| Android SDK Build-Tools | 36.0.0 | AGP 9.5.1 |
| Git | любая | Работа с репозиторием |

Проверка Java:

```bash
java -version
```

Если системная Java сломана или имеет неподходящую версию, задайте `JAVA_HOME` на JDK 21. В Android Studio это встроенный JBR, но для CLI надёжнее отдельный JDK 21.

## 2. Быстрый старт

### 2.1. Клонирование и переменные окружения

```bash
git clone <repo-url>
cd my-project

cp .env.example .env
```

Для Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

В `.env` задайте собственный `SECRET_KEY` (не короче 16 символов) и пароль PostgreSQL. Файл `.env` не коммитится.

### 2.2. Backend

```bash
cd backend
python -m venv .venv
# Linux/macOS:
source .venv/bin/activate
# Windows PowerShell:
# .\.venv\Scripts\Activate.ps1

pip install -r requirements-dev.txt
```

Вариант A — всё в Docker:

```bash
# из корня репозитория
docker compose up --build -d
curl http://localhost:8000/health
```

Вариант B — PostgreSQL в Docker, приложение локально:

```bash
# из корня репозитория
docker compose up -d db

cd backend
cp .env.example .env
# DATABASE_URL должен указывать на localhost:5433
alembic upgrade head
uvicorn app.main:app --reload
```

Swagger UI: http://localhost:8000/docs.

### 2.3. Android

```bash
cd android
./gradlew testDebugUnitTest lintDebug assembleDebug
```

На Windows:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

APK: `android/app/build/outputs/apk/debug/app-debug.apk`.

Если SDK не найден, создайте `android/local.properties`:

```properties
sdk.dir=C\:\\Users\\<user>\\AppData\\Local\\Android\\Sdk
```

Установка нужных пакетов SDK:

```bash
sdkmanager "platforms;android-37.0" "build-tools;36.0.0" "platform-tools"
```

В новых cmdline-tools команда `sdkmanager` перенаправляется в Android CLI. Эквивалент:

```bash
android sdk install "platforms/android-37.0" "build-tools/36.0.0" "platform-tools"
```

## 3. Команды проверки

### Backend

```bash
cd backend
ruff check .
ruff format --check .
pytest -q
```

Автоисправление форматирования:

```bash
ruff check . --fix
ruff format .
```

Тесты по умолчанию используют SQLite in-memory. Чтобы прогнать их на PostgreSQL, как в CI:

```bash
# PostgreSQL должен быть доступен
export TEST_DATABASE_URL=postgresql+asyncpg://tarot:tarot@localhost:5433/tarot_test
pytest -q
```

В PowerShell:

```powershell
$env:TEST_DATABASE_URL = "postgresql+asyncpg://tarot:tarot@localhost:5433/tarot_test"
.\.venv\Scripts\python.exe -m pytest -q
```

### Android

```bash
cd android
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Release-сборка (без секретов создастся unsigned APK):

```bash
./gradlew assembleRelease
```

## 4. Миграции базы данных

Схема управляется Alembic. После изменения моделей:

```bash
cd backend
alembic revision --autogenerate -m "add reading note"
```

Обязательно просмотрите сгенерированный файл в `migrations/versions/`:

- проверьте имена ограничений и индексов;
- убедитесь, что `downgrade()` действительно откатывает изменение;
- не редактируйте уже применённые миграции в общей ветке.

Применить и проверить откат:

```bash
alembic upgrade head
alembic downgrade -1
alembic upgrade head
```

В CI дополнительно выполняется `alembic upgrade head` и `alembic downgrade base` на чистой PostgreSQL.

## 5. Стиль кода

### Python

- `ruff check` и `ruff format` обязательны; конфигурация — в `backend/pyproject.toml`.
- Длина строки — 100 символов.
- Используются современные аннотации (`str | None`), async/await, SQLAlchemy 2.
- Роутеры — тонкие; бизнес-логика — в `app/services/`.
- Ошибки для пользователя — на русском, через `HTTPException(detail=...)`.
- Новые DTO — Pydantic-модели в `app/schemas/`, не словари.

### Kotlin / Compose

- Пакет `com.example.tarot`; имена файлов — по основному composable/классу.
- Состояние экрана — `data class ...UiState` + `MutableStateFlow` + `collectAsStateWithLifecycle()`.
- Сетевые вызовы — только из ViewModel через `TarotRepository`.
- Новые строки интерфейса — на русском; тексты лучше выносить в `strings.xml`, если они переиспользуются.
- Не добавляйте DI-фреймворк и лишние абстракции без необходимости: проект сознательно использует ручной `AppContainer`.
- Соблюдайте существующее форматирование (4 пробела, trailing commas в аргументах).
- Для новых экранов добавляйте маршрут в `Routes` и `AppNavHost`.

### Общее

- Не коммитьте `.env`, `local.properties`, keystore, `build/`, `.gradle/`, `.venv/`.
- Не меняйте версии AGP/Gradle/Compose BOM без проверки совместимости: AGP 9.5.1 требует Gradle ≥ 9.6, а Compose BOM 2026.09.00 — `compileSdk 37`.
- Для Android SDK используйте пакет `platforms;android-37.0`, а не устаревший `platforms;android-37`.

## 6. Тесты

### Backend

- Фикстуры — в `tests/conftest.py`.
- Каждый тест получает чистую схему БД.
- Для защищённых ручек используйте фикстуру `auth_headers`.
- Проверяйте не только успешный сценарий, но и `401/404/409/422`.
- Новую бизнес-логику раскладов покрывайте тестами в `tests/test_tarot.py`.

### Android

- Unit-тесты — `android/app/src/test/java/...`.
- Тестируйте чистые функции и DTO без Android-фреймворка.
- Для UI-тестов можно добавить `androidTest`, но они не входят в обязательный CI.

## 7. Git и pull request

Рекомендуемый процесс:

1. Создайте ветку от `main`: `feature/...`, `fix/...`, `docs/...`.
2. Делайте небольшие логические коммиты.
3. Сообщения коммитов — в повелительном наклонении, на русском или английском, например:
   - `add reading detail screen`
   - `исправить расчёт перевёрнутых карт`
4. Перед PR выполните локальные проверки:

```bash
# backend
cd backend
ruff check . && ruff format --check . && pytest -q

# android
cd ../android
./gradlew testDebugUnitTest lintDebug assembleDebug
```

5. В описании PR укажите:
   - что изменено и зачем;
   - как проверяли;
   - скриншоты для UI-изменений;
   - нужны ли миграции или новые переменные окружения.

CI должен быть зелёным. PR, меняющий API, должен обновлять README/ARCHITECTURE и, при необходимости, Android-клиент.

## 8. Release и подпись APK

Релиз запускается тегом `v*`:

```bash
git tag v1.0.0
git push origin v1.0.0
```

Workflow `release.yml`:

- собирает и публикует backend-образ в GHCR;
- собирает debug APK;
- собирает подписанный release APK, если настроены секреты;
- прикладывает APK к GitHub Release;
- опционально деплоит на сервер по SSH.

### Создание keystore

```bash
keytool -genkeypair -v \
  -keystore release-keystore.jks \
  -alias mystic-tarot \
  -keyalg RSA -keysize 2048 -validity 10000
```

Base64 для секрета `ANDROID_KEYSTORE_BASE64`:

```bash
# Linux/macOS
base64 -w 0 release-keystore.jks > keystore.b64
# macOS без -w:
base64 -i release-keystore.jks -o keystore.b64
```

PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Content keystore.b64
```

Секреты GitHub Actions:

| Секрет | Значение |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | содержимое `keystore.b64` |
| `ANDROID_KEYSTORE_PASSWORD` | пароль хранилища |
| `ANDROID_KEY_ALIAS` | alias ключа |
| `ANDROID_KEY_PASSWORD` | пароль ключа |

Никогда не коммитьте keystore и пароли. Локальная сборка подписанного release:

```bash
cd android
export ANDROID_KEYSTORE_PATH=/absolute/path/release-keystore.jks
export ANDROID_KEYSTORE_PASSWORD=...
export ANDROID_KEY_ALIAS=...
export ANDROID_KEY_PASSWORD=...
./gradlew assembleRelease
```

Без переменных окружения `assembleRelease` создаёт `app-release-unsigned.apk`; такой APK не подходит для публикации.

## 9. Частые проблемы

| Симптом | Решение |
| --- | --- |
| `Package platforms not found` при установке SDK | Используйте `platforms;android-37.0` или `android sdk install platforms/android-37.0` |
| `SDK location not found` | Создайте `android/local.properties` с `sdk.dir` |
| Gradle требует другую Java | Задайте `JAVA_HOME` на JDK 21; проверьте `./gradlew -version` |
| `docker compose` падает с ошибкой PostgreSQL 18 про `/var/lib/postgresql/data` | Volume должен монтироваться в `/var/lib/postgresql`; при переходе со старого тома удалите его (`docker compose down -v`) |
| Порт 5432 занят | В `.env` оставьте `POSTGRES_PORT=5433` или задайте свободный порт |
| Android не видит API | Для эмулятора — `http://10.0.2.2:8000/`, для устройства — IP компьютера; проверьте, что backend слушает `0.0.0.0:8000` |
| `401` после перезапуска приложения | Токен истёк или изменился `SECRET_KEY`; выполните выход и войдите снова |
| CI не находит Android SDK 37 | Проверьте шаг установки пакетов в workflow и доступность `android`/`sdkmanager` |

## 10. Лицензия и контакты

Проект учебный/демонстрационный; см. раздел «Лицензия» в README. По вопросам архитектуры и релизов создавайте issue или PR.
