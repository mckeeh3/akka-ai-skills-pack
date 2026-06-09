#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Validate workstream surface contracts in an app-description tree.

Usage:
  tools/validate-surface-contracts.sh [--mode template|implementation] [app-description-dir]

Default mode: implementation
Default app-description-dir: app-description

Modes:
  template        Validate process/template baselines. Allows deferred typed surfaces and compact mappings.
  implementation Validate app-level contracts. Readiness-aware: capability-ready and higher workstreams must
                 not rely on unresolved deferred result surfaces for in-scope action mappings.

Checks:
  - surfaces index exists
  - every contract referenced from the index exists
  - every surface-contract markdown file has core contract sections
  - every contract includes surface-id, type/version, a single owner functional agent, payload, action mapping,
    actions, states, auth/security, redaction, traces/correlation, and tests
  - surface graph and traceability surface-to-capability map exist and mention each contract surface id
  - template mode requires deferred typed surfaces file when deferred/fallback surfaces are referenced
EOF
}

MODE="implementation"
ARGS=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --help|-h)
      usage
      exit 0
      ;;
    --mode)
      [[ $# -ge 2 ]] || { printf '[validate-surface-contracts][error] --mode requires template or implementation\n' >&2; exit 2; }
      MODE="$2"
      shift 2
      ;;
    --mode=*)
      MODE="${1#--mode=}"
      shift
      ;;
    *)
      ARGS+=("$1")
      shift
      ;;
  esac
done

if [[ "$MODE" != "template" && "$MODE" != "implementation" ]]; then
  printf '[validate-surface-contracts][error] invalid --mode: %s\n' "$MODE" >&2
  exit 2
fi

ROOT="${ARGS[0]:-app-description}"
INDEX="$ROOT/12-workstreams/surfaces-index.md"
CONTRACT_DIR="$ROOT/12-workstreams/surface-contracts"
TRACE_MAP="$ROOT/70-traceability/surface-to-capability-map.md"
SURFACE_GRAPH="$ROOT/12-workstreams/surface-graph.md"
DEFERRED="$ROOT/12-workstreams/deferred-typed-surfaces.md"
MANIFEST="$ROOT/12-workstreams/workstream-manifest.json"

failures=0
warn() { printf '[validate-surface-contracts][warn] %s\n' "$*" >&2; }
fail() { printf '[validate-surface-contracts][error] %s\n' "$*" >&2; failures=$((failures + 1)); }
rel() { realpath --relative-to=. "$1" 2>/dev/null || printf '%s' "$1"; }

[[ -f "$INDEX" ]] || fail "missing surfaces index: $INDEX"
[[ -d "$CONTRACT_DIR" ]] || fail "missing surface contracts directory: $CONTRACT_DIR"
[[ -f "$TRACE_MAP" ]] || fail "missing surface-to-capability map: $TRACE_MAP"
[[ -f "$SURFACE_GRAPH" ]] || fail "missing surface graph: $SURFACE_GRAPH"

if [[ -f "$INDEX" ]]; then
  while IFS= read -r ref; do
    [[ -n "$ref" ]] || continue
    [[ -f "$ROOT/12-workstreams/$ref" ]] || fail "index references missing contract: $ref"
  done < <(grep -Eo 'surface-contracts/[^` |)]+' "$INDEX" | sort -u)
fi

check_pattern() {
  local file="$1" pattern="$2" description="$3"
  if ! grep -Eiq -- "$pattern" "$file"; then
    fail "$(rel "$file") missing $description"
  fi
}

all_text_has_deferred_refs=false
if { [[ -f "$INDEX" ]] && grep -Eiq 'deferred|fallback' "$INDEX"; } || { [[ -d "$CONTRACT_DIR" ]] && grep -REiq 'deferred `|fallback|Deferred' "$CONTRACT_DIR"; }; then
  all_text_has_deferred_refs=true
fi
if [[ "$all_text_has_deferred_refs" == true && ! -f "$DEFERRED" ]]; then
  fail "deferred/fallback surfaces are referenced but missing: $DEFERRED"
fi

if [[ -d "$CONTRACT_DIR" ]]; then
  shopt -s nullglob
  contract_files=("$CONTRACT_DIR"/*.md)
  for file in "${contract_files[@]}"; do
    surface_id=""
    surface_id="$(grep -Eio 'surface-id:[[:space:]]*`?[-._a-z0-9]+' "$file" | head -1 | sed -E 's/.*surface-id:[[:space:]]*`?//I' || true)"
    [[ -n "$surface_id" ]] || fail "$(rel "$file") missing parseable surface-id"

    check_pattern "$file" 'surface-id:' 'surface-id field'
    check_pattern "$file" 'type/version:' 'type/version field'
    check_pattern "$file" 'owner functional agent:' 'single owner functional agent field'
    if grep -Eiq 'owner functional agents:' "$file"; then
      fail "$(rel "$file") uses plural/ambiguous owner functional agents field; use one owner plus reusable-by"
    fi
    check_pattern "$file" 'reusable by|reusable surfaces|composition note' 'reuse/composition field'
    check_pattern "$file" 'Payload summary|Payload contract|payload schema' 'payload section'
    check_pattern "$file" 'Compact payload schema|type .*Data|```ts' 'compact typed payload schema'
    check_pattern "$file" 'Allowed actions|Actions and capabilities|## Actions' 'actions section'
    check_pattern "$file" '## Action mapping' 'action mapping section'
    check_pattern "$file" 'actionId' 'stable actionId mapping'
    check_pattern "$file" 'browserToolId' 'browserToolId mapping'
    check_pattern "$file" 'governedToolId' 'governedToolId mapping'
    check_pattern "$file" 'capabilityId' 'capabilityId mapping'
    check_pattern "$file" 'resultSurfaceId' 'resultSurfaceId mapping'
    check_pattern "$file" 'idempotency' 'idempotency mapping'
    check_pattern "$file" 'traceRequired' 'traceRequired mapping'
    check_pattern "$file" 'Capability hint|capability id|capability links|Capability/action|capabilityId' 'capability mapping details'
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
    if [[ -n "$surface_id" && -f "$SURFACE_GRAPH" ]] && ! grep -q -- "\`$surface_id\`" "$SURFACE_GRAPH"; then
      fail "surface id $surface_id not present in $SURFACE_GRAPH"
    fi
  done
  if [[ "${#contract_files[@]}" -eq 0 ]]; then
    warn "no surface contract markdown files found under $CONTRACT_DIR"
  fi
  shopt -u nullglob
fi

if [[ "$MODE" == "implementation" && -f "$MANIFEST" ]]; then
  if python3 - "$ROOT" <<'PY'
import json, sys
from pathlib import Path
root=Path(sys.argv[1])
manifest=root/'12-workstreams'/'workstream-manifest.json'
try:
    data=json.loads(manifest.read_text())
except Exception:
    sys.exit(0)
strict={'capability-ready','expertise-ready','runtime-ready','production-ready'}
strict_ws=[w for w in data.get('workstreams',[]) if w.get('readiness') in strict]
if not strict_ws:
    sys.exit(0)
contract_dir=root/'12-workstreams'/'surface-contracts'
text='\n'.join(p.read_text(errors='ignore') for p in contract_dir.glob('*.md')) if contract_dir.exists() else ''
for w in strict_ws:
    wid=w.get('workstreamId','<unknown>')
    mappings=w.get('surfaceActionMappings') or []
    if not mappings:
        print(f'{wid}: missing surfaceActionMappings at readiness {w.get("readiness")}', file=sys.stderr)
        sys.exit(1)
    for m in mappings:
        rs=str(m.get('resultSurfaceId',''))
        if rs.startswith('deferred') or (rs and f'deferred `{rs}`' in text):
            print(f'{wid}: capability-ready mapping relies on deferred result surface {rs}', file=sys.stderr)
            sys.exit(1)
sys.exit(0)
PY
  then
    :
  else
    fail "implementation mode failed readiness-aware deferred-surface checks"
  fi
fi

if [[ "$failures" -gt 0 ]]; then
  printf '[validate-surface-contracts][error] %s failure(s)\n' "$failures" >&2
  exit 1
fi

printf '[validate-surface-contracts] validation passed: %s (mode=%s)\n' "$ROOT" "$MODE"
