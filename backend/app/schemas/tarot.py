from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

class CardResponse(BaseModel):
    id: int
    name: str
    arcana: Optional[str] = None
    suit: Optional[str] = None
    number: Optional[int] = None
    keywords: Optional[str] = None
    meaning_upright: Optional[str] = None
    meaning_reversed: Optional[str] = None
    image_url: Optional[str] = None
    
    class Config:
        from_attributes = True

class DrawnCard(BaseModel):
    card: CardResponse
    position: int
    is_reversed: bool

class DrawRequest(BaseModel):
    question: Optional[str] = None
    spread_type: str = "single"  # single, three_cards

class PredictionResponse(BaseModel):
    id: int
    question: Optional[str]
    spread_type: str
    cards: List[DrawnCard]
    interpretation: str
    created_at: datetime
    
    class Config:
        from_attributes = True