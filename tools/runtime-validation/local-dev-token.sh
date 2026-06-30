#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: tools/runtime-validation/local-dev-token.sh --email saas.admin@example.test [--base-url http://localhost:9000]

Obtains a local-dev runtime-validation bearer token for a seeded test email.
The app must be running with APP_AUTH_MODE=local-dev. Use the printed token as
Authorization: Bearer <token> for normal protected local APIs only.
USAGE
}

EMAIL=""
BASE_URL="${RUNTIME_VALIDATION_BASE_URL:-http://localhost:9000}"
while [[ $# -gt 0 ]]; do
  case "$1" in
    --email|--user) EMAIL="${2:-}"; shift 2 ;;
    --base-url) BASE_URL="${2:-}"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "unknown argument: $1" >&2; usage >&2; exit 2 ;;
  esac
done

if [[ -z "$EMAIL" ]]; then
  echo "--email is required" >&2
  usage >&2
  exit 2
fi

body="$(python3 - "$EMAIL" <<'PY'
import json, sys
print(json.dumps({"email": sys.argv[1]}))
PY
)"
response_file="$(mktemp)"
status="$(curl -sS -o "$response_file" -w '%{http_code}' \
  -X POST \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data "$body" \
  "${BASE_URL%/}/api/dev/auth/sign-in")"
if [[ "$status" -lt 200 || "$status" -ge 300 ]]; then
  cat "$response_file" >&2
  rm -f "$response_file"
  echo "local-dev sign-in failed with HTTP $status" >&2
  exit 1
fi
python3 - "$response_file" <<'PY'
import json, sys
with open(sys.argv[1], 'r', encoding='utf-8') as f:
    print(json.load(f)['accessToken'])
PY
rm -f "$response_file"
