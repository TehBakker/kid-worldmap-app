#!/usr/bin/env bash
# Verify required environment variables before running or deploying.

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

errors=0

check_required() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    echo "ERROR: ${name} is missing or empty."
    errors=$((errors + 1))
  else
    echo "OK: ${name} is set."
  fi
}

echo "Checking required environment variables..."
check_required "DATABASE_URL"

if [[ -n "${DATABASE_URL:-}" ]] && [[ "${DATABASE_URL}" != postgresql+psycopg://* ]] && [[ "${DATABASE_URL}" != postgresql://* ]]; then
  echo "WARNING: DATABASE_URL should use postgresql+psycopg:// for psycopg driver."
fi

if [[ -n "${DATABASE_URL:-}" ]] && [[ "${DATABASE_URL}" != *sslmode=require* ]]; then
  echo "WARNING: Supabase requires sslmode=require in DATABASE_URL."
fi

echo
echo "Optional variables (defaults applied if missing):"
echo "  PORT=${PORT:-8080 (default)}"
echo "  ENVIRONMENT=${ENVIRONMENT:-local (default)}"

if [[ "${errors}" -gt 0 ]]; then
  echo
  echo "${errors} required variable(s) missing. Copy .env.example to .env and fill in values."
  exit 1
fi

echo
echo "Environment check passed."
