from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from app.db.models.user import Base

class TarotCard(Base):
    __tablename__ = "tarot_cards"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True, nullable=False)
    arcana = Column(String)  # "major" или "minor"
    suit = Column(String)     # "wands", "cups", "swords", "pentacles" или None
    number = Column(Integer)
    keywords = Column(String)
    meaning_upright = Column(Text)   # Прямое значение
    meaning_reversed = Column(Text)  # Перевернутое значение
    image_url = Column(String)

class Prediction(Base):
    __tablename__ = "predictions"
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    question = Column(Text)
    spread_type = Column(String, default="single")  # single, three_cards, celtic_cross
    interpretation = Column(Text)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    cards = relationship("PredictionCard", back_populates="prediction")

class PredictionCard(Base):
    __tablename__ = "prediction_cards"
    id = Column(Integer, primary_key=True, index=True)
    prediction_id = Column(Integer, ForeignKey("predictions.id"), nullable=False)
    card_id = Column(Integer, ForeignKey("tarot_cards.id"), nullable=False)
    position = Column(Integer, default=0)   # Позиция в раскладе
    is_reversed = Column(Integer, default=0)  # 0 - прямая, 1 - перевернутая
    
    prediction = relationship("Prediction", back_populates="cards")