"""Application settings loaded from environment variables."""

from functools import lru_cache
import os

from dotenv import load_dotenv

load_dotenv()


class Settings:
    """Central configuration for the application."""

    def __init__(self) -> None:
        self.database_url: str | None = os.getenv("DATABASE_URL")
        self.port: int = int(os.getenv("PORT", "8080"))
        self.environment: str = os.getenv("ENVIRONMENT", "local")

    @property
    def is_production(self) -> bool:
        """Return True when running in production."""
        return self.environment.lower() == "production"


@lru_cache
def get_settings() -> Settings:
    """Return cached settings instance."""
    return Settings()
