from fastapi import FastAPI
from app.api.v1 import tarot

app = FastAPI(title="Tarot API", version="1.0.0")

app.include_router(tarot.router, prefix="/api/v1/tarot", tags=["tarot"])

@app.get("/health")
def health_check():
    return {"status": "ok"}
