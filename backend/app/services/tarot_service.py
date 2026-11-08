import random
from sqlalchemy.orm import Session
from app.db.models.tarot import TarotCard, Prediction, PredictionCard
from app.schemas.tarot import DrawRequest, DrawnCard, CardResponse
from typing import List

# Значения позиций для расклада из 3 карт
THREE_CARDS_POSITIONS = {
    0: "Прошлое",
    1: "Настоящее",
    2: "Будущее"
}

def draw_cards(db: Session, spread_type: str) -> List[dict]:
    """Вытягивает случайные карты для расклада"""
    count = 1 if spread_type == "single" else 3
    all_cards = db.query(TarotCard).all()
    
    if len(all_cards) < count:
        raise ValueError("Недостаточно карт в базе данных")
    
    drawn = random.sample(all_cards, count)
    result = []
    for i, card in enumerate(drawn):
        result.append({
            "card": card,
            "position": i,
            "is_reversed": random.choice([True, False])
        })
    return result

def generate_interpretation(
    question: str, 
    spread_type: str, 
    drawn_cards: List[dict]
) -> str:
    """Генерирует текстовую интерпретацию расклада"""
    
    intro = f"Ваш вопрос: «{question}»\n\n" if question else "Ваш расклад:\n\n"
    
    parts = [intro]
    for dc in drawn_cards:
        card = dc["card"]
        position_name = THREE_CARDS_POSITIONS.get(dc["position"], "Карта") if spread_type == "three_cards" else "Карта"
        orientation = "перевёрнутая" if dc["is_reversed"] else "прямая"
        meaning = card.meaning_reversed if dc["is_reversed"] else card.meaning_upright
        
        parts.append(
            f"📍 {position_name}: {card.name} ({orientation})\n"
            f"   Ключевые слова: {card.keywords}\n"
            f"   Значение: {meaning}\n\n"
        )
    
    # Итоговый совет (можно заменить на вызов OpenAI)
    parts.append(
        "✨ Совет: доверьтесь своей интуиции и прислушайтесь к внутреннему голосу. "
        "Карты указывают путь, но выбор всегда за вами."
    )
    
    return "".join(parts)

def save_prediction(
    db: Session,
    user_id: int,
    question: str,
    spread_type: str,
    drawn_cards: List[dict],
    interpretation: str
) -> Prediction:
    """Сохраняет расклад в историю пользователя"""
    prediction = Prediction(
        user_id=user_id,
        question=question,
        spread_type=spread_type,
        interpretation=interpretation
    )
    db.add(prediction)
    db.flush()
    
    for dc in drawn_cards:
        pc = PredictionCard(
            prediction_id=prediction.id,
            card_id=dc["card"].id,
            position=dc["position"],
            is_reversed=1 if dc["is_reversed"] else 0
        )
        db.add(pc)
    
    db.commit()
    db.refresh(prediction)
    return prediction