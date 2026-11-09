import pytest


async def test_cards_seeded(client):
    response = await client.get("/api/v1/tarot/cards")
    assert response.status_code == 200
    cards = response.json()
    assert len(cards) == 78
    assert any(card["name_ru"] == "Шут" for card in cards)
    assert any(card["name_ru"] == "Туз Жезлов" for card in cards)
    assert all(card["meaning_upright"] for card in cards)


async def test_major_arcana_filter(client):
    response = await client.get("/api/v1/tarot/cards", params={"arcana": "major"})
    assert response.status_code == 200
    assert len(response.json()) == 22


async def test_draw_single(client, auth_headers):
    response = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Что меня ждёт сегодня?", "spread": "single"},
        headers=auth_headers,
    )
    assert response.status_code == 201, response.text
    body = response.json()
    assert body["spread"] == "single"
    assert body["spread_label"] == "Одна карта"
    assert len(body["cards"]) == 1
    assert body["cards"][0]["position"] == 1
    assert body["cards"][0]["position_name"] == "Ответ"
    assert isinstance(body["cards"][0]["reversed"], bool)
    assert body["cards"][0]["card"]["name_ru"]


async def test_spread_catalog(client):
    response = await client.get("/api/v1/tarot/spreads")
    assert response.status_code == 200
    spreads = response.json()
    ids = {spread["id"] for spread in spreads}
    assert len(spreads) == 12
    assert {"single", "three", "celtic", "bridge", "career", "yesno"} <= ids

    bridge = next(spread for spread in spreads if spread["id"] == "bridge")
    assert bridge["title"] == "Мост между вами"
    assert bridge["cards_count"] == 6
    assert bridge["positions"][0] == "Вы"
    assert bridge["category"] == "love"


@pytest.mark.parametrize(
    ("spread", "count"),
    [("three", 3), ("celtic", 10), ("bridge", 6), ("choice", 5), ("month", 4)],
)
async def test_draw_spreads(client, auth_headers, spread, count):
    response = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Стоит ли мне менять работу?", "spread": spread},
        headers=auth_headers,
    )
    assert response.status_code == 201, response.text
    cards = response.json()["cards"]
    assert len(cards) == count
    assert [card["position"] for card in cards] == list(range(1, count + 1))


async def test_draw_requires_auth(client):
    response = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Что меня ждёт?", "spread": "single"},
    )
    assert response.status_code == 401


async def test_invalid_spread(client, auth_headers):
    response = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Что меня ждёт?", "spread": "unknown"},
        headers=auth_headers,
    )
    assert response.status_code == 422


async def test_question_validation(client, auth_headers):
    response = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "ok", "spread": "single"},
        headers=auth_headers,
    )
    assert response.status_code == 422


async def test_history_and_delete(client, auth_headers):
    created = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Первый вопрос", "spread": "single"},
        headers=auth_headers,
    )
    reading_id = created.json()["id"]

    history = await client.get("/api/v1/tarot/readings", headers=auth_headers)
    assert history.status_code == 200
    assert len(history.json()) == 1
    assert history.json()[0]["question"] == "Первый вопрос"

    detail = await client.get(f"/api/v1/tarot/readings/{reading_id}", headers=auth_headers)
    assert detail.status_code == 200
    assert detail.json()["id"] == reading_id

    deleted = await client.delete(f"/api/v1/tarot/readings/{reading_id}", headers=auth_headers)
    assert deleted.status_code == 204

    missing = await client.get(f"/api/v1/tarot/readings/{reading_id}", headers=auth_headers)
    assert missing.status_code == 404


async def test_readings_are_private(client, auth_headers, register_user):
    created = await client.post(
        "/api/v1/tarot/draw",
        json={"question": "Личный вопрос", "spread": "single"},
        headers=auth_headers,
    )
    reading_id = created.json()["id"]

    second = await register_user(email="mars@example.com", display_name="Марс")
    second_headers = {"Authorization": f"Bearer {second['access_token']}"}

    response = await client.get(f"/api/v1/tarot/readings/{reading_id}", headers=second_headers)
    assert response.status_code == 404

    history = await client.get("/api/v1/tarot/readings", headers=second_headers)
    assert history.json() == []


async def test_readings_require_auth(client):
    response = await client.get("/api/v1/tarot/readings")
    assert response.status_code == 401
