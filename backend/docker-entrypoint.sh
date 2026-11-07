#!/bin/sh
set -eu

attempt=1
max_attempts=30

while [ "$attempt" -le "$max_attempts" ]; do
    if alembic upgrade head; then
        break
    fi
    if [ "$attempt" -eq "$max_attempts" ]; then
        echo "Database is not ready after ${max_attempts} attempts" >&2
        exit 1
    fi
    echo "Waiting for database (attempt ${attempt}/${max_attempts})..."
    attempt=$((attempt + 1))
    sleep 2
done

exec uvicorn app.main:app --host 0.0.0.0 --port "${PORT:-8000}" --proxy-headers
