from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from app.db.session import get_db
from app.db.models.tarot import TarotCard, Prediction
from app.db.models.user import User
from app.schemas.tarot import DrawRequest, PredictionResponse, DrawnCard, CardResponse
from app.api.deps import get_current_user
from app.services.tarot_service import draw_cards, generate_interpretation, save_prediction

router = APIRouter()


@router.get("/cards", response_model=List[CardResponse])
def list_cards(db: Session = Depends(get_db)):
    return db.query(TarotCard).all()


@router.get("/cards/{card_id}", response_model=CardResponse)
def get_card(card_id: int, db: Session = Depends(get_db)):
    card = db.query(TarotCard).filter(TarotCard.id == card_id).first()
    if not card:
        raise HTTPException(404, "Карта не найдена")
    return card


@router.post("/draw", response_model=PredictionResponse)
def draw(
    data: DrawRequest,
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    try:
        drawn = draw_cards(db, data.spread_type)
    except ValueError as e:
        raise HTTPException(400, str(e))

    interpretation = generate_interpretation(data.question or "", data.spread_type, drawn)
    prediction = save_prediction(db, user.id, data.question or "", data.spread_type, drawn, interpretation)

    return PredictionResponse(
        id=prediction.id,
        question=prediction.question,
        spread_type=prediction.spread_type,
        cards=[
            DrawnCard(
                card=CardResponse.model_validate(dc["card"]),
                position=dc["position"],
                is_reversed=dc["is_reversed"]
            ) for dc in drawn
        ],
        interpretation=prediction.interpretation,
        created_at=prediction.created_at
    )


@router.get("/history", response_model=List[PredictionResponse])
def history(
    db: Session = Depends(get_db),
    user: User = Depends(get_current_user)
):
    predictions = db.query(Prediction).filter(
        Prediction.user_id == user.id
    ).order_by(Prediction.created_at.desc()).limit(50).all()

    result = []
    for p in predictions:
        cards_data = []
        for pc in p.cards:
            card = db.query(TarotCard).filter(TarotCard.id == pc.card_id).first()
            cards_data.append(DrawnCard(
                card=CardResponse.model_validate(card),
                position=pc.position,
                is_reversed=bool(pc.is_reversed)
            ))
        result.append(PredictionResponse(
            id=p.id, question=p.question, spread_type=p.spread_type,
            cards=cards_data, interpretation=p.interpretation, created_at=p.created_at
        ))
    return result
