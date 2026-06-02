# Prompt and Skill Governance UI

## Prompt Governance

- route: `/ui/governance/prompts`
- user goal: review, propose, approve, activate, rollback, and audit governed `PromptDocument` and `PromptVersion` records
- required regions:
  - prompt document list with tenant/customer scope, status, active version, draft/proposed version, checksum, owner/steward, and linked agents
  - proposed diff/history with rationale, risk/impact notes, affected tests/replays, and approval status
  - prompt assembly preview showing active prompt, policy context, compact `AgentSkillManifest`, and redaction notes
  - activation/rollback controls gated by prompt approval permissions
  - links to `PromptAssemblyTrace`, AdminAuditEvent, decision cards, and editing-agent proposals
- constraints:
  - prompt text cannot grant tool, data, role, tenant, approval, or authorization authority
  - direct text editing, if enabled, creates drafts only and does not bypass review/approval

## Skill Governance

- route: `/ui/governance/skills`
- user goal: manage governed `SkillDocument` and `SkillVersion` records as runtime-loadable behavior guidance
- required regions:
  - skill document list with tenant/customer scope, status, active version, draft/proposed version, checksum, compact manifest hint, and linked manifests/agents
  - full skill text review for authorized stewards/reviewers
  - proposed diff/history, risk/impact, tests/replays, approval status, activation/deprecation controls, and rollback history
  - `readSkill(skillId)` test console that shows allowed/denied outcomes for a selected agent, manifest, tool boundary, and AuthContext
  - links to `SkillLoadTrace`, `AgentWorkTrace`, AdminAuditEvent, decision cards, and editing-agent proposals
- constraints:
  - skill text is behavior guidance only and cannot grant authority beyond the assigned `AgentSkillManifest`, `ToolPermissionBoundary`, policy, and caller AuthContext
  - unassigned skill reads are denied and traceable
