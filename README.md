# Mystic Tarot

Полноценное приложение для раскладов Таро: REST API на **FastAPI**, Android-клиент на **Kotlin + Jetpack Compose**, база **PostgreSQL**, упаковка в **Docker** и пайплайны **GitHub Actions (CI/CD)**.

В колоде все 78 карт: 22 старших аркана и 56 младших (Жезлы, Кубки, Мечи, Пентакли) с прямыми и перевёрнутыми значениями на русском языке.

## Возможности

- Регистрация и вход по email с JWT-аутентификацией.
- Три расклада: «Одна карта», «Три карты», «Кельтский крест» (10 карт).
- Случайная ориентация карт (прямая/перевёрнутая) и сохранение истории раскладов.
- Просмотр и удаление своих раскладов; чужие расклады недоступны.
- Swagger UI и OpenAPI-схема.
- Миграции Alembic, автоматический сидинг колоды при старте.
- Тёмная «мистическая» тема Compose, экраны входа, гадания, истории и деталей.
- Тесты API на pytest (21 тест), unit-тесты Android, линтеры.
- Docker Compose для локального запуска и production-конфигурация.
- CI для бэкенда и Android, CD с публикацией Docker-образа в GHCR и APK в GitHub Releases.

## Стек

| Слой | Технологии |
| --- | --- |
| Backend | Python 3.12, FastAPI, SQLAlchemy 2 (async), Pydantic 2, Alembic, PyJWT, Argon2 |
| База данных | PostgreSQL 18 |
| Android | Kotlin 2.4, Jetpack Compose (Material 3), Navigation Compose, ViewModel + StateFlow, Retrofit/OkHttp/Gson |
| Сборка Android | Gradle 9.8.1, AGP 9.5.1, JDK 21, compileSdk 37 |
| Инфраструктура | Docker, Docker Compose, GitHub Actions |

## Быстрый старт

### 1. Запуск бэкенда и PostgreSQL

Нужен установленный Docker Desktop (или Docker Engine + Compose).

```bash
cp .env.example .env
# при необходимости отредактируйте .env и задайте свой SECRET_KEY
docker compose up --build -d
```

Проверка:

```bash
curl http://localhost:8000/health
# {"status":"ok","database":"ok","version":"1.0.0"}
```

- Swagger UI: http://localhost:8000/docs
- API: http://localhost:8000/api/v1
- PostgreSQL доступен на хосте по порту **5433** (чтобы не конфликтовать с локальным Postgres на 5432).

Остановить:

```bash
docker compose down          # данные сохранятся в volume pgdata
docker compose down -v       # удалить и данные
```

### 2. Запуск Android-приложения

Требования: Android Studio (или JDK 21 + Android SDK 37), эмулятор или устройство. Нужные пакеты SDK устанавливаются командой `sdkmanager "platforms;android-37.0" "build-tools;36.0.0" "platform-tools"` (в новых cmdline-tools — `android sdk install "platforms/android-37.0" "build-tools/36.0.0" "platform-tools"`).

```bash
cd android
./gradlew assembleDebug
```

APK: `android/app/build/outputs/apk/debug/app-debug.apk`.

По умолчанию приложение обращается к `http://10.0.2.2:8000/` — это адрес хоста из Android-эмулятора. Для физического устройства укажите IP компьютера:

```bash
./gradlew assembleDebug -PapiBaseUrl=http://192.168.1.10:8000/
```

> Для локальной разработки в манифесте разрешён HTTP (`usesCleartextTraffic`). Для production используйте HTTPS и отключите cleartext.

## Локальная разработка бэкенда без Docker

```bash
cd backend
python -m venv .venv
# Windows: .venv\Scripts\activate
source .venv/bin/activate
pip install -r requirements-dev.txt
cp .env.example .env   # DATABASE_URL указывает на localhost:5433
alembic upgrade head
uvicorn app.main:app --reload
```

Если PostgreSQL запущен через `docker compose`, backend можно запускать локально, используя `localhost:5433`.

### Тесты и линтеры

```bash
cd backend
ruff check .
ruff format --check .
pytest -q
```

Тесты по умолчанию используют SQLite in-memory, а в CI — PostgreSQL из service-контейнера.

### Сборка Android и проверки

```bash
cd android
./gradlew testDebugUnitTest lintDebug assembleDebug
```

## API

Базовый префикс: `/api/v1`.

| Метод | Путь | Описание | Авторизация |
| --- | --- | --- | --- |
| POST | `/auth/register` | Регистрация, возвращает JWT | — |
| POST | `/auth/login` | Вход по email/паролю | — |
| POST | `/auth/token` | OAuth2 form-вход для Swagger | — |
| GET | `/auth/me` | Текущий пользователь | Bearer |
| GET | `/tarot/cards` | Колода (можно фильтровать `?arcana=major`) | — |
| POST | `/tarot/draw` | Сделать расклад | Bearer |
| GET | `/tarot/readings` | История раскладов (`limit`, `offset`) | Bearer |
| GET | `/tarot/readings/{id}` | Расклад по id | Bearer |
| DELETE | `/tarot/readings/{id}` | Удалить расклад | Bearer |
| GET | `/health` | Проверка API и БД | — |

Пример:

```bash
curl -X POST http://localhost:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"luna@example.com","password":"password123","display_name":"Луна"}'

curl -X POST http://localhost:8000/api/v1/tarot/draw \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"question":"Стоит ли мне менять работу?","spread":"three"}'
```

## Структура проекта

```
.
├── backend/
│   ├── app/
│   │   ├── api/            # роутеры и зависимости
│   │   ├── core/           # конфигурация, JWT, расклады
│   │   ├── db/             # сессии, модели, сидинг колоды
│   │   ├── schemas/        # Pydantic-схемы
│   │   ├── services/       # бизнес-логика раскладов
│   │   └── main.py         # точка входа FastAPI
│   ├── migrations/         # Alembic
│   ├── tests/
│   ├── Dockerfile
│   └── requirements*.txt
├── android/
│   └── app/src/main/java/com/example/tarot/
│       ├── data/           # Retrofit, repository, token storage
│       ├── domain/         # модели предметной области
│       ├── ui/             # Compose-экраны, ViewModel, тема
│       └── util/
├── docker-compose.yml
├── docker-compose.prod.yml
└── .github/workflows/      # CI/CD
```

Подробнее — в [ARCHITECTURE.md](ARCHITECTURE.md). Инструкции для контрибьюторов и релизный процесс — в [CONTRIBUTING.md](CONTRIBUTING.md), детали Android-клиента — в [android/README.md](android/README.md).

## CI/CD

| Workflow | Что делает |
| --- | --- |
| `backend-ci.yml` | Ruff, pytest на PostgreSQL, проверка миграций Alembic, сборка образа и smoke-тест контейнера с БД |
| `android-ci.yml` | Unit-тесты, lint, сборка debug APK, публикация артефактов |
| `release.yml` | На тег `v*`: публикация backend-образа в GHCR, сборка APK и GitHub Release, опциональный деплой по SSH |

### Секреты для деплоя

Добавьте в GitHub → Settings → Secrets and variables → Actions:

| Секрет | Назначение |
| --- | --- |
| `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_SSH_KEY`, `DEPLOY_PATH` | SSH-доступ к серверу и путь к репозиторию |
| `GHCR_TOKEN` | Токен для `docker login ghcr.io` на сервере |
| `ANDROID_KEYSTORE_BASE64` | (опционально) release-keystore в base64 |
| `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | (опционально) параметры подписи |

Если `DEPLOY_HOST` не задан, шаг деплоя пропускается. На сервере должен быть установлен Docker Compose и склонирован репозиторий; деплой выполняется командой `docker compose -f docker-compose.prod.yml`.

Production-запуск:

```bash
cp .env.production.example .env
# заполните пароли, SECRET_KEY и BACKEND_IMAGE
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## Безопасность: что важно перед production

- Замените `SECRET_KEY` и пароль PostgreSQL; не коммитьте `.env`.
- Включите HTTPS (reverse proxy с TLS) и отключите `usesCleartextTraffic` в Android-манифесте.
- Ограничьте `CORS_ORIGINS` конкретными доменами.
- Настройте регулярные бэкапы volume `pgdata`.
- Для горизонтального масштабирования вынесите сидинг/миграции в отдельный job (сейчас они идемпотентны и безопасны для одного инстанса).

## Лицензия

Учебный/демонстрационный проект. Используйте свободно в своих целях.
