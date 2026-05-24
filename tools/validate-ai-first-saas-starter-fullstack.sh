#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
TEMPLATE_DIR="$REPO_ROOT/templates/ai-first-saas-starter"
SCAFFOLD_SCRIPT="$REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh"
MODEL_SMOKE_SCRIPT="$REPO_ROOT/tools/smoke-ai-first-saas-starter-real-model.sh"
KEEP=false
TARGET_DIR=""
APP_NAME="Fullstack Smoke Starter"
APP_SLUG="fullstack-smoke-starter"
BASE_PACKAGE="ai.first"
MAVEN_GROUP_ID="ai.first"

print_help() {
  cat <<'EOF'
Validate the AI-first SaaS starter fullstack scaffold.

Usage:
  tools/validate-ai-first-saas-starter-fullstack.sh [options]

Options:
  --target <dir>          Use this target directory instead of a temp directory.
                          The directory must be empty or absent.
  --keep                  Keep the generated temp directory after validation.
  --app-name <name>       Rendered app name. Default: Fullstack Smoke Starter
  --app-slug <slug>       Rendered app slug. Default: fullstack-smoke-starter
  --base-package <pkg>    Java base package. Default: ai.first
  --maven-group-id <id>   Maven group id. Default: ai.first
  --help                  Show this help text

The validation scaffolds from templates/ai-first-saas-starter, runs backend
Maven tests, installs/tests/typechecks/builds the React/Vite frontend, verifies
Akka static resources, scans built assets for obvious backend secret leaks, and
runs the optional real provider smoke in skip mode unless OPENAI_API_KEY is set.
EOF
}

fail() {
  printf '[starter-fullstack][error] %s\n' "$*" >&2
  exit 1
}

log() {
  printf '[starter-fullstack] %s\n' "$*"
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

[[ -x "$SCAFFOLD_SCRIPT" ]] || fail "Scaffold script is not executable: $SCAFFOLD_SCRIPT"
[[ -x "$MODEL_SMOKE_SCRIPT" ]] || fail "Provider smoke script is not executable: $MODEL_SMOKE_SCRIPT"
[[ -d "$TEMPLATE_DIR" ]] || fail "Template directory not found: $TEMPLATE_DIR"
command -v mvn >/dev/null 2>&1 || fail "mvn is required"
command -v npm >/dev/null 2>&1 || fail "npm is required"
command -v grep >/dev/null 2>&1 || fail "grep is required"

if [[ -z "$TARGET_DIR" ]]; then
  TARGET_DIR="$(mktemp -d "${TMPDIR:-/tmp}/ai-first-saas-starter-fullstack.XXXXXX")"
else
  if [[ -e "$TARGET_DIR" && -n "$(find "$TARGET_DIR" -mindepth 1 -maxdepth 1 -print -quit 2>/dev/null)" ]]; then
    fail "--target must be empty or absent: $TARGET_DIR"
  fi
  mkdir -p "$TARGET_DIR"
  TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
  KEEP=true
fi

cleanup() {
  if [[ "$KEEP" == true ]]; then
    log "Kept scaffold target: $TARGET_DIR"
  else
    rm -rf "$TARGET_DIR"
  fi
}
trap cleanup EXIT

require_path() {
  local path="$1"
  [[ -e "$TARGET_DIR/$path" ]] || fail "Expected rendered path missing: $path"
}

log "Scaffolding starter into $TARGET_DIR"
"$SCAFFOLD_SCRIPT" \
  --target "$TARGET_DIR" \
  --template-dir "$TEMPLATE_DIR" \
  --app-name "$APP_NAME" \
  --app-slug "$APP_SLUG" \
  --base-package "$BASE_PACKAGE" \
  --maven-group-id "$MAVEN_GROUP_ID"

log "Verifying rendered backend, frontend, and planning paths"
require_path "pom.xml"
require_path "src/main/java/${BASE_PACKAGE//.//}/api"
require_path "src/main/resources"
require_path "src/test/java/${BASE_PACKAGE//.//}"
require_path "frontend/package.json"
require_path "frontend/src/main.tsx"
require_path "app-description/README.md"
require_path "specs/scaffold-report.md"

log "Running scaffolded backend tests"
( cd "$TARGET_DIR" && mvn test )

log "Installing frontend dependencies"
# The template source can exist beside a developer's local node_modules cache; if such
# files are copied by the generic scaffold renderer, discard them so this smoke test
# proves a clean install and restores executable package-bin permissions.
rm -rf "$TARGET_DIR/frontend/node_modules"
( cd "$TARGET_DIR/frontend" && npm install )

log "Running frontend tests"
( cd "$TARGET_DIR/frontend" && npm test -- --run )

log "Running frontend typecheck"
( cd "$TARGET_DIR/frontend" && npm run typecheck )

log "Building frontend into Akka static resources"
( cd "$TARGET_DIR/frontend" && npm run build )

STATIC_DIR="$TARGET_DIR/src/main/resources/static-resources"
[[ -d "$STATIC_DIR" ]] || fail "Static resource directory was not created: src/main/resources/static-resources"
[[ -f "$STATIC_DIR/index.html" ]] || fail "Built static index missing: src/main/resources/static-resources/index.html"
[[ -d "$STATIC_DIR/assets" ]] || fail "Built static assets directory missing: src/main/resources/static-resources/assets"
if ! find "$STATIC_DIR/assets" -type f \( -name '*.js' -o -name '*.css' \) -print -quit | grep -q .; then
  fail "Built static assets directory does not contain JS/CSS assets"
fi

log "Scanning built static assets for backend secret leaks"
SECRET_PATTERN='(WORKOS_API_KEY|WORKOS_CLIENT_SECRET|RESEND_API_KEY|OPENAI_API_KEY|ANTHROPIC_API_KEY|INVITE_EMAIL_FROM|ADMIN_USERS|BEGIN[[:space:]]+(RSA |EC |OPENSSH |)PRIVATE KEY|sk-[A-Za-z0-9_-]{20,}|whsec_[A-Za-z0-9_-]+)'
if grep -RIE --line-number "$SECRET_PATTERN" "$STATIC_DIR"; then
  fail "Potential backend secret marker found in built static assets"
fi

log "Running optional real model provider smoke or reporting provider-skip state"
"$MODEL_SMOKE_SCRIPT" --target "$TARGET_DIR" --base-package "$BASE_PACKAGE" --maven-group-id "$MAVEN_GROUP_ID"

log "Fullstack starter validation passed"
log "Validated target: $TARGET_DIR"
