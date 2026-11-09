"""Каталог раскладов Mystic Tarot.

Все описания и схемы — собственная разработка проекта. Расклады не копируют
чужие сайты: используются только классические схемы таро и оригинальные тексты.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class SpreadDefinition:
    id: str
    title: str
    description: str
    category: str
    symbol: str
    positions: tuple[str, ...]


SPREAD_CATALOG: tuple[SpreadDefinition, ...] = (
    SpreadDefinition(
        id="single",
        title="Одна карта",
        description="Короткий и ясный ответ на один конкретный вопрос.",
        category="general",
        symbol="✦",
        positions=("Ответ",),
    ),
    SpreadDefinition(
        id="three",
        title="Три карты",
        description="Как ситуация развивалась, где вы сейчас и что ждёт впереди.",
        category="general",
        symbol="☾",
        positions=("Прошлое", "Настоящее", "Будущее"),
    ),
    SpreadDefinition(
        id="celtic",
        title="Кельтский крест",
        description="Объёмный разбор: причины, препятствия, скрытые мотивы и итог.",
        category="general",
        symbol="✚",
        positions=(
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
        ),
    ),
    SpreadDefinition(
        id="horseshoe",
        title="Подкова",
        description="Семь карт о препятствиях, поддержке и вероятном исходе.",
        category="future",
        symbol="⌒",
        positions=(
            "Прошлое",
            "Настоящее",
            "Скрытые влияния",
            "Препятствие",
            "Окружение",
            "Совет",
            "Итог",
        ),
    ),
    SpreadDefinition(
        id="choice",
        title="Перекрёсток",
        description="Сравните два варианта и поймите, какой путь ближе вам.",
        category="decision",
        symbol="⚖",
        positions=("Суть выбора", "Путь А", "Путь Б", "Что поможет", "Итог"),
    ),
    SpreadDefinition(
        id="yesno",
        title="Да или нет",
        description="Быстрое решение: аргументы за, аргументы против и совет карт.",
        category="decision",
        symbol="◐",
        positions=("За", "Против", "Совет"),
    ),
    SpreadDefinition(
        id="bridge",
        title="Мост между вами",
        description="О чувствах, дистанции и том, что поможет сблизиться.",
        category="love",
        symbol="♥",
        positions=(
            "Вы",
            "Партнёр",
            "Что между вами",
            "Что мешает",
            "Что сблизит",
            "Перспектива",
        ),
    ),
    SpreadDefinition(
        id="spark",
        title="Искра",
        description="О зарождающейся симпатии и о том, куда она может привести.",
        category="love",
        symbol="❥",
        positions=(
            "Ваши чувства",
            "Чувства другого",
            "Что притягивает",
            "Что настораживает",
            "Куда ведёт",
        ),
    ),
    SpreadDefinition(
        id="career",
        title="Карьерный компас",
        description="Сильные стороны, скрытые возможности и следующий шаг в работе.",
        category="career",
        symbol="◈",
        positions=(
            "Текущая ситуация",
            "Сильные стороны",
            "Скрытая возможность",
            "Препятствие",
            "Следующий шаг",
            "Результат",
        ),
    ),
    SpreadDefinition(
        id="money",
        title="Денежный поток",
        description="Откуда приходят деньги, куда уходят и где точка роста.",
        category="money",
        symbol="◍",
        positions=(
            "Сейчас",
            "Что привлекает доход",
            "Куда утекает",
            "Что изменить",
            "Перспектива",
        ),
    ),
    SpreadDefinition(
        id="month",
        title="Лунный месяц",
        description="Прогноз на ближайший месяц: начало, середина, завершение и совет.",
        category="future",
        symbol="☽",
        positions=("Начало", "Середина", "Завершение", "Совет"),
    ),
    SpreadDefinition(
        id="open",
        title="Открытая книга",
        description="Что скрыто в ситуации и какой шаг прояснит её.",
        category="general",
        symbol="▣",
        positions=("Что скрыто", "Что важно знать", "Что делать", "Итог"),
    ),
)

SPREAD_BY_ID: dict[str, SpreadDefinition] = {spread.id: spread for spread in SPREAD_CATALOG}

# Обратная совместимость с сервисом раскладов.
SPREADS: dict[str, list[str]] = {
    spread.id: list(spread.positions) for spread in SPREAD_CATALOG
}

SPREAD_LABELS: dict[str, str] = {spread.id: spread.title for spread in SPREAD_CATALOG}
