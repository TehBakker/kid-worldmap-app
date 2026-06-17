#!/usr/bin/env bash
# Verify PostgreSQL connectivity using DATABASE_URL from .env.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

if [[ -z "${DATABASE_URL:-}" ]]; then
  echo "ERROR: DATABASE_URL is not set. Copy .env.example to .env first."
  exit 1
fi

echo "Checking PostgreSQL connection..."

python - <<'PY'
import sys

from sqlalchemy import create_engine, text

from dotenv import load_dotenv
import os

load_dotenv()
database_url = os.getenv("DATABASE_URL")
if not database_url:
    print("ERROR: DATABASE_URL is not set.")
    sys.exit(1)

if "sslmode=" not in database_url:
    separator = "&" if "?" in database_url else "?"
    database_url = f"{database_url}{separator}sslmode=require"

engine = create_engine(database_url)
try:
    with engine.connect() as conn:
        result = conn.execute(text("SELECT 1")).scalar_one()
        print(f"PostgreSQL connection OK (SELECT 1 => {result})")
except Exception as exc:
    print(f"ERROR: PostgreSQL connection failed: {exc}")
    sys.exit(1)
PY

echo "Database check completed successfully."
