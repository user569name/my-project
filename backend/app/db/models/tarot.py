from __future__ import annotations

import uuid
from datetime import datetime
from typing import TYPE_CHECKING, Any

from sqlalchemy import (
    JSON,
    Boolean,
    DateTime,
    ForeignKey,
    Integer,
    String,
    Text,
    UniqueConstraint,
    Uuid,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base
from app.db.models.user import utcnow

if TYPE_CHECKING:
    from app.db.models.user import User


class TarotCard(Base):
    __tablename__ = "tarot_cards"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    slug: Mapped[str] = mapped_column(String(80), unique=True, index=True, nullable=False)
    name: Mapped[str] = mapped_column(String(80), nullable=False)
    name_ru: Mapped[str] = mapped_column(String(80), nullable=False)
    arcana: Mapped[str] = mapped_column(String(10), nullable=False)
    suit: Mapped[str | None] = mapped_column(String(20), nullable=True)
    number: Mapped[int] = mapped_column(Integer, nullable=False)
    symbol: Mapped[str] = mapped_column(String(16), default="✦", nullable=False)
    keywords_upright: Mapped[list[str]] = mapped_column(JSON, default=list, nullable=False)
    keywords_reversed: Mapped[list[str]] = mapped_column(JSON, default=list, nullable=False)
    meaning_upright: Mapped[str] = mapped_column(Text, nullable=False)
    meaning_reversed: Mapped[str] = mapped_column(Text, nullable=False)


class Reading(Base):
    __tablename__ = "readings"

    id: Mapped[uuid.UUID] = mapped_column(Uuid, primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(
        Uuid, ForeignKey("users.id", ondelete="CASCADE"), index=True, nullable=False
    )
    question: Mapped[str] = mapped_column(String(500), nullable=False)
    spread: Mapped[str] = mapped_column(String(20), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utcnow, index=True, nullable=False
    )

    user: Mapped[User] = relationship(back_populates="readings")
    cards: Mapped[list[ReadingCard]] = relationship(
        back_populates="reading",
        cascade="all, delete-orphan",
        passive_deletes=True,
        order_by="ReadingCard.position",
    )


class ReadingCard(Base):
    __tablename__ = "reading_cards"
    __table_args__ = (UniqueConstraint("reading_id", "position", name="uq_reading_position"),)

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    reading_id: Mapped[uuid.UUID] = mapped_column(
        Uuid, ForeignKey("readings.id", ondelete="CASCADE"), index=True, nullable=False
    )
    card_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("tarot_cards.id", ondelete="RESTRICT"), nullable=False
    )
    position: Mapped[int] = mapped_column(Integer, nullable=False)
    position_name: Mapped[str] = mapped_column(String(80), nullable=False)
    reversed: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)

    reading: Mapped[Reading] = relationship(back_populates="cards")
    card: Mapped[TarotCard] = relationship(lazy="joined")


def card_to_dict(card: TarotCard) -> dict[str, Any]:
    """Small helper used by tests and debugging."""
    return {
        "id": card.id,
        "slug": card.slug,
        "name": card.name,
        "name_ru": card.name_ru,
        "arcana": card.arcana,
        "suit": card.suit,
        "number": card.number,
        "symbol": card.symbol,
    }
