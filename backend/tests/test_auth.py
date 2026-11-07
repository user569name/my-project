async def test_register_and_me(client, register_user):
    data = await register_user()
    assert data["token_type"] == "bearer"
    assert data["user"]["email"] == "luna@example.com"
    assert data["user"]["display_name"] == "Луна"

    response = await client.get(
        "/api/v1/auth/me",
        headers={"Authorization": f"Bearer {data['access_token']}"},
    )
    assert response.status_code == 200
    assert response.json()["email"] == "luna@example.com"


async def test_duplicate_email(client, register_user):
    await register_user()
    response = await client.post(
        "/api/v1/auth/register",
        json={
            "email": "LUNA@example.com",
            "password": "password123",
            "display_name": "Другая Луна",
        },
    )
    assert response.status_code == 409


async def test_login(client, register_user):
    await register_user()
    response = await client.post(
        "/api/v1/auth/login",
        json={"email": "luna@example.com", "password": "password123"},
    )
    assert response.status_code == 200
    assert response.json()["access_token"]


async def test_login_wrong_password(client, register_user):
    await register_user()
    response = await client.post(
        "/api/v1/auth/login",
        json={"email": "luna@example.com", "password": "wrong-password"},
    )
    assert response.status_code == 401


async def test_me_requires_token(client):
    response = await client.get("/api/v1/auth/me")
    assert response.status_code == 401


async def test_invalid_token(client):
    response = await client.get(
        "/api/v1/auth/me", headers={"Authorization": "Bearer not-a-real-token"}
    )
    assert response.status_code == 401


async def test_short_password_rejected(client):
    response = await client.post(
        "/api/v1/auth/register",
        json={"email": "short@example.com", "password": "123", "display_name": "Тест"},
    )
    assert response.status_code == 422


async def test_oauth2_form_login(client, register_user):
    await register_user()
    response = await client.post(
        "/api/v1/auth/token",
        data={"username": "luna@example.com", "password": "password123"},
    )
    assert response.status_code == 200
    assert response.json()["access_token"]
