"""Health and root endpoints."""

from fastapi import APIRouter

from app.db import check_db_connection
from app.schemas import DbCheckResponse, HealthResponse, RootResponse

router = APIRouter(tags=["health"])


@router.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    """Return application health status."""
    return HealthResponse(status="ok")


@router.get("/", response_model=RootResponse)
def root() -> RootResponse:
    """Return a welcome message."""
    return RootResponse(message="Kid Worldmap API")


@router.get("/db-check", response_model=DbCheckResponse)
def db_check() -> DbCheckResponse:
    """Verify PostgreSQL connectivity."""
    if check_db_connection():
        return DbCheckResponse(database="connected")

    return DbCheckResponse(
        database="disconnected",
        detail="Unable to connect to PostgreSQL. Check DATABASE_URL and network access.",
    )
