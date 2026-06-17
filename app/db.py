"""Database engine and session configuration."""

import logging
from collections.abc import Generator

from sqlalchemy import create_engine, text
from sqlalchemy.orm import DeclarativeBase, Session, sessionmaker

from app.settings import get_settings

logger = logging.getLogger(__name__)


class Base(DeclarativeBase):
    """Base class for SQLAlchemy models."""


def _normalize_database_url(url: str) -> str:
    """Ensure Supabase-compatible SSL settings are present."""
    if "sslmode=" not in url:
        separator = "&" if "?" in url else "?"
        url = f"{url}{separator}sslmode=require"
    return url


def _build_engine():
    """Create SQLAlchemy engine from DATABASE_URL."""
    settings = get_settings()
    if not settings.database_url:
        return None

    database_url = _normalize_database_url(settings.database_url)

    return create_engine(
        database_url,
        pool_pre_ping=True,
    )


engine = _build_engine()
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine) if engine else None


def get_db() -> Generator[Session, None, None]:
    """Provide a database session for request handlers."""
    if SessionLocal is None:
        raise RuntimeError("DATABASE_URL is not configured.")

    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def init_db() -> None:
    """Create all database tables if they do not exist."""
    if engine is None:
        logger.warning("DATABASE_URL is not set; skipping table creation.")
        return

    from app import models  # noqa: F401 — register models with Base.metadata

    Base.metadata.create_all(bind=engine)
    logger.info("Database tables initialized.")


def check_db_connection() -> bool:
    """Return True if the database connection is healthy."""
    if engine is None:
        return False

    try:
        with engine.connect() as connection:
            connection.execute(text("SELECT 1"))
        return True
    except Exception:
        logger.exception("Database connection check failed.")
        return False
