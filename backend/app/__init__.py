from app.db.models.user import User, Base
from app.db.models.tarot import TarotCard, Prediction, PredictionCard

__all__ = ["User", "TarotCard", "Prediction", "PredictionCard", "Base"]