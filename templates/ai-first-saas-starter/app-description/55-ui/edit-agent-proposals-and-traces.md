# Edit-Agent Proposals and Managed-Agent Traces UI

## Edit-Agent Proposal Review

- route: `/ui/governance/agent-edit-proposals`
- user goal: review behavior changes drafted by `AgentBehaviorEditorAgent` before activation
- required regions:
  - proposal list filtered by agent, prompt, skill, manifest, tool boundary, risk, reviewer, tenant/customer scope, and status
  - proposed diff for `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, or `ToolPermissionBoundary`
  - editing-agent rationale, risk/impact notes, authority-expansion flag, affected tests/replays, policy citations, and linked decision card
  - approval, reject, request-changes, escalate, and rollback actions
  - denial reason capture and audit result
- behavior:
  - editing agent may draft and explain changes but cannot activate behavior or authority changes
  - approval-required authority expansion blocks activation until human approval succeeds

## Prompt, Skill, and Work Trace Surfaces

- routes:
  - `/ui/audit/prompt-assembly-traces`
  - `/ui/audit/skill-load-traces`
  - `/ui/audit/agent-work-traces`
- user goal: explain what governed behavior guidance was assembled, which skills were loaded or denied, and which consequential work occurred
- required regions:
  - filters by tenant/customer, agent id, prompt version, skill version, manifest id, tool boundary id, decision card, actor, outcome, and time
  - `PromptAssemblyTrace` detail with active `AgentDefinition`, prompt version, compact manifest entries, policy context, checksum, redaction notes, and correlation id
  - `SkillLoadTrace` detail with requested `skillId`, allowed/denied outcome, manifest/boundary reason, caller AuthContext, and correlation id
  - `AgentWorkTrace` detail with recommendations, tool/data access, decisions, approvals, denials, outcome links, and redaction marker
  - links back to agent detail, governed documents, AdminAuditEvent, decision cards, and originating goal/plan/workflow when present
- constraints:
  - trace details are redacted by caller scope
  - unavailable or cross-tenant traces use safe forbidden/not-found states
