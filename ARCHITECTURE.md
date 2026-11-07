# Архитектура Mystic Tarot

Документ описывает устройство проекта, потоки данных и принятые технические решения. Он дополняет [README.md](README.md), где приведены команды запуска.

## 1. Общая схема

```text
┌──────────────────────┐        HTTPS/HTTP         ┌────────────────────────────┐
│  Android (Kotlin)    │  ───────────────────────▶ │  FastAPI (Python 3.12)     │
│  Jetpack Compose     │   JSON + Bearer JWT       │  /api/v1                   │
│  Retrofit / OkHttp   │  ◀─────────────────────── │  Pydantic-схемы            │
└──────────────────────┘                           └─────────────┬──────────────┘
                                                                 │ SQLAlchemy 2 async
                                                                 │ asyncpg
                                                   ┌─────────────▼──────────────┐
                                                   │  PostgreSQL 18             │
                                                   │  users / tarot_cards /     │
                                                   │  readings / reading_cards  │
                                                   └────────────────────────────┘
```

Проект состоит из двух независимых приложений:

| Компонент | Каталог | Назначение |
| --- | --- | --- |
| Backend | `backend/` | REST API, аутентификация, генерация раскладов, история |
| Android | `android/` | Мобильный клиент для платформы Android |
| Инфраструктура | корень репозитория | Docker Compose, CI/CD, документация |

## 2. Backend

### 2.1. Слои

```text
app/
├── main.py           # create_app(), lifespan, /health, подключение роутеров
├── api/
│   ├── deps.py       # зависимость get_current_user (Bearer JWT)
│   └── v1/
│       ├── auth.py   # регистрация, вход, /me, OAuth2 form-вход
│       └── tarot.py  # колода, расклады, история
├── core/
│   ├── config.py     # настройки из переменных окружения / .env
│   ├── security.py   # Argon2-хеширование и JWT
│   └── spreads.py    # определения раскладов и подписи позиций
├── db/
│   ├── base.py       # Declarative Base
│   ├── session.py    # async engine, SessionLocal, init_db()
│   ├── seed.py       # идемпотентный сидинг 78 карт
│   └── models/       # User, TarotCard, Reading, ReadingCard
├── schemas/          # Pydantic-модели запросов и ответов
└── services/
    └── tarot.py      # бизнес-логика выбора карт и чтения истории
```

Роутеры отвечают только за HTTP-контракт, а операции с колодой и историей вынесены в `services/tarot.py`. Доступ к БД передаётся через зависимость `get_db`, которая создаёт `AsyncSession` на запрос.

### 2.2. Жизненный цикл приложения

1. `create_app()` собирает FastAPI, подключает CORS и GZip, регистрирует роутеры с префиксом `/api/v1`.
2. При старте `lifespan` вызывает `init_db()`:
   - при `AUTO_CREATE_TABLES=true` создаёт таблицы через `Base.metadata.create_all` (удобно для локальной разработки и тестов);
   - при `SEED_DATABASE=true` идемпотентно добавляет 78 карт, если колода ещё пуста.
3. При остановке корректно освобождает пул соединений (`engine.dispose()`).

В Docker миграции применяются раньше приложения: `docker-entrypoint.sh` выполняет `alembic upgrade head` с ретраями, затем запускает Uvicorn. То есть production-схема всегда создаётся миграциями, а `create_all` остаётся страховкой для локального режима.

### 2.3. Модель данных

```mermaid
erDiagram
    USERS ||--o{ READINGS : "владеет"
    READINGS ||--|{ READING_CARDS : "содержит"
    TAROT_CARDS ||--o{ READING_CARDS : "выпадает в"

    USERS {
        uuid id PK
        string email UK
        string display_name
        string hashed_password
        datetime created_at
    }
    TAROT_CARDS {
        int id PK
        string slug UK
        string name
        string name_ru
        string arcana
        string suit
        int number
        string symbol
        json keywords_upright
        json keywords_reversed
        text meaning_upright
        text meaning_reversed
    }
    READINGS {
        uuid id PK
        uuid user_id FK
        string question
        string spread
        datetime created_at
    }
    READING_CARDS {
        int id PK
        uuid reading_id FK
        int card_id FK
        int position
        string position_name
        bool reversed
    }
```

Ключевые решения:

- **UUID** для пользователей и раскладов — идентификаторы не раскрывают объём данных и удобны для клиентов.
- **JSON-поля** для ключевых слов карт: список коротких строк не требует отдельной таблицы.
- **`reading_cards`** хранит снимок позиции (`position_name`) и ориентацию карты. Поэтому старые расклады остаются читаемыми, даже если описание позиции в коде изменится.
- **Каскадное удаление**: удаление пользователя удаляет его расклады, удаление расклада — его карты. Удалить карту из колоды, пока она используется в раскладах, нельзя (`RESTRICT`).
- **Индексы** по `email`, `created_at`, внешним ключам и уникальность `(reading_id, position)`.

### 2.4. Аутентификация

```text
POST /api/v1/auth/register или /login
        │
        ▼
Pydantic валидация ──▶ Argon2-хеш пароля (pwdlib)
        │
        ▼
JWT HS256: sub=user.id, iat, exp (7 дней), type=access
        │
        ▼
Клиент сохраняет access_token и отправляет Authorization: Bearer <token>
        │
        ▼
get_current_user: декодирует JWT, проверяет подпись/срок/type,
преобразует sub в UUID и загружает пользователя из БД
```

Пароли никогда не возвращаются в API. Ошибки входа намеренно не различают «неверный email» и «неверный пароль». `SECRET_KEY` обязан быть не короче 16 символов; для production используется длинный случайный секрет.

### 2.5. Поток расклада

1. `POST /api/v1/tarot/draw` принимает `question` (3–500 символов) и `spread` (`single`, `three`, `celtic`).
2. Сервис проверяет наличие всех карт колоды.
3. `random.SystemRandom().sample()` выбирает уникальные карты без повторов; каждая с вероятностью 30 % становится перевёрнутой.
4. В одной транзакции создаются `Reading` и `ReadingCard` для каждой позиции.
5. Ответ собирается через `selectinload` и содержит карту, позицию и ориентацию.
6. `GET /tarot/readings` возвращает только расклады текущего пользователя с сортировкой от новых к старым; `GET/DELETE /tarot/readings/{id}` дополнительно фильтруют по `user_id`, поэтому чужие расклады дают `404`.

Расклады определены в `app/core/spreads.py`:

| Ключ | Название | Карт |
| --- | --- | --- |
| `single` | Одна карта | 1 |
| `three` | Три карты | 3 |
| `celtic` | Кельтский крест | 10 |

### 2.6. Ошибки и контракты

- Ошибки бизнес-логики — `HTTPException` с русским `detail`.
- Валидация Pydantic возвращает стандартный `422` с массивом ошибок.
- `GET /health` проверяет `SELECT 1` и возвращает `503`, если БД недоступна.
- Схемы ответов описаны Pydantic-моделями, поэтому OpenAPI и Swagger UI генерируются автоматически.

## 3. Android

### 3.1. Слои и внедрение зависимостей

```text
TarotApplication
└── AppContainer (ручной DI)
    ├── TokenStore (SharedPreferences)
    ├── OkHttp + AuthInterceptor + logging
    ├── Retrofit + Gson ──▶ TarotApi
    └── TarotRepository
            │
            ▼
     ViewModel (StateFlow) ──▶ Compose-экран
```

DI намеренно ручной: для проекта такого размера библиотека DI добавила бы больше конфигурации, чем экономии. `AppContainer` создаёт единственный экземпляр `TarotRepository`, а ViewModel-фабрики получают его явно.

### 3.2. Структура клиента

| Пакет | Содержимое |
| --- | --- |
| `data` | `TarotRepository`, `TokenStore`, `AppContainer` |
| `data.remote` | Retrofit-интерфейс, DTO, interceptors, разбор ошибок |
| `domain` | `SpreadOption` — UI-описания раскладов |
| `ui.auth` | Вход и регистрация |
| `ui.home` | Вопрос, выбор расклада, результат |
| `ui.history` | Список раскладов, детали, удаление |
| `ui.navigation` | Маршруты и `NavHost` |
| `ui.components` | Общие composable-компоненты |
| `ui.theme` | Тёмная тема, цвета, типографика |
| `util` | Форматирование дат |

### 3.3. Сетевой слой

- `ApiClient` собирает Retrofit с базовым URL из `BuildConfig.API_BASE_URL`.
- `AuthInterceptor` добавляет `Authorization: Bearer`, если токен сохранён.
- В debug-сборке включён `HttpLoggingInterceptor.Level.BODY`; в release логирование выключено.
- `ApiErrors.kt` преобразует `HttpException`/`IOException` в понятные русские сообщения и разбирает `detail` из FastAPI.
- Таймауты: 15 секунд на соединение, 30 секунд на чтение и запись.

### 3.4. Состояние и навигация

- Каждый экран имеет ViewModel с `MutableStateFlow<UiState>`; UI подписывается через `collectAsStateWithLifecycle()`.
- Сетевые вызовы выполняются в `viewModelScope`; ошибки не выбрасываются в UI-поток, а попадают в состояние.
- `AppNavHost` выбирает стартовый экран по наличию токена: `auth` или `home`.
- Нижняя панель показывается только на `home` и `history`; детали расклада открываются отдельным маршрутом `reading/{readingId}`.
- После успешного входа стек очищается, чтобы кнопка «Назад» не возвращала на экран логина.

### 3.5. Конфигурация API

| Сценарий | Базовый URL |
| --- | --- |
| Эмулятор Android | `http://10.0.2.2:8000/` (по умолчанию) |
| Физическое устройство | `http://<IP компьютера>:8000/` |
| Production | HTTPS-адрес reverse proxy |

Переопределение при сборке:

```bash
./gradlew assembleDebug -PapiBaseUrl=http://192.168.1.10:8000/
```

`usesCleartextTraffic` включён только для удобства локальной разработки. Перед production его нужно отключить и использовать HTTPS.

## 4. Docker и окружения

### 4.1. Локальный запуск

`docker-compose.yml` поднимает:

- `db` — PostgreSQL 18 Alpine, порт хоста **5433** (чтобы не конфликтовать с локальным Postgres на 5432), healthcheck `pg_isready`;
- `backend` — образ из `backend/Dockerfile`, ждёт здоровую БД, публикует порт 8000, запускает миграции и Uvicorn.

Данные хранятся в именованном volume `pgdata`. Для PostgreSQL 18+ volume монтируется в `/var/lib/postgresql` — официальный образ хранит данные в версионном подкаталоге и не принимает старый путь `/var/lib/postgresql/data`.

### 4.2. Production

`docker-compose.prod.yml` использует готовый образ из GHCR (`BACKEND_IMAGE`), обязательные `POSTGRES_PASSWORD` и `SECRET_KEY`, не публикует порт БД и запускает backend с `ENVIRONMENT=production`. Рекомендуется ставить перед API reverse proxy с TLS (Caddy, Nginx, Traefik).

### 4.3. Переменные окружения

| Переменная | По умолчанию | Назначение |
| --- | --- | --- |
| `DATABASE_URL` | `postgresql+asyncpg://tarot:tarot@localhost:5432/tarot` | Подключение SQLAlchemy |
| `SECRET_KEY` | dev-значение | Подпись JWT, минимум 16 символов |
| `ACCESS_TOKEN_EXPIRE_MINUTES` | `10080` (7 дней) | Время жизни токена |
| `CORS_ORIGINS` | `*` | Список origin через запятую или `*` |
| `ENVIRONMENT` | `development` | Имя окружения |
| `AUTO_CREATE_TABLES` | `true` | Создавать таблицы при старте |
| `SEED_DATABASE` | `true` | Заполнять колоду при старте |
| `POSTGRES_PORT` | `5433` | Порт PostgreSQL на хосте |
| `BACKEND_PORT` | `8000` | Порт API на хосте |

## 5. CI/CD

```text
push / PR
   │
   ├── backend-ci.yml ──▶ ruff ─▶ pytest (PostgreSQL 18) ─▶ alembic up/down
   │                       └──▶ docker build ─▶ smoke-тест /health
   │
   └── android-ci.yml ──▶ JDK 21 + SDK 37.0 ─▶ unit tests ─▶ lint ─▶ assembleDebug
                                                  └──▶ артефакты APK и отчётов

tag v* / workflow_dispatch
   │
   └── release.yml
        ├── backend-image ──▶ GHCR (semver, ref, sha)
        ├── android-apk ────▶ debug APK всегда, signed release APK при наличии keystore
        │                      └──▶ GitHub Release для тега
        └── deploy (опционально) ──▶ SSH + docker compose prod
```

Особенности:

- Backend-тесты в CI выполняются на реальном PostgreSQL, а локально — на SQLite in-memory.
- `alembic upgrade head` и `downgrade base` проверяют обратимость миграций на чистой БД.
- Smoke-тест поднимает контейнер с БД и ждёт успешный `/health`.
- Android CI ставит пакеты SDK `platforms;android-37.0` и `build-tools;36.0.0`. В новых cmdline-tools используется `android sdk install platforms/android-37.0 build-tools/36.0.0`, поэтому workflow поддерживает оба варианта.
- Release APK подписывается только если задан `ANDROID_KEYSTORE_BASE64`; иначе собирается и прикладывается только debug APK.
- Деплой пропускается, если не задан `DEPLOY_HOST`.

## 6. Тестирование

| Уровень | Инструмент | Что проверяется |
| --- | --- | --- |
| Backend API | pytest + httpx ASGI | Регистрация, вход, JWT, доступ, колода, все расклады, история, удаление, изоляция пользователей |
| Backend статика | ruff | Импорты, стиль, типичные ошибки |
| Android unit | JUnit 4 | Модели DTO, сортировка карт, сериализация имён полей |
| Android статика | Android Lint | API-совместимость, ресурсы, манифест |
| Сборка | Gradle | debug/release APK |
| Инфраструктура | Docker + GitHub Actions | Миграции, healthcheck, smoke-тест |

Тесты backend используют фикстуры `prepared_database` (пересоздаёт схему перед каждым тестом) и `auth_headers` (регистрирует пользователя и возвращает Bearer-заголовок).

## 7. Осознанные ограничения

- Нет refresh-токенов и отзыва токенов: JWT живёт 7 дней, при утечке действует до истечения. Для production стоит добавить refresh-токены и blacklist/rotation.
- Нет rate limiting и CAPTCHA на `/auth/*`; перед публичным запуском нужен лимитер на reverse proxy или в приложении.
- Расклад генерируется на сервере, поэтому клиент не может влиять на выбор карт; это одновременно упрощает клиент и защищает историю.
- Колода зашита в `seed.py` и не редактируется через API. Для управления картами потребуется админ-панель.
- История не имеет полнотекстового поиска и пагинации в UI (API поддерживает `limit`/`offset`).
- Нет push-уведомлений, социальных функций и оплаты — проект учебный.
- `AUTO_CREATE_TABLES=true` удобно локально, но в production схемой должны управлять только миграции; после первого запуска флаг можно выключить.

## 8. Куда развивать

1. Refresh-токены, rotation и отзыв сессий.
2. Rate limiting, аудит входов, блокировка после неудачных попыток.
3. Админ-API для колоды и аналитики.
4. Пагинация истории и фильтры по раскладам.
5. Push-уведомления и ежедневная карта дня.
6. Полноценный HTTPS reverse proxy в compose и мониторинг (Prometheus/Grafana, Sentry).
7. Скриншот-тесты Compose и E2E-тесты на эмуляторе.
