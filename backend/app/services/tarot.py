from __future__ import annotations

import random

from fastapi import HTTPException, status
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.core.spreads import SPREADS
from app.db.models.tarot import Reading, ReadingCard, TarotCard
from app.db.models.user import User

_load_options = (selectinload(Reading.cards).selectinload(ReadingCard.card),)


async def get_reading(
    session: AsyncSession, reading_id, user_id, *, load_cards: bool = True
) -> Reading | None:
    statement = select(Reading).where(Reading.id == reading_id, Reading.user_id == user_id)
    if load_cards:
        statement = statement.options(*_load_options)
    return await session.scalar(statement)


async def list_readings(
    session: AsyncSession, user_id, *, limit: int = 50, offset: int = 0
) -> list[Reading]:
    statement = (
        select(Reading)
        .where(Reading.user_id == user_id)
        .options(*_load_options)
        .order_by(Reading.created_at.desc())
        .limit(limit)
        .offset(offset)
    )
    result = await session.scalars(statement)
    return list(result.unique().all())


async def draw_reading(session: AsyncSession, user: User, question: str, spread: str) -> Reading:
    positions = SPREADS.get(spread)
    if positions is None:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail="Неизвестный расклад"
        )

    result = await session.scalars(select(TarotCard))
    deck = list(result.all())
    if len(deck) < len(positions):
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Колода карт ещё не готова. Попробуйте чуть позже.",
        )

    rng = random.SystemRandom()
    picked = rng.sample(deck, k=len(positions))

    reading = Reading(user_id=user.id, question=question.strip(), spread=spread)
    session.add(reading)
    await session.flush()

    for position, card in enumerate(picked, start=1):
        session.add(
            ReadingCard(
                reading_id=reading.id,
                card_id=card.id,
                position=position,
                position_name=positions[position - 1],
                reversed=rng.random() < 0.3,
            )
        )

    await session.commit()

    loaded = await get_reading(session, reading.id, user.id)
    if loaded is None:  # pragma: no cover - should never happen
        raise HTTPException(status_code=500, detail="Не удалось сохранить расклад")
    return loaded
