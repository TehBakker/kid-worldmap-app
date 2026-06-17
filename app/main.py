"""FastAPI application entry point."""

import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.db import init_db
from app.routers import health, notes
from app.settings import get_settings

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(name)s | %(message)s",
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(_app: FastAPI):
    """Initialize resources on startup and clean up on shutdown."""
    settings = get_settings()
    logger.info("Starting application in %s mode.", settings.environment)
    init_db()
    yield
    logger.info("Application shutdown complete.")


app = FastAPI(
    title="Kid Worldmap API",
    description="Backend API for the kid-worldmap-app project.",
    version="0.1.0",
    lifespan=lifespan,
)

app.include_router(health.router)
app.include_router(notes.router)
