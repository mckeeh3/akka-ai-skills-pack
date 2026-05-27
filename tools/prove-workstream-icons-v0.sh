#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
TARGET_DIR=""
KEEP=false

print_help() {
  cat <<'EOF'
Prove the scaffolded AI-first SaaS starter exposes v0 workstream rail icons.

Usage:
  tools/prove-workstream-icons-v0.sh [--target <dir>] [--keep]

The proof scaffolds the starter into a temporary target unless --target is
provided, then inspects the rendered frontend source without requiring network
access or npm install. It verifies:
  - User Admin, Agent Admin, Audit/Trace, and Governance/Policy each carry
    WorkstreamIconDescriptor metadata used by rendered rail icon affordances.
  - FunctionalAgentRailItem renders descriptor-backed icon data attributes,
    tooltip markup, and accessible labels.
  - My Account remains opened from the lower-left signed-in user tile and is
    filtered out of the normal top rail.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --target)
      [[ $# -ge 2 ]] || { echo "[proof][error] Missing value for --target" >&2; exit 1; }
      TARGET_DIR="$2"
      shift 2
      ;;
    --keep)
      KEEP=true
      shift
      ;;
    --help|-h)
      print_help
      exit 0
      ;;
    *)
      echo "[proof][error] Unknown option: $1" >&2
      exit 1
      ;;
  esac
done

if [[ -z "$TARGET_DIR" ]]; then
  TARGET_DIR="$(mktemp -d "${TMPDIR:-/tmp}/workstream-icons-v0-proof.XXXXXX")"
  if [[ "$KEEP" != true ]]; then
    trap 'rm -rf "$TARGET_DIR"' EXIT
  fi
else
  mkdir -p "$TARGET_DIR"
  TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"
fi

"$REPO_ROOT/tools/scaffold-ai-first-saas-starter.sh" \
  --target "$TARGET_DIR" \
  --template-dir "$REPO_ROOT/templates/ai-first-saas-starter" \
  --base-package ai.first \
  --app-name "Workstream Icon Proof" \
  --app-slug "workstream-icon-proof"

export PROOF_TARGET_DIR="$TARGET_DIR"
python3 <<'PY'
from __future__ import annotations

import os
import re
from pathlib import Path

root = Path(os.environ["PROOF_TARGET_DIR"])
frontend = root / "frontend" / "src"
fixtures_path = frontend / "workstream" / "fixtures" / "agents.ts"
rail_path = frontend / "workstream" / "rail" / "FunctionalAgentRail.tsx"
rail_item_path = frontend / "workstream" / "rail" / "FunctionalAgentRailItem.tsx"
css_path = frontend / "styles" / "components.css"

for path in [fixtures_path, rail_path, rail_item_path, css_path]:
    if not path.exists():
        raise SystemExit(f"[proof][error] Missing rendered file: {path.relative_to(root)}")

fixtures = fixtures_path.read_text()
rail = rail_path.read_text()
rail_item = rail_item_path.read_text()
css = css_path.read_text()

required = [
    ("User Admin", "agent-user-admin", "workstream-user-admin", "users-admin", "accent-users", "Open User Admin workstream"),
    ("Agent Admin", "agent-agent-admin", "workstream-agent-admin", "bot-spark", "accent-agents", "Open Agent Admin workstream"),
    ("Audit/Trace", "agent-audit-trace", "workstream-audit-trace", "timeline-search", "accent-audit", "Open Audit/Trace workstream"),
    ("Governance/Policy", "agent-governance-policy", "workstream-governance-policy", "shield-checklist", "accent-governance", "Open Governance/Policy workstream"),
]

for label, agent_id, workstream_id, icon_id, token, tooltip in required:
    checks = {
        f"label {label}": rf"label: '{re.escape(label)}'",
        f"agent id {agent_id}": rf"functionalAgentId: '{re.escape(agent_id)}'",
        f"workstream id {workstream_id}": rf"workstreamId: '{re.escape(workstream_id)}'",
        f"icon id {icon_id}": rf"iconId: '{re.escape(icon_id)}'",
        f"accent token {token}": rf"accentColorToken: '{re.escape(token)}'",
        f"tooltip {tooltip}": rf"tooltip: '{re.escape(tooltip)}'",
        f"aria label {tooltip}": rf"ariaLabel: '{re.escape(tooltip)}'",
    }
    for description, pattern in checks.items():
        if not re.search(pattern, fixtures):
            raise SystemExit(f"[proof][error] Missing {description} in {fixtures_path.relative_to(root)}")

rail_item_checks = {
    "descriptor-backed glyph": r"iconGlyph\(entry\.workstreamIcon, entry\.icon, entry\.label\)",
    "icon id data attribute": r"data-workstream-icon-id=\{entry\.workstreamIcon\.iconId\}",
    "accent token data attribute": r"data-accent-color-token=\{entry\.workstreamIcon\.accentColorToken\}",
    "accessible icon button label": r"aria-label=\{entry\.workstreamIcon\.ariaLabel\}",
    "tooltip description wiring": r"entry\.workstreamIcon\.tooltip \? tooltipId",
    "tooltip role": r"className=\"workstream-icon-tooltip\" role=\"tooltip\"",
}
for description, pattern in rail_item_checks.items():
    if not re.search(pattern, rail_item):
        raise SystemExit(f"[proof][error] Missing rail item proof for {description}")

for token in ["accent-users", "accent-agents", "accent-audit", "accent-governance"]:
    if f'.workstream-icon[data-accent-color-token="{token}"]' not in css:
        raise SystemExit(f"[proof][error] Missing CSS accent rule for {token}")

my_account_checks = {
    "My Account fixture": r"label: 'My Account'",
    "My Account launcher id": r"const myAccountFunctionalAgentId = 'agent-my-account'",
    "My Account filtered out of top rail": r"entry\.functionalAgentId !== myAccountFunctionalAgentId",
    "My Account lower-left button": r"className=\{`rail-user-button",
    "My Account lower-left accessible label": r"aria-label=\{`Open My Account workstream for \$\{userDisplayName\}`\}",
    "My Account click handler": r"onClick=\{openMyAccount\}",
}
for description, pattern in my_account_checks.items():
    haystack = fixtures if description == "My Account fixture" else rail
    if not re.search(pattern, haystack):
        raise SystemExit(f"[proof][error] Missing {description}")

if re.search(r"aria-haspopup=\"menu\"|rail-user-menu|role=\"menuitem\"", rail):
    raise SystemExit("[proof][error] My Account user tile regressed to menu-style navigation")

print("[proof] PASS: scaffolded v0 left rail exposes descriptor-backed icons for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.")
print("[proof] PASS: My Account remains available only through the lower-left signed-in user tile, not the top rail.")
print(f"[proof] Target: {root}")
PY

if [[ "$KEEP" == true ]]; then
  echo "[proof] Kept scaffold target: $TARGET_DIR"
fi
