async def test_health(client):
    response = await client.get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "ok"
    assert body["database"] == "ok"


async def test_root(client):
    response = await client.get("/")
    assert response.status_code == 200
    assert "text/html" in response.headers["content-type"]
    assert "Mystic Tarot" in response.text
