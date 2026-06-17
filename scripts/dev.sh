#!/usr/bin/env bash
# Load .env and start FastAPI in development mode with auto-reload.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
  echo "Loaded environment from .env"
else
  echo "Warning: .env file not found. Copy .env.example to .env first."
fi

export PORT="${PORT:-8080}"

echo "Starting FastAPI on http://127.0.0.1:${PORT} (ENVIRONMENT=${ENVIRONMENT:-local})"
exec uvicorn app.main:app --host 127.0.0.1 --port "${PORT}" --reload
