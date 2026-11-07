"""Spread definitions shared by the API, schemas and services."""

from __future__ import annotations

SPREADS: dict[str, list[str]] = {
    "single": ["Ответ"],
    "three": ["Прошлое", "Настоящее", "Будущее"],
    "celtic": [
        "Суть ситуации",
        "Вызов",
        "Прошлое",
        "Будущее",
        "Сознание",
        "Подсознание",
        "Совет",
        "Влияние извне",
        "Надежды и страхи",
        "Итог",
    ],
}

SPREAD_LABELS: dict[str, str] = {
    "single": "Одна карта",
    "three": "Три карты",
    "celtic": "Кельтский крест",
}
