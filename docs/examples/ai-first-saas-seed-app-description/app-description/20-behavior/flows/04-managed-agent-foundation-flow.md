# Flow: Managed Agent Foundation

## Agent definition lifecycle

1. Authorized Agent Steward or Tenant Admin creates an `AgentDefinition` in draft state with owner/steward, model reference, prompt reference, `AgentSkillManifest` reference, `ToolPermissionBoundary` reference, tenant/customer scope, and authority level.
2. System validates scope, steward authority, referenced prompt/skill/manifest/tool-boundary status, disabled-agent state, expected version, idempotency key, and correlation id.
3. Activation requires approved prompt and skill versions, approved manifest assignment, approved tool boundary, no unresolved authority-expansion decision, and an AdminAuditEvent.
4. Disabled or archived agents cannot be invoked, cannot call tools, and cannot load skills; denial creates audit/trace evidence without leaking cross-tenant artifact existence.
5. Re-enabling a disabled agent requires steward/admin approval and records the effective prompt, skill manifest, and tool-boundary versions.

## Prompt and skill governance lifecycle

1. Human steward may request behavior changes in natural language; the default seed path routes the request to `AgentBehaviorEditorAgent` instead of requiring direct text editing.
2. `AgentBehaviorEditorAgent` identifies affected `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, and `ToolPermissionBoundary` records.
3. Editing agent drafts a proposed diff with rationale, risk/impact notes, affected tests/replays, and whether the change attempts authority expansion.
4. System creates draft `PromptVersion` or `SkillVersion` records and links them to a proposal/decision card for review.
5. Reviewer approves, rejects, requests changes, or escalates; approval is mandatory before activation when the proposal changes active behavior.
6. Activation records checksum, active version, approver, policy citation, and AdminAuditEvent; rollback restores a prior approved version and records the reason.
7. Direct text editing, when explicitly enabled for restricted admins, may create drafts only; activation still follows review/approval and audit policy.

## Runtime prompt assembly and skill loading

1. Runtime invocation resolves active `AgentDefinition` in the selected AuthContext and rejects disabled, archived, missing, cross-tenant, or stale definitions.
2. Runtime assembles the active governed prompt from approved `PromptDocument`/`PromptVersion`, adds policy context and compact `AgentSkillManifest` entries, and creates `PromptAssemblyTrace`.
3. Prompt and skill content are behavior guidance only; they cannot grant role, tenant, data, tool, or approval authority.
4. When an agent requests full skill content, runtime authorizes `readSkill(skillId)` against `AgentSkillManifest`, `ToolPermissionBoundary`, caller AuthContext, tenant/customer scope, and policy.
5. Allowed and denied skill loads create `SkillLoadTrace` records with manifest/boundary reason and correlation id.
6. Consequential recommendations, tool/data access, decisions, approvals, denials, and outcomes create `AgentWorkTrace` records linked to prompt assembly and skill-load traces.

## Managed-agent admin offload

1. A single governed `UserAdminAgent` may fulfill access-review, admin-risk-scoring, invitation-drafting, role-recommendation, support-access-review, and audit-summary responsibilities through approved skills in its `AgentSkillManifest`.
2. Separate specialized agents remain allowed when isolation, scale, or review needs justify them.
3. High-risk admin changes still route to decision cards and approval gates regardless of whether a single skilled agent or multiple specialized agents propose them.
