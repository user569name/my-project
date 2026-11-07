from __future__ import annotations

from datetime import datetime
from typing import Literal
from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field, computed_field

from app.core.spreads import SPREAD_LABELS


class TarotCardOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    slug: str
    name: str
    name_ru: str
    arcana: str
    suit: str | None
    number: int
    symbol: str
    keywords_upright: list[str]
    keywords_reversed: list[str]
    meaning_upright: str
    meaning_reversed: str


class ReadingCardOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    position: int
    position_name: str
    reversed: bool
    card: TarotCardOut


class ReadingOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: UUID
    question: str
    spread: str
    created_at: datetime
    cards: list[ReadingCardOut]

    @computed_field  # type: ignore[prop-decorator]
    @property
    def spread_label(self) -> str:
        return SPREAD_LABELS.get(self.spread, self.spread)


class DrawRequest(BaseModel):
    question: str = Field(min_length=3, max_length=500)
    spread: Literal["single", "three", "celtic"] = "three"
