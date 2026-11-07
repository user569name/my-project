"""Initial schema: users, tarot cards, readings.

Revision ID: 0001_initial
Revises:
Create Date: 2026-11-08
"""

from __future__ import annotations

from collections.abc import Sequence

import sqlalchemy as sa
from alembic import op

revision: str = "0001_initial"
down_revision: str | None = None
branch_labels: str | Sequence[str] | None = None
depends_on: str | Sequence[str] | None = None


def upgrade() -> None:
    op.create_table(
        "users",
        sa.Column("id", sa.Uuid(), nullable=False),
        sa.Column("email", sa.String(length=255), nullable=False),
        sa.Column("hashed_password", sa.String(length=255), nullable=False),
        sa.Column("display_name", sa.String(length=80), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_users_email", "users", ["email"], unique=True)

    op.create_table(
        "tarot_cards",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("slug", sa.String(length=80), nullable=False),
        sa.Column("name", sa.String(length=80), nullable=False),
        sa.Column("name_ru", sa.String(length=80), nullable=False),
        sa.Column("arcana", sa.String(length=10), nullable=False),
        sa.Column("suit", sa.String(length=20), nullable=True),
        sa.Column("number", sa.Integer(), nullable=False),
        sa.Column("symbol", sa.String(length=16), nullable=False),
        sa.Column("keywords_upright", sa.JSON(), nullable=False),
        sa.Column("keywords_reversed", sa.JSON(), nullable=False),
        sa.Column("meaning_upright", sa.Text(), nullable=False),
        sa.Column("meaning_reversed", sa.Text(), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_tarot_cards_slug", "tarot_cards", ["slug"], unique=True)

    op.create_table(
        "readings",
        sa.Column("id", sa.Uuid(), nullable=False),
        sa.Column("user_id", sa.Uuid(), nullable=False),
        sa.Column("question", sa.String(length=500), nullable=False),
        sa.Column("spread", sa.String(length=20), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(["user_id"], ["users.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_readings_created_at", "readings", ["created_at"], unique=False)
    op.create_index("ix_readings_user_id", "readings", ["user_id"], unique=False)

    op.create_table(
        "reading_cards",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("reading_id", sa.Uuid(), nullable=False),
        sa.Column("card_id", sa.Integer(), nullable=False),
        sa.Column("position", sa.Integer(), nullable=False),
        sa.Column("position_name", sa.String(length=80), nullable=False),
        sa.Column("reversed", sa.Boolean(), nullable=False),
        sa.ForeignKeyConstraint(["card_id"], ["tarot_cards.id"], ondelete="RESTRICT"),
        sa.ForeignKeyConstraint(["reading_id"], ["readings.id"], ondelete="CASCADE"),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("reading_id", "position", name="uq_reading_position"),
    )
    op.create_index("ix_reading_cards_reading_id", "reading_cards", ["reading_id"], unique=False)


def downgrade() -> None:
    op.drop_index("ix_reading_cards_reading_id", table_name="reading_cards")
    op.drop_table("reading_cards")
    op.drop_index("ix_readings_user_id", table_name="readings")
    op.drop_index("ix_readings_created_at", table_name="readings")
    op.drop_table("readings")
    op.drop_index("ix_tarot_cards_slug", table_name="tarot_cards")
    op.drop_table("tarot_cards")
    op.drop_index("ix_users_email", table_name="users")
    op.drop_table("users")
