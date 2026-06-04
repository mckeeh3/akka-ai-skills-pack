#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Validate workstream surface contracts in an app-description tree.

Usage:
  tools/validate-surface-contracts.sh [app-description-dir]

Default app-description-dir: app-description

Checks:
  - surfaces index exists
  - every contract referenced from the index exists
  - every surface-contract markdown file has core contract sections
  - every contract includes surface-id, type/version, owner functional agent, payload, actions, states, auth/security, and tests
  - traceability surface-to-capability map exists and mentions each contract surface id
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

ROOT="${1:-app-description}"
INDEX="$ROOT/12-workstreams/surfaces-index.md"
CONTRACT_DIR="$ROOT/12-workstreams/surface-contracts"
TRACE_MAP="$ROOT/70-traceability/surface-to-capability-map.md"

failures=0
warn() { printf '[validate-surface-contracts][warn] %s\n' "$*" >&2; }
fail() { printf '[validate-surface-contracts][error] %s\n' "$*" >&2; failures=$((failures + 1)); }

[[ -f "$INDEX" ]] || fail "missing surfaces index: $INDEX"
[[ -d "$CONTRACT_DIR" ]] || fail "missing surface contracts directory: $CONTRACT_DIR"
[[ -f "$TRACE_MAP" ]] || fail "missing surface-to-capability map: $TRACE_MAP"

if [[ -f "$INDEX" ]]; then
  while IFS= read -r ref; do
    [[ -n "$ref" ]] || continue
    [[ -f "$ROOT/12-workstreams/$ref" ]] || fail "index references missing contract: $ref"
  done < <(grep -Eo 'surface-contracts/[^` |)]+' "$INDEX" | sort -u)
fi

check_pattern() {
  local file="$1" pattern="$2" description="$3"
  if ! grep -Eiq -- "$pattern" "$file"; then
    fail "$(realpath --relative-to=. "$file" 2>/dev/null || printf '%s' "$file") missing $description"
  fi
}

if [[ -d "$CONTRACT_DIR" ]]; then
  shopt -s nullglob
  contract_files=("$CONTRACT_DIR"/*.md)
  for file in "${contract_files[@]}"; do
    surface_id=""
    surface_id="$(grep -Eio 'surface-id:[[:space:]]*`?[-._a-z0-9]+' "$file" | head -1 | sed -E 's/.*surface-id:[[:space:]]*`?//I' || true)"
    [[ -n "$surface_id" ]] || fail "$(realpath --relative-to=. "$file" 2>/dev/null || printf '%s' "$file") missing parseable surface-id"

    check_pattern "$file" 'surface-id:' 'surface-id field'
    check_pattern "$file" 'type/version:' 'type/version field'
    check_pattern "$file" 'owner functional agent|owner functional agents:' 'owner functional agent field'
    check_pattern "$file" 'Payload summary|Payload contract|payload schema' 'payload section'
    check_pattern "$file" 'Allowed actions|Actions and capabilities|## Actions' 'actions section'
    check_pattern "$file" 'Capability hint|capability id|capability links|Capability/action' 'capability mapping details'
    check_pattern "$file" 'Qualified exposure|browser-tool|agent-tool|governed-tool' 'qualified governed-tool exposure details'
    check_pattern "$file" 'UI states|## States|loading' 'state section'
    check_pattern "$file" 'forbidden|denial' 'forbidden/denial behavior'
    check_pattern "$file" 'Auth/security|authorization|tenant' 'auth/security section'
    check_pattern "$file" 'trace|correlation' 'trace/correlation fields'
    check_pattern "$file" 'Rendering.*tests|Tests|capability tests' 'test section'
    check_pattern "$file" 'redaction|redacted|omitted' 'redaction rules'

    if [[ -n "$surface_id" && -f "$INDEX" ]] && ! grep -q -- "\`$surface_id\`" "$INDEX"; then
      fail "surface id $surface_id not listed in $INDEX"
    fi
    if [[ -n "$surface_id" && -f "$TRACE_MAP" ]] && ! grep -q -- "\`$surface_id\`" "$TRACE_MAP"; then
      fail "surface id $surface_id not mapped in $TRACE_MAP"
    fi
  done
  if [[ "${#contract_files[@]}" -eq 0 ]]; then
    warn "no surface contract markdown files found under $CONTRACT_DIR"
  fi
  shopt -u nullglob
fi

if [[ "$failures" -gt 0 ]]; then
  printf '[validate-surface-contracts][error] %s failure(s)\n' "$failures" >&2
  exit 1
fi

printf '[validate-surface-contracts] validation passed: %s\n' "$ROOT"
