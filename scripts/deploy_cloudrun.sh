#!/usr/bin/env bash
# Build and deploy the application to Google Cloud Run.

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

echo "==> Checking prerequisites"
command -v gcloud >/dev/null 2>&1 || { echo "ERROR: gcloud CLI not found."; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "ERROR: docker not found."; exit 1; }

if [[ -z "${PROJECT_ID:-}" ]]; then
  echo "ERROR: PROJECT_ID is not set. Add it to .env or export it."
  exit 1
fi

if [[ -z "${REGION:-}" ]]; then
  REGION="europe-west1"
  echo "REGION not set, using default: ${REGION}"
fi

if [[ -z "${SERVICE_NAME:-}" ]]; then
  SERVICE_NAME="fastapi-backend"
  echo "SERVICE_NAME not set, using default: ${SERVICE_NAME}"
fi

if [[ -z "${DATABASE_URL:-}" ]]; then
  echo "ERROR: DATABASE_URL is not set. Cloud Run needs a valid Supabase connection string."
  exit 1
fi

IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/cloud-run-source-deploy/${SERVICE_NAME}:latest"

echo "==> Configuring Docker for Artifact Registry"
gcloud auth configure-docker "${REGION}-docker.pkg.dev" --quiet

echo "==> Ensuring Artifact Registry repository exists"
if ! gcloud artifacts repositories describe cloud-run-source-deploy \
  --location="${REGION}" \
  --project="${PROJECT_ID}" >/dev/null 2>&1; then
  gcloud artifacts repositories create cloud-run-source-deploy \
    --repository-format=docker \
    --location="${REGION}" \
    --description="Cloud Run source deploy images"
fi

echo "==> Building Docker image: ${IMAGE}"
docker build -t "${IMAGE}" .

echo "==> Pushing Docker image"
docker push "${IMAGE}"

echo "==> Deploying to Cloud Run"
gcloud run deploy "${SERVICE_NAME}" \
  --image="${IMAGE}" \
  --platform=managed \
  --region="${REGION}" \
  --project="${PROJECT_ID}" \
  --allow-unauthenticated \
  --port=8080 \
  --set-env-vars="ENVIRONMENT=production,DATABASE_URL=${DATABASE_URL}"

SERVICE_URL="$(gcloud run services describe "${SERVICE_NAME}" \
  --platform=managed \
  --region="${REGION}" \
  --project="${PROJECT_ID}" \
  --format='value(status.url)')"

echo
echo "Deployment successful!"
echo "Service URL: ${SERVICE_URL}"
echo

echo "==> Testing /health"
curl -sS -f "${SERVICE_URL}/health"
echo
echo
echo "Health check passed."
