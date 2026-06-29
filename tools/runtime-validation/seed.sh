#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: tools/runtime-validation/seed.sh base-organization [--base-url http://localhost:9000]

Seeds the local Akka runtime with the base organization/personas required by
runtime-validation scenarios. The Akka app must have been started with
`tools/runtime-validation/start-local.sh`, which generates .runtime-validation/local.env
and enables the local-only seed endpoint.
USAGE
}

if [[ $# -lt 1 ]]; then usage >&2; exit 2; fi
SETUP="$1"
shift
BASE_URL="${RUNTIME_VALIDATION_BASE_URL:-http://localhost:9000}"
while [[ $# -gt 0 ]]; do
  case "$1" in
    --base-url) BASE_URL="${2:-}"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "unknown argument: $1" >&2; usage >&2; exit 2 ;;
  esac
done

if [[ "$SETUP" != "base-organization" ]]; then
  echo "unsupported runtime-validation setup: $SETUP" >&2
  exit 2
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

load_env_file() {
  local file="$1"
  if [[ -f "$file" ]]; then
    set -a
    # shellcheck disable=SC1090
    source "$file"
    set +a
  fi
}
load_env_file ".runtime-validation/local.env"

if [[ -z "${RUNTIME_VALIDATION_SEED_TOKEN:-}" ]]; then
  echo "missing RUNTIME_VALIDATION_SEED_TOKEN; run tools/runtime-validation/start-local.sh first" >&2
  exit 78
fi

correlation_id="rv-seed-base-organization-$(date -u +%Y%m%dT%H%M%SZ)"
response_file="$(mktemp)"
status="$(curl -sS -o "$response_file" -w '%{http_code}' \
  -X POST \
  -H "X-Runtime-Validation-Seed-Token: ${RUNTIME_VALIDATION_SEED_TOKEN}" \
  -H "X-Correlation-Id: ${correlation_id}" \
  "${BASE_URL%/}/internal/runtime-validation/seed/base-organization")"
cat "$response_file"
rm -f "$response_file"
printf '\n'
if [[ "$status" -lt 200 || "$status" -ge 300 ]]; then
  echo "seed failed with HTTP $status" >&2
  exit 1
fi
