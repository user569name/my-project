from __future__ import annotations

from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException, Query, Response, status
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_current_user
from app.db.models.tarot import TarotCard
from app.db.models.user import User
from app.db.session import get_db
from app.schemas.tarot import DrawRequest, ReadingOut, TarotCardOut
from app.services.tarot import draw_reading, get_reading, list_readings

router = APIRouter()


@router.get("/cards", response_model=list[TarotCardOut])
async def cards(
    arcana: str | None = Query(default=None, pattern="^(major|minor)$"),
    session: AsyncSession = Depends(get_db),
) -> list[TarotCard]:
    statement = select(TarotCard).order_by(TarotCard.number, TarotCard.id)
    if arcana:
        statement = statement.where(TarotCard.arcana == arcana)
    result = await session.scalars(statement)
    return list(result.all())


@router.post("/draw", response_model=ReadingOut, status_code=status.HTTP_201_CREATED)
async def draw(
    payload: DrawRequest,
    current_user: User = Depends(get_current_user),
    session: AsyncSession = Depends(get_db),
) -> ReadingOut:
    reading = await draw_reading(session, current_user, payload.question, payload.spread)
    return ReadingOut.model_validate(reading)


@router.get("/readings", response_model=list[ReadingOut])
async def readings(
    limit: int = Query(default=50, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    current_user: User = Depends(get_current_user),
    session: AsyncSession = Depends(get_db),
) -> list[ReadingOut]:
    items = await list_readings(session, current_user.id, limit=limit, offset=offset)
    return [ReadingOut.model_validate(reading) for reading in items]


@router.get("/readings/{reading_id}", response_model=ReadingOut)
async def reading_detail(
    reading_id: UUID,
    current_user: User = Depends(get_current_user),
    session: AsyncSession = Depends(get_db),
) -> ReadingOut:
    reading = await get_reading(session, reading_id, current_user.id)
    if reading is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Расклад не найден")
    return ReadingOut.model_validate(reading)


@router.delete("/readings/{reading_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_reading(
    reading_id: UUID,
    current_user: User = Depends(get_current_user),
    session: AsyncSession = Depends(get_db),
) -> Response:
    reading = await get_reading(session, reading_id, current_user.id, load_cards=False)
    if reading is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Расклад не найден")
    await session.delete(reading)
    await session.commit()
    return Response(status_code=status.HTTP_204_NO_CONTENT)
