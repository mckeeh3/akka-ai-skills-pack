#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PACK_ROOT="$(cd -- "$SCRIPT_DIR/../../.." && pwd)"
APP_ROOT="$(cd -- "$PACK_ROOT/.." && pwd)"

fail() {
  printf '[verify-opinionated-ai-first-saas][error] %s\n' "$*" >&2
  exit 1
}

log() {
  printf '[verify-opinionated-ai-first-saas] %s\n' "$*"
}

require_file() {
  local path="$1"
  [[ -f "$PACK_ROOT/$path" ]] || fail "Required file missing: $path"
}

require_rg() {
  local pattern="$1"
  shift
  rg -q -- "$pattern" "$@" || fail "Missing required pattern '$pattern' in: $*"
}

forbid_rg() {
  local pattern="$1"
  shift
  if rg -n -- "$pattern" "$@"; then
    fail "Forbidden pattern '$pattern' found in: $*"
  fi
}

check_manifest_skill_consistency() {
  python3 - "$PACK_ROOT" <<'PY'
from pathlib import Path
import re
import sys

root = Path(sys.argv[1])
manifest_path = root / "pack/manifest.yaml"
lines = manifest_path.read_text().splitlines()

content_entries = []
in_content_skills = False
current = None
for line in lines:
    if line == "  skills:" and not content_entries:
        in_content_skills = True
        continue
    if in_content_skills and line == "  examples:":
        in_content_skills = False
        current = None
        continue
    if not in_content_skills:
        continue
    match = re.fullmatch(r"    - id: ([A-Za-z0-9_-]+)", line)
    if match:
        current = {"id": match.group(1), "path": None}
        content_entries.append(current)
        continue
    if current is not None:
        match = re.fullmatch(r"      path: skills/([A-Za-z0-9_-]+)", line)
        if match:
            current["path"] = match.group(1)

content_ids = [entry["id"] for entry in content_entries]
skill_dirs = sorted(
    path.parent.name for path in (root / "skills").glob("*/SKILL.md") if path.is_file()
)

bundle_all = []
in_bundles = False
in_all_bundle = False
in_all_skills = False
for line in lines:
    if line == "bundles:":
        in_bundles = True
        continue
    if in_bundles and line == "transforms:":
        break
    if not in_bundles:
        continue
    if line == "  - id: all":
        in_all_bundle = True
        in_all_skills = False
        continue
    if in_all_bundle and re.fullmatch(r"  - id: .+", line):
        break
    if in_all_bundle and line == "    skills:":
        in_all_skills = True
        continue
    if in_all_skills:
        match = re.fullmatch(r"      - ([A-Za-z0-9_-]+)", line)
        if match:
            bundle_all.append(match.group(1))
        elif line and not line.startswith("      "):
            in_all_skills = False

errors = []
if len(content_ids) != len(set(content_ids)):
    errors.append("content.skills contains duplicate ids")
if len(bundle_all) != len(set(bundle_all)):
    errors.append("bundle all contains duplicate skill ids")
for entry in content_entries:
    if entry["path"] != entry["id"]:
        errors.append(
            f"content.skills entry {entry['id']} has path skills/{entry['path']}, expected skills/{entry['id']}"
        )
for missing in sorted(set(skill_dirs) - set(content_ids)):
    errors.append(f"skill directory missing from content.skills: {missing}")
for extra in sorted(set(content_ids) - set(skill_dirs)):
    errors.append(f"content.skills references missing skill directory: {extra}")
for missing in sorted(set(content_ids) - set(bundle_all)):
    errors.append(f"content.skills id missing from bundle all: {missing}")
for extra in sorted(set(bundle_all) - set(content_ids)):
    errors.append(f"bundle all references missing content.skills id: {extra}")

if errors:
    print("[verify-opinionated-ai-first-saas][error] Manifest skill consistency check failed:", file=sys.stderr)
    for error in errors:
        print(f"- {error}", file=sys.stderr)
    sys.exit(1)
PY
}

cd "$PACK_ROOT"

log "checking manifest directory-level install contract"
require_rg "installedDirectories:" pack/manifest.yaml
require_rg "- references" pack/manifest.yaml
require_rg "- docs" pack/manifest.yaml
require_rg "- examples" pack/manifest.yaml
require_rg "- templates" pack/manifest.yaml
require_rg "- tools" pack/manifest.yaml
require_rg "- skills/\*" pack/manifest.yaml
check_manifest_skill_consistency

log "checking core foundation skill and manifest routing"
require_file "skills/core-saas-foundation/SKILL.md"
require_rg "id: core-saas-foundation" pack/manifest.yaml
require_rg "path: skills/core-saas-foundation" pack/manifest.yaml
require_rg "- core-saas-foundation" pack/manifest.yaml

log "checking mandatory invitation and AI-admin skills and manifest routing"
require_file "skills/akka-saas-invitation-onboarding/SKILL.md"
require_file "skills/ai-first-saas-admin-agents/SKILL.md"
require_rg "id: akka-saas-invitation-onboarding" pack/manifest.yaml
require_rg "path: skills/akka-saas-invitation-onboarding" pack/manifest.yaml
require_rg "- akka-saas-invitation-onboarding" pack/manifest.yaml
require_rg "id: ai-first-saas-admin-agents" pack/manifest.yaml
require_rg "path: skills/ai-first-saas-admin-agents" pack/manifest.yaml
require_rg "- ai-first-saas-admin-agents" pack/manifest.yaml

log "checking mandatory-security doctrine in top-level guidance"
require_rg "Security is mandatory" "$APP_ROOT/AGENTS.md" AGENTS.md pack/AGENTS.md docs/ai-first-saas-application-architecture.md
require_rg "mandatory secure SaaS foundation" "$APP_ROOT/AGENTS.md" AGENTS.md pack/AGENTS.md skills/README.md docs/ai-first-saas-application-architecture.md
require_rg "Applications generated by this skills pack are \*\*secure AI-first SaaS applications by default\*\*" docs/ai-first-saas-application-architecture.md
require_rg "core-saas-foundation" skills/README.md

log "checking broad intake and generation skills route through core foundation"
require_rg "core-saas-foundation" \
  skills/ai-first-saas/SKILL.md \
  skills/akka-solution-decomposition/SKILL.md \
  skills/akka-prd-to-specs-backlog/SKILL.md \
  skills/app-description-bootstrap/SKILL.md \
  skills/app-generate-app/SKILL.md

log "checking invitation onboarding doctrine guardrails"
require_rg "resend" docs/core-ai-first-saas-foundation.md docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "revoke|revoked" docs/core-ai-first-saas-foundation.md docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "expiry|expire|expired" docs/core-ai-first-saas-foundation.md docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "delivery status" docs/core-ai-first-saas-foundation.md docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "InvitationWorkflow" skills/akka-saas-invitation-onboarding/SKILL.md skills/akka-basic-user-admin/SKILL.md docs/core-saas-identity-tenancy-admin.md
require_rg "email delivery/outbox" skills/akka-saas-invitation-onboarding/SKILL.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md

log "checking admin management and AI-admin doctrine guardrails"
require_rg "UserDirectoryView" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "MembershipView" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "InvitationView" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "AdminAuditView" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "AccessReviewQueueView" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "user directory/search|list/search users|search/filter users" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/akka-basic-user-admin/SKILL.md
require_rg "membership lifecycle" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "admin audit/search" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "access review|Access Review" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "last-admin" docs/core-saas-identity-tenancy-admin.md docs/security-workos-auth-and-admin.md skills/core-saas-foundation/SKILL.md skills/akka-basic-user-admin/SKILL.md
require_rg "AccessReviewAgent" skills/ai-first-saas-admin-agents/SKILL.md docs/core-saas-identity-tenancy-admin.md skills/core-saas-foundation/SKILL.md
require_rg "AdminRiskAgent" skills/ai-first-saas-admin-agents/SKILL.md docs/core-saas-identity-tenancy-admin.md skills/core-saas-foundation/SKILL.md
require_rg "RoleRecommendationAgent" skills/ai-first-saas-admin-agents/SKILL.md docs/core-saas-identity-tenancy-admin.md skills/core-saas-foundation/SKILL.md
require_rg "AdminAuditSummaryAgent" skills/ai-first-saas-admin-agents/SKILL.md docs/core-saas-identity-tenancy-admin.md skills/core-saas-foundation/SKILL.md

log "checking planning guardrails for full foundation task breakdown"
require_file "tools/validate-pending-task-workstream-contract.sh"
require_rg "Vertical workstream contract" docs/pending-task-queue.md skills/akka-backlog-item-to-task-brief/SKILL.md
require_rg "validate-pending-task-workstream-contract" docs/pending-task-queue.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md skills/akka-do-next-pending-task/SKILL.md
require_rg "vertical contract block/line" tools/validate-pending-task-workstream-contract.sh
require_rg "invitation lifecycle" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "email delivery" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "UserDirectoryView" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "MembershipView" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "InvitationView" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AdminAuditView" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AccessReviewQueueView" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AI admin|admin AI" skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AdminRiskAgent" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AccessReviewAgent" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "decision cards for risky admin" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md

log "checking governed runtime agent foundation doctrine guardrails"
require_rg "AgentDefinition" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-behavior-profiles/SKILL.md
require_rg "PromptDocument" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-prompt-governance/SKILL.md
require_rg "SkillDocument" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "AgentSkillManifest" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "ToolPermissionBoundary" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-behavior-profiles/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "readSkill\(skillId\)" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "PromptAssemblyTrace" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-prompt-governance/SKILL.md
require_rg "SkillLoadTrace" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "AgentWorkTrace" "$APP_ROOT/README.md" skills/README.md docs/core-ai-first-saas-foundation.md skills/core-saas-foundation/SKILL.md
require_rg "AgentBehaviorEditorAgent|behavior editing agent|editing-agent" docs/ai-first-saas-application-architecture.md skills/akka-agent-governed-documents/SKILL.md skills/akka-agent-prompt-governance/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg 'prompt maintenance through an `AgentBehaviorEditorAgent`|skill, manifest, and tool-boundary changes through an `AgentBehaviorEditorAgent`|editing-agent proposal' skills/akka-agent-prompt-governance/SKILL.md skills/akka-agent-skill-governance/SKILL.md
require_rg "Prompt and skill content is behavior guidance only|cannot grant tool permissions|does not grant tool permissions" skills/core-saas-foundation/SKILL.md skills/akka-agent-skill-governance/SKILL.md docs/ai-first-saas-application-architecture.md

log "checking governed runtime agent foundation planning and task-generation guardrails"
require_rg "AgentDefinition" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "PromptDocument" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "SkillDocument" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "AgentSkillManifest" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "readSkill" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "PromptAssemblyTrace" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "SkillLoadTrace" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "behavior editing|AgentBehaviorEditorAgent" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "too broad|split" skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md docs/pending-task-queue.md
require_rg "agent catalog" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md
require_rg "agent detail" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md docs/module-sprint-planning.md docs/pending-task-queue.md

log "checking governed runtime agent foundation core app-description assets"
require_rg "AgentDefinition" "$APP_ROOT/app-description"
require_rg "PromptDocument" "$APP_ROOT/app-description"
require_rg "SkillDocument" "$APP_ROOT/app-description"
require_rg "AgentSkillManifest" "$APP_ROOT/app-description"
require_rg "ToolPermissionBoundary|tool permission" "$APP_ROOT/app-description"
require_rg "readSkill" "$APP_ROOT/app-description"
require_rg "PromptAssemblyTrace" "$APP_ROOT/app-description"
require_rg "SkillLoadTrace" "$APP_ROOT/app-description"
require_rg "AgentWorkTrace" "$APP_ROOT/app-description"
require_rg "AgentBehaviorEditorAgent|editing-agent" "$APP_ROOT/app-description"
require_rg "agent catalog" "$APP_ROOT/app-description"
require_rg "agent detail" "$APP_ROOT/app-description"
require_rg "unauthorized.*PromptDocument|unassigned skill denial|disabled-agent denial|authority expansion" "$APP_ROOT/app-description"

log "checking source-controlled workstream/surface templates and validators"
require_file "docs/workstream-contract.md"
require_file "docs/workstream-attention-contracts.md"
require_file "docs/examples/domain-workstream-contract-example.md"
require_file "templates/ai-first-saas-core-app/app-description/README.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/functional-agents.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstreams-and-retention.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/attention-and-dashboards.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/internal-agents.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/README.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/foundation-workstream-completeness.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/my-account-agent.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/user-admin-agent.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/agent-admin-agent.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/audit-trace-agent.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-expertise/governance-policy-agent.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surfaces-index.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/01-access-profile-dashboard.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/05-decision-card.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/06-audit-trace-explorer.md"
require_file "templates/ai-first-saas-core-app/app-description/12-workstreams/surface-contracts/07-agent-governance-center.md"
require_file "templates/ai-first-saas-core-app/app-description/55-ui/structured-surface-rendering.md"
require_file "templates/ai-first-saas-core-app/app-description/70-traceability/functional-agent-to-capability-map.md"
require_file "templates/ai-first-saas-core-app/app-description/70-traceability/surface-to-capability-map.md"
require_file "tools/validate-workstream-contracts.sh"
require_file "tools/validate-workstream-manifest.py"
require_file "tools/validate-surface-contracts.sh"
require_file "docs/examples/domain-workstream-surface-contract-example.md"
require_rg "validate-workstream-contracts" docs/agent-workstream-design-review-checklist.md tools/validate-workstream-contracts.sh
require_rg "validate-surface-contracts" docs/structured-surface-contracts.md skills/app-description-surface-modeling/SKILL.md
"$PACK_ROOT/tools/validate-workstream-contracts.sh" "$PACK_ROOT/templates/ai-first-saas-core-app/app-description"
"$PACK_ROOT/tools/validate-surface-contracts.sh" "$PACK_ROOT/templates/ai-first-saas-core-app/app-description"

log "checking pack docs, manifest, skills, and core app-description assets"
require_file "docs/core-ai-first-saas-foundation.md"
require_file "docs/core-saas-identity-tenancy-admin.md"
require_file "docs/core-saas-owner-tenant-billing.md"
require_file "docs/workstream-ui-reference-architecture.md"
require_file "docs/workstream-manifest-schema.md"
require_file "docs/workstream-manifest.schema.json"
require_file "docs/minimum-implementable-workstream-slice.md"
require_file "docs/agent-workstream-design-review-checklist.md"
require_file "docs/examples/README.md"
require_rg "docs/frontend-with-akka-backend.md" skills/akka-web-ui-apps/SKILL.md skills/akka-web-ui-frontend-project/SKILL.md skills/akka-workos-user-auth/SKILL.md
require_rg "frontend/src/workstream" docs/workstream-ui-reference-architecture.md "$APP_ROOT/frontend/README.md" skills/akka-web-ui-apps/SKILL.md
require_rg "install-skills" install-skills.sh "$APP_ROOT/install-skills.sh" README.md

log "checking repository example references"
require_file "tools/validate-repository-example-references.py"
require_file "tools/validate-curated-example-index.py"
require_file "tools/validate-installed-skill-references.py"
python3 tools/validate-repository-example-references.py
python3 tools/validate-curated-example-index.py
installed_reference_check_dir="$(mktemp -d)"
trap 'rm -rf "$installed_reference_check_dir"' EXIT
./install-skills.sh --target "$installed_reference_check_dir/.agents/skills" --prune
python3 tools/validate-installed-skill-references.py "$installed_reference_check_dir/.agents/skills"
example_java_count="$(find examples/akka-components/src -name '*.java' | wc -l | tr -d ' ')"
if [[ "$example_java_count" -gt 120 ]]; then
  fail "Curated akka-components examples are too large ($example_java_count Java files); do not reinstall a duplicate app baseline"
fi
for maintainer_tool in release.sh check-version-consistency.sh; do
  if [[ -e "tools/$maintainer_tool" ]]; then
    fail "Maintainer-only tool must not be installed from tools/$maintainer_tool"
  fi
done

log "checking removed quarantined content is not reintroduced"
forbid_rg "core-ai-first-saas-input|ai-first-app-description-gaps|agent-executable-examples-plan|agent-skill-expansion-plan|StaticFrontendEndpoint|WebUiHomeEndpoint|WebUiDataEndpoint|WebUiSsePageEndpoint|WebUiWebSocketPageEndpoint|FrontendReference|frontend-reference|web-ui-sse|web-ui-websocket|quarantined" \
  docs \
  skills \
  examples \
  pack/manifest.yaml

log "checking retired distribution output is not used as guidance/template source"
retired_output_pattern='skills-pack/'"dist"'|'"dist"'/akka-ai'
forbid_rg "$retired_output_pattern" \
  AGENTS.md \
  pack/AGENTS.md \
  skills/README.md \
  docs/agent-workstream-application-architecture.md \
  docs/structured-surface-contracts.md \
  skills/app-description-surface-modeling/SKILL.md \
  templates/ai-first-saas-core-app/app-description

log "checking forbidden optional-security phrasing in top-level routing files"
forbid_rg "security.*optional|optional.*security|when security is in scope|only when security is in scope|JWT/internal security skills only when security is in scope|generic Akka app|ordinary Akka app|CRUD-first" \
  "$APP_ROOT/AGENTS.md" \
  AGENTS.md \
  pack/AGENTS.md \
  skills/README.md \
  docs/ai-first-saas-application-architecture.md

log "checking generic domain terminology guardrails"
require_rg "DCA-specific.*unless DCA is explicitly the user.s domain|historical/example domain names as generic placeholders" \
  "$APP_ROOT/AGENTS.md" \
  AGENTS.md \
  pack/AGENTS.md \
  skills/README.md

log "checking forbidden optional invitation-email phrasing in foundation/admin docs"
forbid_rg "optional invite email|optionally sends invite|optionally send invite|invite email delivery is optional|invite email.*may be omitted|email-invite.*optional|optional.*email-invite" \
  docs/core-ai-first-saas-foundation.md \
  docs/core-saas-identity-tenancy-admin.md \
  docs/security-workos-auth-and-admin.md \
  skills/core-saas-foundation/SKILL.md \
  skills/akka-basic-user-admin/SKILL.md \
  skills/akka-workos-user-auth/SKILL.md \
  skills/akka-saas-invitation-onboarding/SKILL.md

log "checking shell syntax for pack scripts"
for script in install-skills.sh "$APP_ROOT/install-skills.sh" tools/validate-pending-task-workstream-contract.sh tools/validate-workstream-contracts.sh tools/validate-surface-contracts.sh pack/maintainer/tools/release.sh pack/maintainer/tools/check-version-consistency.sh; do
  bash -n "$script"
done
python3 -m py_compile tools/validate-workstream-manifest.py tools/validate-installed-skill-references.py

log "verification passed"
