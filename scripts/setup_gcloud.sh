#!/usr/bin/env bash
# Configure Google Cloud project and enable APIs required for Cloud Run deployment.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "==> Checking gcloud CLI installation"
if ! command -v gcloud >/dev/null 2>&1; then
  echo "ERROR: gcloud CLI is not installed."
  echo "Install it from: https://cloud.google.com/sdk/docs/install"
  exit 1
fi

echo "gcloud version:"
gcloud --version
echo

echo "==> Checking gcloud authentication"
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
  echo "No active gcloud account found. Run: gcloud auth login"
  exit 1
fi

ACTIVE_ACCOUNT="$(gcloud auth list --filter=status:ACTIVE --format="value(account)" | head -n 1)"
echo "Active account: ${ACTIVE_ACCOUNT}"
echo

if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

if [[ -z "${PROJECT_ID:-}" ]]; then
  read -r -p "Enter your Google Cloud PROJECT_ID: " PROJECT_ID
fi

if [[ -z "${PROJECT_ID}" ]]; then
  echo "ERROR: PROJECT_ID is required."
  exit 1
fi

echo "==> Setting active project to ${PROJECT_ID}"
gcloud config set project "${PROJECT_ID}"

echo "==> Enabling required Google Cloud APIs"
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com

echo
echo "Google Cloud setup completed for project: ${PROJECT_ID}"
echo "Next step: ./scripts/deploy_cloudrun.sh"
