from __future__ import annotations

from contextlib import asynccontextmanager
from pathlib import Path

from fastapi import Depends, FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import FileResponse, JSONResponse
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.v1 import auth, tarot
from app.core.config import settings
from app.db.session import engine, get_db, init_db


@asynccontextmanager
async def lifespan(_: FastAPI):
    await init_db()
    yield
    await engine.dispose()


def create_app() -> FastAPI:
    application = FastAPI(
        title=settings.PROJECT_NAME,
        version=settings.VERSION,
        description="API для астрологического приложения Mystic Tarot",
        lifespan=lifespan,
    )

    application.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins_list,
        allow_credentials=settings.cors_origins_list != ["*"],
        allow_methods=["*"],
        allow_headers=["*"],
    )
    application.add_middleware(GZipMiddleware, minimum_size=1024)

    application.include_router(auth.router, prefix=f"{settings.API_V1_PREFIX}/auth", tags=["auth"])
    application.include_router(
        tarot.router, prefix=f"{settings.API_V1_PREFIX}/tarot", tags=["tarot"]
    )

    @application.get("/", tags=["system"], include_in_schema=False)
    async def web_app() -> FileResponse:
        """Веб-версия приложения — открывается по ссылке в браузере."""
        index = Path(__file__).resolve().parent.parent / "static" / "index.html"
        if index.is_file():
            return FileResponse(index, media_type="text/html")
        return JSONResponse(status_code=404, content={"detail": "Web UI not found"})

    @application.get("/health", tags=["system"])
    async def health(session: AsyncSession = Depends(get_db)):
        try:
            await session.execute(text("SELECT 1"))
        except Exception:
            return JSONResponse(
                status_code=503, content={"status": "error", "database": "unavailable"}
            )
        return {"status": "ok", "database": "ok", "version": settings.VERSION}

    return application


app = create_app()
