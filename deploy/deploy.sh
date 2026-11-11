#!/usr/bin/env bash
# Runs on the self-hosted runner ON the Huawei box (user ghrunner, docker group).
# Brings up Postgres always; starts the app only once the required CAMS secrets are present,
# so the pipeline stays green while those are still being sourced from the CAMS team.
set -euo pipefail
umask 077   # any file we create (.env, .env.tmp) is owner-only — it holds DB + CAMS + mail secrets
cd "$(dirname "$0")"

IMAGE_TAG="${1:?usage: deploy.sh <image-tag>}"
REGISTRY="ghcr.io"
REPO="ghcr.io/cbi-technologies-projects/fuelflow-backend"
# Export the FULL repo:tag so docker-compose interpolates ${IMAGE} correctly.
# (The workflow-level env IMAGE=<repo-only> leaks into this job and would otherwise
#  win over the .env value, making compose pull <repo>:latest.)
export IMAGE="${REPO}:${IMAGE_TAG}"
APPDIR="/opt/fuelflow"
install -d "$APPDIR"
cp docker-compose.yml "$APPDIR/docker-compose.yml"
cd "$APPDIR"
COMPOSE="docker compose -f docker-compose.yml"

# --- .env: generate + persist DB password once (so Postgres data survives redeploys) ---
touch .env; chmod 600 .env
if ! grep -q '^DB_PASSWORD=' .env; then echo "DB_PASSWORD=$(openssl rand -hex 24)" >> .env; fi
# rewrite the non-DB keys each deploy from the runner env (GitHub secrets)
# keep ONLY DB_PASSWORD from the prior .env; all IMAGE/CAMS_*/MAIL_* lines are re-written fresh
grep -E '^DB_PASSWORD=' .env > .env.tmp && mv .env.tmp .env
chmod 600 .env
{
  echo "IMAGE=${REPO}:${IMAGE_TAG}"
  echo "CAMS_REQUEST_PROCESSOR=${CAMS_REQUEST_PROCESSOR:-}"
  echo "CAMS_JWT_SECRET=${CAMS_JWT_SECRET:-}"
  echo "CAMS_WEBHOOK_SECRET=${CAMS_WEBHOOK_SECRET:-}"
  echo "CAMS_WEBHOOK_PREVIOUS_SECRET=${CAMS_WEBHOOK_PREVIOUS_SECRET:-}"
  echo "CAMS_INSTITUTION_ID=${CAMS_INSTITUTION_ID:-}"
  echo "MAIL_USERNAME=${MAIL_USERNAME:-}"
  echo "MAIL_PASSWORD=${MAIL_PASSWORD:-}"
} >> .env

echo ">> GHCR login + pull"
echo "${GHCR_TOKEN}" | docker login "$REGISTRY" -u "${GHCR_USER}" --password-stdin
$COMPOSE pull

echo ">> Start Postgres"
$COMPOSE up -d postgres

# --- only start the app when the mandatory CAMS secrets are present ---
if [ -z "${CAMS_JWT_SECRET:-}" ] || [ -z "${CAMS_WEBHOOK_SECRET:-}" ] || [ -z "${CAMS_REQUEST_PROCESSOR:-}" ]; then
  echo "::warning::App NOT started — set repo secrets CAMS_JWT_SECRET, CAMS_WEBHOOK_SECRET, CAMS_REQUEST_PROCESSOR (and MAIL_*). Postgres is up; re-run after adding them."
  docker logout "$REGISTRY" || true
  exit 0
fi

echo ">> Start app ${REPO}:${IMAGE_TAG}"
$COMPOSE up -d app
ok=""
for _ in $(seq 1 60); do
  if curl -fsS http://127.0.0.1:8080/actuator/health >/dev/null 2>&1; then ok=1; break; fi
  sleep 3
done
docker logout "$REGISTRY" || true
if [ -z "$ok" ]; then
  echo "::error::/actuator/health not healthy after deploy"; $COMPOSE logs --tail 100 app; exit 1
fi
echo ">> Deployed ${REPO}:${IMAGE_TAG} — /actuator/health OK"
