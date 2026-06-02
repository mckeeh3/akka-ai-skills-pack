#!/usr/bin/env bash
set -euo pipefail

print_help() {
  cat <<'EOF'
Scan rendered AI-first SaaS starter static assets for browser-visible backend secret markers.

Usage:
  tools/scan-ai-first-saas-static-assets.sh <static-resources-dir>

The directory is normally src/main/resources/static-resources in a rendered scaffold
after `cd frontend && npm run build`.
EOF
}

fail() {
  printf '[static-asset-scan][error] %s\n' "$*" >&2
  exit 1
}

log() {
  printf '[static-asset-scan] %s\n' "$*"
}

if [[ $# -ne 1 || "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  print_help
  [[ $# -eq 1 && ("${1:-}" == "--help" || "${1:-}" == "-h") ]] && exit 0
  exit 2
fi

STATIC_DIR="$1"
[[ -d "$STATIC_DIR" ]] || fail "Static resources directory not found: $STATIC_DIR"
[[ -f "$STATIC_DIR/index.html" ]] || fail "Static index missing: $STATIC_DIR/index.html"
[[ -d "$STATIC_DIR/assets" ]] || fail "Static assets directory missing: $STATIC_DIR/assets"
if ! find "$STATIC_DIR/assets" -type f \( -name '*.js' -o -name '*.css' \) -print -quit | grep -q .; then
  fail "Static assets directory does not contain JS/CSS assets"
fi

SECRET_PATTERN='(WORKOS_API_KEY|WORKOS_CLIENT_SECRET|RESEND_API_KEY|OPENAI_API_KEY|ANTHROPIC_API_KEY|INVITE_EMAIL_FROM|ADMIN_USERS|BEGIN[[:space:]]+(RSA |EC |OPENSSH |)PRIVATE KEY|sk-[A-Za-z0-9_-]{20,}|whsec_[A-Za-z0-9_-]+)'
if grep -RIE --line-number "$SECRET_PATTERN" "$STATIC_DIR"; then
  fail "Potential backend secret marker found in built static assets"
fi

log "Static asset scan passed: $STATIC_DIR"
