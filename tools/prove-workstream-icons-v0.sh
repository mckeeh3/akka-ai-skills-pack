#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"

print_help() {
  cat <<'EOF'
Prove the root AI-first SaaS core app exposes v0 workstream rail icons.

Usage:
  tools/prove-workstream-icons-v0.sh

The proof inspects the canonical root frontend and backend source without
requiring network access or npm install. It verifies:
  - User Admin, Agent Admin, Audit/Trace, and Governance/Policy each carry
    WorkstreamIconDescriptor metadata used by rendered rail icon affordances.
  - FunctionalAgentRailItem renders descriptor-backed icon data attributes,
    accessible labels, and a safe fallback for older payloads.
  - The backend /api/me functional-agent DTO includes WorkstreamIconDescriptor
    metadata for the same core v0 workstreams.
  - My Account remains opened from the lower-left signed-in user tile and is
    filtered out of the normal top rail.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
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

export PROOF_TARGET_DIR="$REPO_ROOT"
python3 <<'PY'
from __future__ import annotations

import os
import re
from pathlib import Path

root = Path(os.environ["PROOF_TARGET_DIR"])
frontend = root / "frontend" / "src"
fixtures_path = frontend / "__tests__" / "fixtures" / "workstream" / "agents.ts"
rail_path = frontend / "workstream" / "rail" / "FunctionalAgentRail.tsx"
rail_item_path = frontend / "workstream" / "rail" / "FunctionalAgentRailItem.tsx"
icon_component_path = frontend / "workstream" / "rail" / "WorkstreamIcon.tsx"
css_path = frontend / "styles" / "components.css"
backend_me_path = root / "src" / "main" / "java" / "ai" / "first" / "application" / "security" / "MeResponse.java"

for path in [fixtures_path, rail_path, rail_item_path, icon_component_path, css_path, backend_me_path]:
    if not path.exists():
        raise SystemExit(f"[proof][error] Missing rendered file: {path.relative_to(root)}")

fixtures = fixtures_path.read_text()
rail = rail_path.read_text()
rail_item = rail_item_path.read_text()
icon_component = icon_component_path.read_text()
css = css_path.read_text()
backend_me = backend_me_path.read_text()

required = [
    ("User Admin", "agent-user-admin", "workstream-user-admin", "users-admin", "accent-users", "Open User Admin workstream"),
    ("Agent Admin", "agent-agent-admin", "workstream-agent-admin", "bot-spark", "accent-agents", "Open Agent Admin workstream"),
    ("Audit/Trace", "agent-audit-trace", "workstream-audit-trace", "timeline-search", "accent-audit", "Open Audit/Trace workstream"),
    ("Governance/Policy", "agent-governance-policy", "workstream-governance-policy", "shield-checklist", "accent-governance", "Open Governance/Policy workstream"),
]

if "record WorkstreamIconDescriptor" not in backend_me or "WorkstreamIconDescriptor workstreamIcon" not in backend_me:
    raise SystemExit("[proof][error] Backend /api/me DTO does not expose WorkstreamIconDescriptor metadata")

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
    for description, pattern in {
        f"backend label {label}": rf'"{re.escape(label)}"',
        f"backend icon id {icon_id}": rf'"{re.escape(icon_id)}"',
        f"backend accent token {token}": rf'"{re.escape(token)}"',
        f"backend aria label {tooltip}": rf'"{re.escape(tooltip)}"',
    }.items():
        if not re.search(pattern, backend_me):
            raise SystemExit(f"[proof][error] Missing {description} in {backend_me_path.relative_to(root)}")

rail_item_checks = {
    "fallback descriptor": r"const workstreamIcon = entry\.workstreamIcon \?\? fallbackIcon",
    "descriptor-backed svg component": r"<WorkstreamIcon descriptor=\{workstreamIcon\}",
    "icon id data attribute": r"data-workstream-icon-id=\{workstreamIcon\.iconId\}",
    "accent token data attribute": r"data-accent-color-token=\{workstreamIcon\.accentColorToken\}",
    "accessible icon button label": r"aria-label=\{workstreamIcon\.ariaLabel\}",
}
for description, pattern in rail_item_checks.items():
    if not re.search(pattern, rail_item):
        raise SystemExit(f"[proof][error] Missing rail item proof for {description}")

icon_component_checks = {
    "svg renderer": r"<svg viewBox=\"0 0 24 24\"",
    "artwork data attribute": r"data-icon-artwork=\{artwork\}",
    "derivation function": r"deriveWorkstreamIconArtwork",
    "keyword derivation": r"keywordArtwork",
    "user admin artwork": r"'users-admin': 'users'",
    "agent admin artwork": r"'bot-spark': 'bot'",
    "audit artwork": r"'timeline-search': 'timeline'",
    "governance artwork": r"'shield-checklist': 'shield'",
}
for description, pattern in icon_component_checks.items():
    if not re.search(pattern, icon_component):
        raise SystemExit(f"[proof][error] Missing icon component proof for {description}")
if re.search(r"slice\(0, 1\)|toUpperCase\(\)", icon_component):
    raise SystemExit("[proof][error] Workstream icon component still contains letter-initial fallback logic")

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

print("[proof] PASS: root v0 left rail exposes descriptor-backed icons for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.")
print("[proof] PASS: My Account remains available only through the lower-left signed-in user tile, not the top rail.")
print(f"[proof] Target: {root}")
PY
