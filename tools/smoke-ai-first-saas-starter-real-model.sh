#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
TEMPLATE_DIR="$REPO_ROOT/templates/ai-first-saas-starter"
SCAFFOLD_SCRIPT="$REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh"
TARGET_DIR=""
KEEP=false
APP_NAME="Real Model Smoke Starter"
APP_SLUG="real-model-smoke-starter"
BASE_PACKAGE="ai.first"
MAVEN_GROUP_ID="ai.first"

print_help() {
  cat <<'EOF'
Run the optional real model provider smoke for the AI-first SaaS starter.

Usage:
  tools/smoke-ai-first-saas-starter-real-model.sh [options]

Options:
  --target <dir>          Use an existing scaffolded target or create one there.
  --keep                  Keep generated temp target after the smoke.
  --app-name <name>       Rendered app name. Default: Real Model Smoke Starter
  --app-slug <slug>       Rendered app slug. Default: real-model-smoke-starter
  --base-package <pkg>    Java base package. Default: ai.first
  --maven-group-id <id>   Maven group id. Default: ai.first
  --help                  Show this help text

Behavior:
  - If OPENAI_API_KEY is absent or blank, the command exits 0 and reports an
    Akka Agent smoke skip that is safe for CI.
  - If provider env is present, it runs a targeted JUnit smoke that submits a
    workstream message through backend WorkstreamService, invokes the
    ComponentClient-backed WorkstreamRuntimeAgent, and verifies provider-backed
    markdown_response plus prompt/model/work trace shape without exposing
    provider secrets in DTOs, frontend env files, static assets, or smoke logs.
EOF
}

fail() {
  printf '[starter-real-model-smoke][error] %s\n' "$*" >&2
  exit 1
}

log() {
  printf '[starter-real-model-smoke] %s\n' "$*"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --target)
      [[ $# -ge 2 ]] || fail "Missing value for --target"
      TARGET_DIR="$2"
      shift 2
      ;;
    --keep)
      KEEP=true
      shift
      ;;
    --app-name)
      [[ $# -ge 2 ]] || fail "Missing value for --app-name"
      APP_NAME="$2"
      shift 2
      ;;
    --app-slug)
      [[ $# -ge 2 ]] || fail "Missing value for --app-slug"
      APP_SLUG="$2"
      shift 2
      ;;
    --base-package)
      [[ $# -ge 2 ]] || fail "Missing value for --base-package"
      BASE_PACKAGE="$2"
      shift 2
      ;;
    --maven-group-id)
      [[ $# -ge 2 ]] || fail "Missing value for --maven-group-id"
      MAVEN_GROUP_ID="$2"
      shift 2
      ;;
    --help|-h)
      print_help
      exit 0
      ;;
    *)
      fail "Unknown option: $1"
      ;;
  esac
done

if [[ -z "${OPENAI_API_KEY:-}" || -z "${OPENAI_API_KEY//[[:space:]]/}" ]]; then
  log "Akka Agent smoke skipped: OPENAI_API_KEY is not set or is blank."
  log "To enable real provider validation: export OPENAI_API_KEY, optionally OPENAI_MODEL_ID/OPENAI_API_BASE_URL/OPENAI_REQUEST_TIMEOUT_SECONDS, then rerun this command."
  exit 0
fi

[[ -x "$SCAFFOLD_SCRIPT" ]] || fail "Scaffold script is not executable: $SCAFFOLD_SCRIPT"
[[ -d "$TEMPLATE_DIR" ]] || fail "Template directory not found: $TEMPLATE_DIR"
command -v mvn >/dev/null 2>&1 || fail "mvn is required"

if [[ -z "$TARGET_DIR" ]]; then
  TARGET_DIR="$(mktemp -d "${TMPDIR:-/tmp}/ai-first-saas-starter-real-model.XXXXXX")"
else
  mkdir -p "$TARGET_DIR"
  TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
  KEEP=true
fi

cleanup() {
  if [[ "$KEEP" == true ]]; then
    log "Kept smoke target: $TARGET_DIR"
  else
    rm -rf "$TARGET_DIR"
  fi
}
trap cleanup EXIT

if [[ ! -f "$TARGET_DIR/pom.xml" ]]; then
  if [[ -n "$(find "$TARGET_DIR" -mindepth 1 -maxdepth 1 -print -quit 2>/dev/null)" ]]; then
    fail "--target must be an existing scaffolded project with pom.xml or an empty/absent directory: $TARGET_DIR"
  fi
  log "Scaffolding starter into $TARGET_DIR"
  "$SCAFFOLD_SCRIPT" \
    --target "$TARGET_DIR" \
    --template-dir "$TEMPLATE_DIR" \
    --app-name "$APP_NAME" \
    --app-slug "$APP_SLUG" \
    --base-package "$BASE_PACKAGE" \
    --maven-group-id "$MAVEN_GROUP_ID"
else
  log "Using existing scaffolded target: $TARGET_DIR"
fi

SMOKE_LOG="$(mktemp "${TMPDIR:-/tmp}/ai-first-saas-starter-real-model-smoke.XXXXXX.log")"
cleanup_log() {
  rm -f "$SMOKE_LOG"
}
trap 'cleanup; cleanup_log' EXIT

redact_smoke_log() {
  python3 - "$SMOKE_LOG" "${OPENAI_API_KEY}" <<'PY'
from pathlib import Path
import sys
path = Path(sys.argv[1])
secret = sys.argv[2]
text = path.read_text(errors="replace") if path.exists() else ""
if secret:
    text = text.replace(secret, "[REDACTED_OPENAI_API_KEY]")
sys.stdout.write(text)
PY
}

scan_path_for_provider_secret() {
  local path="$1"
  [[ -e "$path" ]] || return 0
  if grep -RIF --line-number -- "${OPENAI_API_KEY}" "$path" >/dev/null 2>&1; then
    fail "Provider secret was found in $path"
  fi
}

log "Running real provider Akka Agent smoke through backend workstream message submission"
if ! ( cd "$TARGET_DIR" && mvn -DrealModelProviderSmoke=true -Dtest=RealModelProviderSmokeTest test ) >"$SMOKE_LOG" 2>&1; then
  log "Real provider Akka Agent smoke failed; sanitized Maven output follows."
  redact_smoke_log >&2
  exit 1
fi

if grep -F -- "${OPENAI_API_KEY}" "$SMOKE_LOG" >/dev/null 2>&1; then
  fail "Provider secret was found in smoke logs; raw log suppressed."
fi

scan_path_for_provider_secret "$TARGET_DIR/frontend/.env"
scan_path_for_provider_secret "$TARGET_DIR/frontend/.env.local"
scan_path_for_provider_secret "$TARGET_DIR/frontend/.env.development"
scan_path_for_provider_secret "$TARGET_DIR/frontend/.env.production"
scan_path_for_provider_secret "$TARGET_DIR/src/main/resources/static-resources"

log "Real provider Akka Agent smoke passed without provider-secret leaks in smoke logs, frontend env, or static assets"
