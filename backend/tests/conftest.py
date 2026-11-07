from __future__ import annotations

import os

os.environ["DATABASE_URL"] = os.environ.get("TEST_DATABASE_URL", "sqlite+aiosqlite:///:memory:")
os.environ["SECRET_KEY"] = "test-secret-key-with-enough-length"
os.environ["ENVIRONMENT"] = "test"
os.environ["AUTO_CREATE_TABLES"] = "true"
os.environ["SEED_DATABASE"] = "true"

import pytest_asyncio  # noqa: E402
from app.db.base import Base  # noqa: E402
from app.db.session import engine, init_db  # noqa: E402
from app.main import app  # noqa: E402
from httpx import ASGITransport, AsyncClient  # noqa: E402


@pytest_asyncio.fixture(autouse=True)
async def prepared_database():
    async with engine.begin() as connection:
        await connection.run_sync(Base.metadata.drop_all)
        await connection.run_sync(Base.metadata.create_all)
    await init_db()
    yield
    async with engine.begin() as connection:
        await connection.run_sync(Base.metadata.drop_all)
    # asyncpg connections are bound to the event loop that created them.
    # Tests get a fresh loop, so return pooled connections before it closes.
    await engine.dispose()


@pytest_asyncio.fixture
async def client():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as http_client:
        yield http_client


@pytest_asyncio.fixture
async def register_user(client):
    async def _register(
        email: str = "luna@example.com",
        password: str = "password123",
        display_name: str = "Луна",
    ) -> dict:
        response = await client.post(
            "/api/v1/auth/register",
            json={"email": email, "password": password, "display_name": display_name},
        )
        assert response.status_code == 201, response.text
        return response.json()

    return _register


@pytest_asyncio.fixture
async def auth_headers(register_user):
    data = await register_user()
    return {"Authorization": f"Bearer {data['access_token']}"}
