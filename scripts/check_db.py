"""Check PostgreSQL connectivity using DATABASE_URL from .env."""

import os
import sys

from dotenv import load_dotenv
from sqlalchemy import create_engine, text

load_dotenv()
database_url = os.getenv("DATABASE_URL")

if not database_url:
    print("ERROR: DATABASE_URL is not set.")
    sys.exit(1)

if "YOUR_PASSWORD" in database_url:
    print("ERROR: Replace YOUR_PASSWORD in .env with your Supabase database password.")
    sys.exit(1)

if "sslmode=" not in database_url:
    separator = "&" if "?" in database_url else "?"
    database_url = f"{database_url}{separator}sslmode=require"

engine = create_engine(database_url, connect_args={"connect_timeout": 10})
try:
    with engine.connect() as conn:
        result = conn.execute(text("SELECT 1")).scalar_one()
        print(f"PostgreSQL connection OK (SELECT 1 => {result})")
except Exception as exc:
    print(f"ERROR: PostgreSQL connection failed: {exc}")
    sys.exit(1)
