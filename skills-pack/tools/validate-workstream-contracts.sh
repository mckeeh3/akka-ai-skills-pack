#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Validate workstream contracts in an app-description tree.

Usage:
  tools/validate-workstream-contracts.sh [app-description-dir]

Default app-description-dir: app-description

Checks:
  - core 12-workstreams files exist
  - machine-readable workstream-manifest.json exists and is internally consistent
  - functional agents, workstream ids, attention/dashboard, retention, surfaces, and expertise are present
  - surface and functional-agent traceability maps exist
  - each functional agent has a matching expertise bundle when workstream expertise is in scope
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

ROOT="${1:-app-description}"
WORKSTREAM_DIR="$ROOT/12-workstreams"
TRACE_DIR="$ROOT/70-traceability"

failures=0
fail() { printf '[validate-workstream-contracts][error] %s\n' "$*" >&2; failures=$((failures + 1)); }

require_file() {
  local path="$1"
  [[ -f "$path" ]] || fail "missing required file: $path"
}

check_pattern() {
  local file="$1" pattern="$2" description="$3"
  if [[ -f "$file" ]] && ! grep -Eiq -- "$pattern" "$file"; then
    fail "$(realpath --relative-to=. "$file" 2>/dev/null || printf '%s' "$file") missing $description"
  fi
}

require_file "$WORKSTREAM_DIR/workstream-manifest.json"
require_file "$WORKSTREAM_DIR/functional-agents.md"
require_file "$WORKSTREAM_DIR/workstreams-and-retention.md"
require_file "$WORKSTREAM_DIR/attention-and-dashboards.md"
require_file "$WORKSTREAM_DIR/internal-agents.md"
require_file "$WORKSTREAM_DIR/surfaces-index.md"
require_file "$WORKSTREAM_DIR/foundation-workstream-completeness.md"
require_file "$WORKSTREAM_DIR/workstream-expertise/README.md"
require_file "$TRACE_DIR/functional-agent-to-capability-map.md"
require_file "$TRACE_DIR/surface-to-capability-map.md"

check_pattern "$WORKSTREAM_DIR/workstream-manifest.json" 'workstream-manifest/v1' 'workstream manifest version'
check_pattern "$WORKSTREAM_DIR/functional-agents.md" 'functional agent|functional/context-area agent' 'functional-agent terminology'
check_pattern "$WORKSTREAM_DIR/functional-agents.md" 'workstream id|workstreamId' 'workstream id mapping'
check_pattern "$WORKSTREAM_DIR/functional-agents.md" 'authorized|roles|capabilities|AuthContext' 'authority mapping'
check_pattern "$WORKSTREAM_DIR/functional-agents.md" 'default surface|dashboard' 'default dashboard/surface mapping'
check_pattern "$WORKSTREAM_DIR/functional-agents.md" 'icon' 'workstream icon metadata'

check_pattern "$WORKSTREAM_DIR/workstreams-and-retention.md" 'definition|instance' 'definition vs runtime instance semantics'
check_pattern "$WORKSTREAM_DIR/workstreams-and-retention.md" 'tenantId|selectedContextId|AuthContext' 'runtime scope fields'
check_pattern "$WORKSTREAM_DIR/workstreams-and-retention.md" 'retention|redaction' 'retention/redaction requirements'
check_pattern "$WORKSTREAM_DIR/workstreams-and-retention.md" 'trace|audit' 'trace/audit requirements'
check_pattern "$WORKSTREAM_DIR/workstreams-and-retention.md" 'readiness|runtime-ready|surface-ready|capability-ready' 'readiness labels'

check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'Attention|attention' 'attention model'
check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'WorkstreamAttentionSummary|AttentionItem' 'attention summary/item contract'
check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'dashboard' 'dashboard contract'
check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'left rail|My Account' 'aggregate/rail behavior'
check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'idempotency|producer' 'producer/idempotency rules'
check_pattern "$WORKSTREAM_DIR/attention-and-dashboards.md" 'lifecycle|acknowledge|resolve|dismiss|escalate' 'attention lifecycle'

check_pattern "$WORKSTREAM_DIR/internal-agents.md" 'internal agent|worker' 'internal agent graph'
check_pattern "$WORKSTREAM_DIR/internal-agents.md" 'AutonomousAgent|Agent' 'agent substrate guidance'
check_pattern "$WORKSTREAM_DIR/internal-agents.md" 'capability|governed-tool|ToolPermissionBoundary' 'governed internal authority'
check_pattern "$WORKSTREAM_DIR/internal-agents.md" 'progress|result|surface' 'progress/result surface behavior'
check_pattern "$WORKSTREAM_DIR/internal-agents.md" 'workerId|Worker id|worker id' 'internal worker id template'

check_pattern "$WORKSTREAM_DIR/foundation-workstream-completeness.md" 'Runtime evidence|Current gap' 'foundation completeness runtime/gap matrix'
check_pattern "$WORKSTREAM_DIR/foundation-workstream-completeness.md" 'runtime-ready' 'readiness promotion rule'

check_pattern "$WORKSTREAM_DIR/surfaces-index.md" 'surface-contracts/' 'surface contract references'
check_pattern "$WORKSTREAM_DIR/surfaces-index.md" 'system_message|markdown_response' 'base system/markdown surfaces'
check_pattern "$TRACE_DIR/functional-agent-to-capability-map.md" 'Capability|capability' 'functional-agent capability traceability'
check_pattern "$TRACE_DIR/surface-to-capability-map.md" 'Capability|capability' 'surface capability traceability'

if [[ -f "$WORKSTREAM_DIR/functional-agents.md" && -d "$WORKSTREAM_DIR/workstream-expertise" ]]; then
  mapfile -t agent_ids < <(grep -Eo '`[-a-z0-9]+-agent`' "$WORKSTREAM_DIR/functional-agents.md" | tr -d '`' | sort -u)
  for agent_id in "${agent_ids[@]}"; do
    [[ -f "$WORKSTREAM_DIR/workstream-expertise/$agent_id.md" ]] || fail "missing expertise bundle for $agent_id: $WORKSTREAM_DIR/workstream-expertise/$agent_id.md"
  done
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ -f "$WORKSTREAM_DIR/workstream-manifest.json" ]]; then
  if ! "$SCRIPT_DIR/validate-workstream-manifest.py" "$ROOT"; then
    failures=$((failures + 1))
  fi
fi

if [[ "$failures" -gt 0 ]]; then
  printf '[validate-workstream-contracts][error] %s failure(s)\n' "$failures" >&2
  exit 1
fi

printf '[validate-workstream-contracts] validation passed: %s\n' "$ROOT"
