# Skill Manifests and Tool Permissions UI

## Skill Manifest Management

- route: `/ui/governance/skill-manifests`
- user goal: control which skills an agent can discover and request through `readSkill(skillId)`
- required regions:
  - `AgentSkillManifest` list/detail with tenant/customer scope, assigned `AgentDefinition`, compact skill metadata, active `SkillDocument`/`SkillVersion` references, status, checksum, and approval history
  - add/remove skill proposal form with rationale, risk classification, affected agents, tests/replays, and expected authority impact
  - unassigned skill denial visibility from `SkillLoadTrace`
  - approval-required authority expansion warnings and decision-card links
- behavior:
  - compact manifest entries may be included during prompt assembly
  - full skill text is not exposed until authorized `readSkill(skillId)` succeeds
  - manifest changes that broaden capability, tool, data, or tenant scope require review/approval before activation

## Tool Permission Boundary Management

- route: `/ui/governance/tool-permissions`
- user goal: govern the tools, data resources, and side effects each agent may use
- required regions:
  - `ToolPermissionBoundary` list/detail with tool/data grants, tenant/customer scope, allowed operations, approval gates, policy citations, status, and linked agents/manifests
  - proposed boundary diff with rationale, risk/impact, affected capabilities, and simulation/replay results
  - denial history for tool/data access attempts and cross-scope attempts
  - approval/activation/rollback controls and AdminAuditEvent links
- behavior:
  - prompt and skill content never expand tool/data permissions by themselves
  - new tools, broader data access, cross-scope authority, external side effects, or autonomous high-impact actions require decision-card approval before activation
