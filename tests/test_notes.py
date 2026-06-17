"""Tests for notes endpoints."""

from fastapi.testclient import TestClient


def test_create_and_list_notes(client: TestClient) -> None:
    """Notes can be created and retrieved."""
    create_response = client.post(
        "/notes",
        json={"title": "Ma note", "content": "Contenu"},
    )
    assert create_response.status_code == 201
    created = create_response.json()
    assert created["title"] == "Ma note"
    assert created["content"] == "Contenu"
    assert "id" in created
    assert "created_at" in created

    list_response = client.get("/notes")
    assert list_response.status_code == 200
    notes = list_response.json()
    assert len(notes) == 1
    assert notes[0]["title"] == "Ma note"


def test_create_note_validation(client: TestClient) -> None:
    """Invalid note payload is rejected."""
    response = client.post("/notes", json={"title": "", "content": ""})
    assert response.status_code == 422
