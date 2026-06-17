"""Pydantic schemas for request and response validation."""

from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field


class HealthResponse(BaseModel):
    """Health check response."""

    status: str


class RootResponse(BaseModel):
    """Root endpoint response."""

    message: str


class DbCheckResponse(BaseModel):
    """Database connectivity check response."""

    database: str
    detail: str | None = None


class NoteCreate(BaseModel):
    """Payload for creating a note."""

    title: str = Field(..., min_length=1, max_length=255)
    content: str = Field(..., min_length=1)


class NoteRead(BaseModel):
    """Note returned by the API."""

    model_config = ConfigDict(from_attributes=True)

    id: int
    title: str
    content: str
    created_at: datetime
