from __future__ import annotations

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_current_user
from app.core.security import create_access_token, hash_password, verify_password
from app.db.models.user import User
from app.db.session import get_db
from app.schemas.auth import AuthResponse, UserCreate, UserLogin, UserOut

router = APIRouter()


async def _issue_token(user: User) -> AuthResponse:
    return AuthResponse(
        access_token=create_access_token(user.id), user=UserOut.model_validate(user)
    )


async def _authenticate(session: AsyncSession, email: str, password: str) -> User:
    user = await session.scalar(select(User).where(User.email == email.lower().strip()))
    if user is None or not verify_password(password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный email или пароль",
            headers={"WWW-Authenticate": "Bearer"},
        )
    return user


@router.post("/register", response_model=AuthResponse, status_code=status.HTTP_201_CREATED)
async def register(payload: UserCreate, session: AsyncSession = Depends(get_db)) -> AuthResponse:
    email = payload.email.lower().strip()
    existing = await session.scalar(select(User).where(User.email == email))
    if existing is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Пользователь с таким email уже существует",
        )

    user = User(
        email=email,
        display_name=payload.display_name.strip(),
        hashed_password=hash_password(payload.password),
    )
    session.add(user)
    try:
        await session.commit()
    except IntegrityError:
        await session.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Пользователь с таким email уже существует",
        ) from None

    await session.refresh(user)
    return await _issue_token(user)


@router.post("/login", response_model=AuthResponse)
async def login(payload: UserLogin, session: AsyncSession = Depends(get_db)) -> AuthResponse:
    user = await _authenticate(session, payload.email, payload.password)
    return await _issue_token(user)


@router.post("/token", response_model=AuthResponse, include_in_schema=False)
async def login_form(
    form: OAuth2PasswordRequestForm = Depends(),
    session: AsyncSession = Depends(get_db),
) -> AuthResponse:
    """OAuth2 form login used by the Swagger UI authorize button."""
    user = await _authenticate(session, form.username, form.password)
    return await _issue_token(user)


@router.get("/me", response_model=UserOut)
async def me(current_user: User = Depends(get_current_user)) -> User:
    return current_user
