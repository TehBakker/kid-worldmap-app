#!/usr/bin/env bash
# Smoke-test the local API endpoints.

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

BASE_URL="${BASE_URL:-http://127.0.0.1:${PORT:-8080}}"

echo "Testing API at ${BASE_URL}"
echo

echo "==> GET /health"
curl -sS -f "${BASE_URL}/health"
echo
echo

echo "==> GET /db-check"
curl -sS -f "${BASE_URL}/db-check"
echo
echo

echo "==> POST /notes"
curl -sS -f -X POST "${BASE_URL}/notes" \
  -H "Content-Type: application/json" \
  -d '{"title":"Ma note","content":"Contenu"}'
echo
echo

echo "==> GET /notes"
curl -sS -f "${BASE_URL}/notes"
echo
echo

echo "All API smoke tests passed."
