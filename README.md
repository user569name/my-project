# Tarot App (Monorepo)

Проект для предсказаний на картах Таро.
Стек: Python (FastAPI), Kotlin (Jetpack Compose), PostgreSQL, Docker, CI/CD.

## Быстрый запуск backend

PostgreSQL запускается в Docker на порту **5433**, чтобы не конфликтовать с локальным PostgreSQL на 5432.

```powershell
# 1. База данных
docker compose -f docker/docker-compose.yml up -d db

# 2. Зависимости
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt

# 3. Настройки (скопировать и при необходимости изменить)
Copy-Item .env.example .env

# 4. Таблицы и 78 карт
python app/scripts/seed.py

# 5. API
uvicorn app.main:app --reload
```

API: http://localhost:8000, Swagger: http://localhost:8000/docs.

## Полный запуск в Docker

```powershell
docker compose -f docker/docker-compose.yml up --build
```

Проверка: `GET http://localhost:8000/health`.
