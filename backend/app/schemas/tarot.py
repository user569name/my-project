from pydantic import BaseModel
from datetime import datetime

class CardResponse(BaseModel):
    id: int
    name: str
    meaning: str
    image_url: str

class PredictionResponse(BaseModel):
    card: CardResponse
    prediction_text: str
    created_at: datetime

    class Config:
        from_attributes = True
