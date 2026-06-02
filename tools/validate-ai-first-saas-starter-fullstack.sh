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
command -v rg >/dev/null 2>&1 || fail "rg (ripgrep) is required"

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

require_grep() {
  local pattern="$1"
  local file="$2"
  local description="$3"
  if ! grep -Eq "$pattern" "$file"; then
    fail "Missing validation marker for $description in $file"
  fi
}

scan_forbidden_runtime_substitutes() {
  local adapter_matches
  adapter_matches="$(find "$TARGET_DIR/src/main/java" -type f | rg -i 'LocalDemo|FailClosed.*Repository|FailClosed.*Sink' || true)"
  if [[ -n "$adapter_matches" ]]; then
    printf '%s\n' "$adapter_matches" >&2
    fail "Production backend source contains substitute adapter files"
  fi

  local forbidden_storage_word
  forbidden_storage_word="in-""memory|In""Memory|IN_""MEMORY|in ""memory"

  if rg -n "$forbidden_storage_word" "$REPO_ROOT" \
    --glob '!**/.git/**' \
    --glob '!**/.git-bak/**' \
    --glob '!**/node_modules/**' \
    --glob '!**/target/**' \
    --glob '!dist/**'; then
    fail "Repository contains forbidden non-Akka storage terminology"
  fi

  local runtime_pattern='new[[:space:]]+LocalDemo|AI_FIRST_SAAS_LOCAL_DEMO|local/demo repositories|reference repositories behind the same ports|downstream product hardening can continue replacing|optional hardening|fixtureWorkstream|FixtureWorkstream|FixtureApiClient|FixtureRealtimeClient'
  if rg -n "$runtime_pattern" \
    "$TARGET_DIR/src/main/java" \
    "$TARGET_DIR/frontend/src" \
    "$TARGET_DIR/README.md" \
    --glob '!**/__tests__/**' \
    --glob '!**/*.test.*' \
    --glob '!**/node_modules/**' \
    --glob '!**/target/**'; then
    fail "Rendered starter production runtime contains forbidden substitute-runtime markers"
  fi

  local canonical_doc_pattern='reference repositories behind the same ports|downstream product hardening can continue replacing|optional hardening'
  if rg -n "$canonical_doc_pattern" \
    "$REPO_ROOT/specs/ai-first-saas-starter-app-template/final-acceptance-review.md" \
    "$REPO_ROOT/specs/ai-first-saas-starter-app-template/migration-completion-summary.md" \
    "$REPO_ROOT/templates/ai-first-saas-starter/README.md"; then
    fail "Canonical starter acceptance/docs contain stale substitute-runtime qualification wording"
  fi
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

log "Verifying managed-agent runtime tool registration gates"
WORKSTREAM_AGENT_PATH="src/main/java/${BASE_PACKAGE//.//}/application/agentfoundation/WorkstreamRuntimeAgent.java"
require_path "$WORKSTREAM_AGENT_PATH"
require_grep '\.tools\(runtimeTools\.runtimeTools\(\)\)' "$TARGET_DIR/$WORKSTREAM_AGENT_PATH" "WorkstreamRuntimeAgent effects().tools(runtimeTools) registration"
require_grep 'AgentRuntimeToolResolver' "$TARGET_DIR/$WORKSTREAM_AGENT_PATH" "governed runtime tool resolver use"
require_grep 'readSkill|readReferenceDoc' "$TARGET_DIR/src/main/java/${BASE_PACKAGE//.//}/application/agentfoundation/AgentRuntimeLoaderTools.java" "governed loader tool methods"

log "Scanning rendered starter for forbidden substitute-runtime markers"
scan_forbidden_runtime_substitutes

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
