"""Tests for health and root endpoints."""

from fastapi.testclient import TestClient


def test_health(client: TestClient) -> None:
    """Health endpoint returns ok status."""
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_root(client: TestClient) -> None:
    """Root endpoint returns hello message."""
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}


def test_db_check_without_database(monkeypatch) -> None:
    """Db-check reports disconnected when database is unavailable."""
    from app.main import app

    monkeypatch.setattr("app.routers.health.check_db_connection", lambda: False)

    with TestClient(app) as test_client:
        response = test_client.get("/db-check")

    assert response.status_code == 200
    payload = response.json()
    assert payload["database"] == "disconnected"
    assert "detail" in payload
