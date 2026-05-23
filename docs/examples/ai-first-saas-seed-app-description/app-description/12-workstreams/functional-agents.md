# Functional Agents

This seed app is modeled as an agent workstream application. These user-facing functional agents are the primary authenticated application verticals; routes and pages are only deep-link or implementation details. Per-agent workstream expertise belongs under `12-workstreams/workstream-expertise/`; this file may summarize expertise, but the bundle file is authoritative once materialized.

| Functional agent | Purpose | Authorized roles/capabilities | Default surface | Callable capabilities | Trace/test obligations |
|---|---|---|---|---|---|
| `my-account-agent` | Current account, selected context, profile, settings, sign out, and safe self-service. The signed-in user tile at the bottom of the left rail opens this workstream. Authoritative expertise: `workstream-expertise/my-account-agent.md`. | Any active signed-in member for own account and permitted contexts. | `my-account-dashboard` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` | `/api/me`, context selection, disabled-user denial, profile/settings audit where consequential, request/response surface rendering, skill/reference load denial, no authority expansion from profile/settings text. |
| `user-admin-agent` | Invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit. Authoritative expertise: `workstream-expertise/user-admin-agent.md`. | Tenant Admin, Customer Admin within scope, SaaS Owner support roles with explicit support grant. | `user-admin-dashboard` with `user-admin-user-list` and `user-admin-user-account` as the canonical list/detail surfaces | `secure-tenant-user-foundation`, `managed-agent-foundation`, `governance-decisions-audit` | invite lifecycle, tenant isolation, last-admin protection, role denial, skill/reference load denial, AdminAuditEvent and AgentWorkTrace coverage. |
| `agent-admin-agent` | Govern AgentDefinition records, prompts, skills, references, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces. Authoritative expertise: `workstream-expertise/agent-admin-agent.md`. | Agent Steward, Tenant Admin, Policy Owner, Reviewer, Auditor read-only where permitted. | `agent-governance-center` | `managed-agent-foundation`, `governance-decisions-audit` | active/draft lifecycle, `readSkill(skillId)` and `readReferenceDoc(referenceId)` authorization, prompt/skill/reference/tool-boundary trace coverage, authority-expansion denial tests. |
| `mission-control-agent` | Supervise active goals, plans, delegated work, exceptions, approvals, and outcome signals. Authoritative expertise: `workstream-expertise/mission-control-agent.md`. | Supervisor, Reviewer/Approver, Outcome Owner, Auditor read-only where permitted. | `mission-control-briefing` | `ai-first-work-management`, `governance-decisions-audit` | workstream timeline rendering, approval queue, policy-triggered exception, outcome link tests, retained-human-authority denial, skill/reference load denial. |
| `governance-policy-agent` | Manage policies, approval gates, proposals, simulations, replay evidence, and activation/rollback. Authoritative expertise: `workstream-expertise/governance-policy-agent.md`. | Policy Owner, Reviewer/Approver, Tenant Admin, Auditor read-only where permitted. | `agent-governance-center` with `decision-card` review | `governance-decisions-audit`, `managed-agent-foundation` | proposal approval, unauthorized authority expansion denial, simulation/replay trace tests, policy-text-cannot-grant-authority tests, skill/reference load denial. |
| `audit-trace-agent` | Search and explain identity, authorization, data access, tool use, decisions, workflows, and outcomes. Authoritative expertise: `workstream-expertise/audit-trace-agent.md`. | Auditor, Tenant Admin, scoped Supervisor, SaaS Owner support roles with explicit support grant. | `audit-trace-explorer` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` | redaction, tenant/customer filtering, trace correlation, support-access audit, skill/reference/tool denial traces, export-denial tests. |

## User Admin functional-agent contract

`user-admin-agent` is a governed skilled functional agent for the canonical three-surface User Admin vertical. Its authoritative expert bundle is `workstream-expertise/user-admin-agent.md`; this section remains a catalog summary. The bundle file owns User Admin prompt intent, skills, references, compact manifests, tool boundary, traces, governance owner, seed policy, denials, and tests. It must operate within the selected `AuthContext`; surface visibility, tool calls, expertise text, and generated recommendations never expand backend authority.

### Supported intents

| Intent | Required behavior | Primary surfaces | Output shape |
|---|---|---|---|
| open dashboard | Load `user-admin-dashboard`, summarize pending invitations, access-review risk, support-access status, and recent admin audit evidence for the selected scope. | `user-admin-dashboard` | `UserAdminDashboardPayload` plus `AgentWorkTrace` summary. |
| search/list users | Interpret filters, call scoped list/search capabilities, and explain pagination, redaction, empty state, or forbidden state without requiring known user ids. | `user-admin-user-list` | `UserAdminUserListPayload` and safe query explanation. |
| open user account | Resolve a selected row to `user-admin-user-account`, highlight account status, memberships, invitations, audit excerpts, access-review items, and allowed/denied actions. | `user-admin-user-account` | `UserAdminUserAccountPayload` and action explanation. |
| explain allowed actions | For each visible dashboard, row, or detail action, cite the backend capability id, scope, actor role, and required idempotency/audit behavior. | all three User Admin surfaces | Browser-safe explanation tied to action metadata. |
| explain denied actions | For forbidden, hidden, disabled, role-escalation, support-access, cross-tenant, Customer Admin Tenant-level, SaaS Owner no-support-access, and last-admin denials, explain the denial without revealing out-of-scope data. | all three User Admin surfaces plus decision cards | Denial explanation with policy/code, redaction marker, and trace link. |
| draft invitation rationale | Create an `InvitationDraft` with onboarding copy, requested roles, least-privilege rationale, delivery caveats, and no raw token exposure. | `user-admin-dashboard`, `user-admin-user-list`, reusable decision card | Draft/proposal only unless a human-confirmed capability call sends the invite. |
| summarize audit evidence | Create an `AdminAuditSummary` from scoped AdminAuditView excerpts, correlated actions, and decision-card links with redaction preserved. | `user-admin-dashboard`, `user-admin-user-account`, audit trace surface | Read-only summary with audit event ids and trace ids. |
| recommend least-privilege roles | Create a `RoleRecommendation` using current memberships, access-review evidence, requested job/context, risk/confidence, alternatives, and escalation warnings. | `user-admin-user-account`, decision card | Recommendation/proposal; never direct authority expansion. |
| route risky actions to decision cards | Convert last-admin risk, role escalation, broad support-access expansion, identity relink/reset, bulk operations, and low-confidence recommendations into decision-card-ready facts. | `user-admin-user-account`, reusable decision card | Decision-card payload with evidence, risk, alternatives, and required approver scope. |

### Governed runtime documents

The first-install or tenant-bootstrap seed bundle must create active governed records for this functional agent before it can claim functional readiness:

- `AgentDefinition`: `user-admin-agent`, owning the User Admin workstream, model policy, active prompt ref, active skill manifest ref, active reference manifest ref, active tool boundary ref, steward/reviewer roles, and lifecycle status.
- `PromptDocument`/`PromptVersion`: system instructions for scoped User Admin work, safe denial, tenant/customer redaction, decision-card routing, and no secret/token exposure.
- `SkillDocument`/`SkillVersion`: focused skills for `access-review`, `admin-risk-scoring`, `invitation-drafting`, `role-recommendation`, `support-access-review`, and `audit-summary`.
- `ReferenceDocument`/`ReferenceVersion`: focused references for role catalog, invitation/onboarding policy, access-review policy, support-access procedure, last-admin protection, and audit redaction.
- `AgentSkillManifest` and `AgentReferenceManifest`: compact skill/reference ids, names, descriptions, and when-to-use/consult hints included in the assembled prompt; full text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)`.
- `ToolPermissionBoundary`: explicit allow/deny rules for `read_skill`, `read_reference`, scoped reads, summaries, draft/proposal creation, human-confirmed mutation calls, decision-card creation, Resend/email preview or send tools, and forbidden cross-scope data access.
- `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace`: required trace records for prompt assembly, skill/reference loading, tool allow/deny decisions, surface loads, action outcomes, recommendations, denials, and decision-card routing.

### Tool boundary defaults

- Read/list/detail/summarize tools are allowed only for scoped `admin.users.dashboard.read`, `admin.users.search`, `admin.users.detail.read`, `admin.audit.read`, and `admin.access_review.read` capability checks inside the selected `AuthContext`.
- Draft/proposal tools may create `InvitationDraft`, `RoleRecommendation`, `AdminAuditSummary`, access-review recommendations, and decision-card facts when authorized.
- Consequential mutations, including invite send/resend/revoke, membership add/suspend/reactivate/remove, role replace/remove, account disable/reactivate, support-access grant/revoke/extend, access-review resolve, identity relink/reset, and policy changes, default to human-confirmed capability calls or decision-card approval flows.
- The agent must deny raw invitation token exposure, provider secrets, unredacted out-of-scope Tenant/Customer data, Customer Admin Tenant-level actions, SaaS Owner Tenant-data access without support access, disabled actor actions, role escalation, and last-admin loss.

### User Admin agent trace and tests

Tests for the functional vertical must cover:

- deterministic prompt assembly from `AgentDefinition`, `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, and `ToolPermissionBoundary`, with `PromptAssemblyTrace` emitted;
- authorized and denied `readSkill(skillId)` calls with `SkillLoadTrace` records;
- authorized and denied `readReferenceDoc(referenceId)` calls with `ReferenceLoadTrace` records;
- tool allow/deny behavior for dashboard read, user search/list, open user account detail, audit summary, invitation draft, role recommendation, and risky decision-card routing;
- SaaS Owner Admin, Tenant Admin, and Customer Admin variants for allowed, denied, redacted, forbidden, empty, stale, and error surface states;
- `AgentWorkTrace` records linking intent, selected `AuthContext`, capability ids, correlation ids, surface ids, audit event ids, recommendation ids, denial codes, and decision-card ids;
- no autonomous side effects for role escalation, last-admin loss, support-access expansion, bulk disable, identity relink/reset, or policy/permission changes.

## Functional-agent rules

- The left rail lists only functional agents authorized by the selected `AuthContext` and backend capability grants.
- Selecting a functional agent opens its durable workstream and default dashboard/attention surface.
- Composer requests are interpreted in the selected functional-agent context and must resolve to capability-backed actions, evidence queries, proposals, or safe denials.
- Prompt intent and skill hints guide the agent; they do not grant authority. Backend capabilities and tool boundaries remain authoritative.
- Full core SaaS scope requires `user-admin-agent` and `agent-admin-agent`; removing either changes the realization scope to a narrower deferred foundation.
