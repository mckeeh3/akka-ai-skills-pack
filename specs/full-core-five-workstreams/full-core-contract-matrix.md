# Full-Core Five Workstreams Contract Matrix

## Status

Implementation contract for `TASK-FC5-01-001`. This document is a planning artifact for the skills-pack source repository. It does not claim new runtime behavior. It defines the vertical contracts that later tasks must implement through real backend/Akka/API/UI paths.

## Decomposition rule

Every full-core increment must preserve this chain:

```text
requirements
→ workstreams
→ attention
→ dashboards
→ surface graph
→ internal workstream agent graph
→ governed-tools
→ capabilities
→ Akka substrate
→ UI/API
→ traces/tests/local validation
```

The five-core v0 bootstrap remains five initial `markdown_response` surfaces. Rich surfaces and actions below are reached only by explicit shell requests, surface actions, or full-core APIs. Normal runtime must not use fixture-only, deterministic, mock, simulated, model-less, or service-only provider-bypass behavior for auth, protected capabilities, workstream agents, provider calls, audit/work traces, or authorization denials.

## Shared contract conventions

| Field | Contract |
|---|---|
| AuthContext | Every capability uses authenticated Account plus selected Tenant/Customer context, active Membership, role/scope checks, tenant/customer isolation, disabled-user denial, and backend authorization. |
| Surface envelope | Every rich surface uses `surfaceId`, `surfaceType`, `surfaceVersion`, owner functional agent, AuthContext summary, `correlationId`, trace ids, stale marker where relevant, redaction profile, typed payload, and actions. |
| Surface action | Every action, including read and surface-request actions, has `browserToolId`, `governedToolId`, `capabilityId`, input schema where needed, idempotency rule, audit event type, and result surface/system-message semantics. |
| Functional agent runtime | User-facing workstream turns remain request-based Akka `Agent` turns through governed managed-agent runtime: active `AgentDefinition`, prompt assembly, compact skill/reference manifests, `ToolPermissionBoundary`, authorized `readSkill(skillId)` / `readReferenceDoc(referenceId)`, `effects().tools(runtimeTools)`, provider fail-closed behavior, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace`. |
| Internal worker runtime | Durable model-driven background work is a candidate for Akka `AutonomousAgent`; if not implemented in a vertical, the surface must fail closed with `blocked_provider_or_runtime` rather than return fake progress or model-less recommendations. |
| System messages | Denials, validation failures, approval-required, deferred, blocked-provider/runtime, stale/reconnect, no-op, and success feedback are typed `system_message` surfaces, not ad hoc frontend strings. |
| Traces | Capability reads, action denials, approvals, provider/model use, prompt/skill/reference loads, tool calls, state changes, and protected data access emit AdminAuditEvent and/or work trace records linked by correlation id. |
| Tests | Each vertical names backend authorization/tenant-isolation/idempotency/audit tests, frontend rendering/action/forbidden/error tests, and local validation/smoke expectations at the stated scope. |

## Cross-workstream shell and surface runtime contract

Shared runtime work for `TASK-FC5-02-001` must implement the following before verticals add richer behavior.

| Contract item | Required shape |
|---|---|
| Shell request normalization | `show_surface`, `open_workstream`, `refresh_surface`, and `open_attention_item` from prompt, surface action, deep link, My Account panel, or system suggestion normalize to a `WorkstreamShellRequest` with honest origin metadata, canonical prompt, target workstream/surface, source action, scope, and `correlationId`. |
| Target rendering | Workstream switching renders the prompt-like request item in the target workstream only. Unauthorized or hidden targets return a safe `system_message` with no existence leak. |
| Rich surface API | Backend returns `SurfaceEnvelope<TData>` and `CapabilityActionResult` through protected APIs only after capability authorization. Frontend never fabricates protected payloads. |
| Action execution | Browser action request includes `actionId`, `browserToolId`, `governedToolId`, `capabilityId`, selected context, idempotency key when required, input, and `correlationId`; backend validates action identity against the current surface/capability. |
| Realtime/stale | Surface events carry tenant/customer scope, surface ids, sequence/event id, trace ids, and stale/reconnect semantics. Cross-context or malformed events are safe no-ops plus diagnostics. |
| Bootstrap guard | Initial app load still emits only the five v0 `markdown_response` core surfaces unless an explicit rich-surface shell request/action/API is made. |
| Shared tests | Prove five v0 bootstrap surfaces remain `markdown_response`; prove rich surface request, denial, idempotency validation, trace ids, stale/reconnect, and frontend secret boundary. |

## Workstream 1: My Account

### Identity

| Field | Contract |
|---|---|
| `workstreamId` | `my-account` |
| Functional agent | My Account Agent (`agent-my-account`) |
| Classification | Core foundation workstream; launched only from signed-in user tile/email in the lower-left rail user region. |
| Primary actors | Signed-in account for self-service; tenant/customer admin can only see their own My Account here, not other users. |
| Responsibility | Current user's profile, settings, selected context, authority basis, personal attention queue, and safe cross-workstream launch/status. |

### Attention and dashboard

| Attention category | Dashboard behavior | My Account / left rail effect |
|---|---|---|
| Personal action needed | Dashboard shows personal queue items from accessible workstreams only. | Contributes to My Account count; source workstream counts remain owned by source workstream. |
| Context/authority issue | Shows selected context, membership, role/capability basis, disabled/forbidden/system states. | Left rail hides unavailable workstreams or shows safe unavailable state without leaking names. |
| Profile/settings state | Shows profile/settings shortcuts and stale/saved/no-op states. | No cross-workstream count unless a personal action is needed. |
| Workstream status | Compact panels for accessible User Admin, Agent Admin, Audit/Trace, Governance/Policy. | Panel action opens target workstream through governed surface-request capability. |

### Human surface graph

| `surfaceId` | Type | Purpose | Actions |
|---|---|---|---|
| `surface-my-account-dashboard` | `dashboard` | Aggregate personal attention, profile/settings shortcuts, selected AuthContext and accessible workstream status panels. | `action-show-my-profile`, `action-show-my-settings`, `action-open-user-admin`, `action-open-agent-admin`, `action-open-audit-trace`, `action-open-governance-policy`, `action-sign-out`. |
| `surface-my-profile` | `detail-edit` | Browser-safe current profile fields; no role/membership editing. | `action-update-my-profile`, `action-open-audit-trace`. |
| `surface-my-settings` | `detail-edit` | Current user shell/preferences settings. | `action-update-my-settings`, `action-open-audit-trace`. |
| `surface-my-account-open-denied` | `system_message` | Safe denial for hidden/forbidden target workstream or context mismatch. | Recovery actions to profile/settings/audit where authorized. |
| `surface-v0-my-account-markdown` | `markdown_response` | Bootstrap v0 explanatory response. | Trace open only; no rich behavior claim. |

### Capabilities and governed-tools

| `capabilityId` | Governed-tool ids / exposure | Class | Substrate |
|---|---|---|---|
| `my_account.view_summary` | `my_account.view_summary`, `my_account.view_profile`, `my_account.view_settings`, `my_account.view_attention_summary`; browser-tool/read, agent-tool/read evidence. | Read/evidence | AuthContext resolver, Account/Profile/Settings state, attention View, HTTP endpoint, request-based Agent evidence tool. |
| `my_account.update_profile_settings` | `my_account.update_profile`, `my_account.update_settings`; browser-tool/command. | Command | Key Value Entity or durable profile/settings repository, endpoint, audit consumer/view. |
| `my_account.open_authorized_workstream` | `my_account.open_workstream`, `my_account.open_attention_item`; browser-tool/surface-request. | Read/surface-request | Capability resolver, shell request API, WorkstreamLogRepository append, system-message denial path. |
| `my_account.view_own_trace_refs` | `my_account.open_trace_ref`; browser-tool/trace, agent-tool/read. | Trace/audit | Audit/Trace View/API with self-scope redaction. |

### Internal workstream agent graph

- Virtual dashboard agent evaluates personal attention across accessible workstreams using read-only attention summaries.
- No durable worker is required for profile/settings save.
- Future personal digest worker is deferred unless implemented with a real AutonomousAgent task lifecycle and read-only `ToolPermissionBoundary`.

### Expertise bundle

| Field | Contract |
|---|---|
| Bundle id | `my-account-agent.expertise` |
| Prompt intent | Help the signed-in user understand profile, settings, selected context, personal attention, and safe workstream navigation. Refuse admin changes and cross-user data access. |
| Skills/references | Profile/settings self-service, AuthContext explanation, workstream navigation, denial recovery, trace-link help. |
| ToolPermissionBoundary | Read self profile/settings/context/attention; update own editable settings; open authorized workstreams; read own trace refs. No membership/role mutation. |
| Traces | PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, UserProfileDisplayed, UserSettingsDisplayed, UserProfileUpdateRequested, UserSettingsUpdateRequested, MyAccountOpen*Requested. |

### Tests

- Backend: view/update self only, tenant isolation, disabled account denial, forbidden context, idempotent update/no-op, audit trace creation.
- Frontend: dashboard/profile/settings render states, open-workstream denial as `system_message`, no hidden workstream leakage, action id/capability id validation.
- Local validation: explicit request opens rich My Account surfaces; bootstrap remains `surface-v0-my-account-markdown`.

## Workstream 2: User Admin

### Identity

| Field | Contract |
|---|---|
| `workstreamId` | `user-admin` |
| Functional agent | User Admin Agent (`agent-user-admin`) |
| Classification | Core foundation workstream. |
| Primary actors | Tenant Admin, Customer Admin where customer-scoped operations are allowed, SaaS Owner support with explicit support authority. |
| Responsibility | Users, invitations, memberships, roles/capabilities, disabled access, access review, support-access visibility, and related admin audit. |

### Attention and dashboard

| Attention category | Dashboard behavior | My Account / left rail effect |
|---|---|---|
| Pending/failed invitations | Cards for pending, expired, delivery failed; table filters open details. | Count visible to admins with invitation capability. |
| Risky/stale access | Access-review queue, blocked worker state, review result acceptance/rejection. | Count urgent/blocked when review worker blocked, result rejected, or policy issue exists. |
| Membership/role changes | Role preview, approval-required, last-admin/self-disable denials. | Count when human approval/review is needed. |
| Support/admin audit concern | Links to trace timeline for user-admin actions. | Audit/Trace owns detailed trace; User Admin shows local trace links. |

### Human surface graph

| `surfaceId` | Type | Purpose | Actions |
|---|---|---|---|
| `surface-user-admin-dashboard` | `dashboard` | Command center: active users, pending/failed invitations, access review status, trace links. | Display list, invite, resend/revoke, start/read access review, open trace. |
| `surface-user-admin-list` | `data-table` | Tenant/customer-scoped member directory and invitation summary. | `action-display-user-detail`, disable/reactivate, preview/apply roles, open trace. |
| `surface-user-admin-detail-admin` | `detail-edit` | Membership/account detail with allowed admin actions and last-admin/self-disable policy hints. | `action-useradmin-preview-role-change`, `action-useradmin-change-member-roles`, `action-useradmin-disable-member`, `action-useradmin-reactivate-member`. |
| `surface-user-admin-invitation-panel` | `form/workflow-status` | Invite, resend, revoke, delivery status, captured outbox/Resend state. | `action-invite-user`, `action-useradmin-resend-invitation`, `action-useradmin-revoke-invitation`. |
| `surface-user-admin-role-change-preview` | `decision` | Preview capability delta, affected workstreams, last-admin impact, policy hints. | `action-useradmin-change-member-roles`, trace. |
| `surface-user-admin-access-review` | `workflow-status` | Durable access-review task state, blocked-provider/runtime state, evidence and result review. | start/read/cancel/accept/reject access review. |
| `surface-v0-user-admin-markdown` | `markdown_response` | Bootstrap v0 explanatory response. | Trace open only; no rich behavior claim. |

### Capabilities and governed-tools

| `capabilityId` | Governed-tool ids / exposure | Class | Akka substrate |
|---|---|---|---|
| `USERADMIN_VIEW_OVERVIEW` | `user_admin.dashboard.read`; browser-tool/read, agent-tool/read evidence. | Read/evidence | UserDirectoryView, InvitationView, HTTP endpoint, request-based Agent evidence tool. |
| `USERADMIN_LIST_MEMBERS` | `user_admin.members.list`, `user_admin.member.detail`; browser-tool/read, agent-tool/read. | Read/evidence | View/query with tenant/customer filters. |
| `USERADMIN_LIST_INVITATIONS` | `user_admin.invitations.list`; browser-tool/read. | Read/evidence | InvitationView and captured outbox view. |
| `USERADMIN_SEND_INVITATION` | `user_admin.invitation.create`; browser-tool/command, workflow-tool, email internal-tool. | Command/workflow | Invitation entity/repository, InvitationWorkflow, Resend email service/captured outbox, Consumer, TimedAction expiry/reminder. |
| `USERADMIN_RESEND_INVITATION` | `user_admin.invitation.resend`; browser-tool/command. | Command | Invitation service/entity, email outbox, audit. |
| `USERADMIN_REVOKE_INVITATION` | `user_admin.invitation.revoke`; browser-tool/command. | Command | Invitation entity/workflow, idempotency, audit. |
| `USERADMIN_LIST_ROLES_CAPABILITIES` | `user_admin.roles_capabilities.list`; browser-tool/read, agent-tool/read evidence. | Read/evidence | Role/capability catalog service/view. |
| `USERADMIN_PREVIEW_ROLE_CHANGE` | `user_admin.role_change.preview`; browser-tool/proposal, agent-tool/proposal. | Proposal | Policy service, role delta calculator, decision surface. |
| `USERADMIN_CHANGE_MEMBER_ROLES` | `user_admin.member_roles.change`; browser-tool/command with approval where needed. | Command/approval | Membership entity/repository, workflow for approvals, audit. |
| `USERADMIN_UPDATE_MEMBER_STATUS` | `user_admin.member_status.update`; browser-tool/command. | Command/approval | Membership entity/repository, last-admin/self-disable policy, audit. |
| `UserAdminAccessReviewService.*` | `user_admin.access_review.start/read/cancel/accept_result/reject_result`; browser-tool/workflow, future AutonomousAgent agent-tool/read evidence. | Autonomous task/workflow | AccessReviewTask repository/entity, future AutonomousAgent worker, views, system-message fail-closed surface until real worker exists. |

### Internal workstream agent graph

- Virtual dashboard agent identifies failed invitations, stale memberships, role-risk deltas, blocked access-review tasks, and audit anomalies.
- AccessReviewAgent is a durable AutonomousAgent candidate for review investigation. It may read directory, roles, invitation history, and audit evidence only; it cannot mutate memberships.
- InvitationDraftAgent and RoleRecommendationAgent are optional request-based or AutonomousAgent candidates for drafts/proposals only. Side effects remain approval/command capabilities.

### Expertise bundle

| Field | Contract |
|---|---|
| Bundle id | `user-admin-agent.expertise` |
| Prompt intent | Explain user/invitation/member/role state, guide safe admin actions, draft recommendations, and escalate policy/approval issues. |
| Skills/references | Invitation lifecycle, role recommendation, access-review triage, last-admin/self-disable policy, support-access procedure, tenant role catalog. |
| ToolPermissionBoundary | Read directory/invitations/roles/audit; create/resend/revoke invitations if role allows; preview roles; request/apply approved role/status changes; start/read/cancel access-review task. No authority expansion from skill text. |
| Traces | PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, InvitationRequested/Resend/Revoke, UserAdminMemberRolesChanged, UserAdminAccessReview*, protected read traces. |

### Tests

- Backend: invitation create/resend/revoke idempotency, tenant isolation, wrong role denial, disabled user denial, last-admin/self-disable denial, role preview no-op, access-review blocked-provider/system-message behavior, audit events.
- Frontend: dashboard/list/detail/invitation/access-review surfaces, forms/actions, approval-required, forbidden/error/no-op states, trace links, secret boundary.
- Local validation: User Admin full-core surfaces work through API/UI; access-review worker cannot fake success until real AutonomousAgent runtime is implemented.

## Workstream 3: Agent Admin

### Identity

| Field | Contract |
|---|---|
| `workstreamId` | `agent-admin` |
| Functional agent | Agent Admin Agent (`agent-agent-admin`) |
| Classification | Core foundation and managed-agent runtime governance workstream. |
| Primary actors | Tenant Admin / governance steward; SaaS Owner support under explicit support authority. |
| Responsibility | Governed managed-agent `AgentDefinition`, prompts, skills, references, manifests, model refs, `ToolPermissionBoundary`, behavior proposals, reviews, tests, and agent work traces. |

### Attention and dashboard

| Attention category | Dashboard behavior | My Account / left rail effect |
|---|---|---|
| Behavior proposal pending | Catalog/dashboard lists pending prompt/skill/reference/tool-boundary/model changes. | Count for governance stewards. |
| Tool boundary risk | Diff/simulation surfaces show authority expansion, side-effecting tool grants, denied provider aliases. | Urgent count when high-risk boundary proposal needs decision. |
| Prompt/skill/reference lifecycle | Surfaces show active vs draft vs deprecated records and seed material. | Count for review/activation tasks. |
| Runtime trace issue | Trace surface links prompt assembly, skill/reference loads, denied loads, model invocation, AgentWorkTrace. | Audit/Trace owns detailed trace; Agent Admin shows local trace links. |

### Human surface graph

| `surfaceId` | Type | Purpose | Actions |
|---|---|---|---|
| `surface-agent-admin-catalog` | `list-search` | AgentDefinition catalog and readiness summary. | `action-display-agent-catalog`, `action-open-agent-detail`, seed material, trace. |
| `surface-agent-admin-detail` | `detail-edit` | AgentDefinition readiness, active prompt/model/manifests/tool boundary. | propose prompt diff, test prompt, manage model ref, trace. |
| `surface-agent-prompt-governance` | `governance-diff` | PromptVersion detail/diff/review. | propose prompt diff, submit/review/trace. |
| `surface-agent-skill-manifest-diff` | `governance-diff` | Skill/reference manifest entries and assignment diffs. | approve manifest, trace. |
| `surface-agent-tool-boundary-diff` | `governance-diff` | ToolPermissionBoundary detail, side-effect risk, simulation result. | simulate tool boundary, trace. |
| `surface-agent-model-refs` | `detail-edit` | ModelConfigRef and ModelPolicy display with provider-secret redaction. | request model ref change (denied/approval-required where appropriate), trace. |
| `surface-agent-seed-material` | `list-search` | Seeded default prompt/skill/reference/manifest/boundary provenance and checksums. | trace. |
| `surface-agent-test-console` | `workflow-status` | No-side-effect prompt assembly/loader/tool-boundary test. | `action-test-agent-prompt`, trace. |
| `surface-agent-behavior-proposal` | `decision` | Behavior change proposal evidence, risk, approval/activation/rollback. | submit, reject, activate, cancel, rollback, trace. |
| `surface-agent-admin-trace` | `audit-timeline` | Agent work trace timeline. | open audit/trace. |
| `surface-v0-agent-admin-markdown` | `markdown_response` | Bootstrap v0 explanatory response. | Trace open only; no rich behavior claim. |

### Capabilities and governed-tools

| `capabilityId` | Governed-tool ids / exposure | Class | Akka substrate |
|---|---|---|---|
| `agent_admin.list_definitions` | `agent_admin.definitions.list`; browser-tool/read, agent-tool/read evidence. | Read/evidence | Governed-agent repository/entity, view, endpoint. |
| `agent_admin.get_definition` | `agent_admin.definition.get`; browser-tool/read. | Read/evidence | AgentDefinition entity/repository, view. |
| `agent_admin.get_prompt_version` | `agent_admin.prompt_version.get`; browser-tool/read. | Read/evidence | PromptDocument/PromptVersion entity/repository. |
| `agent_admin.get_skill_version` | `agent_admin.skill_version.get`; browser-tool/read. | Read/evidence | SkillDocument/SkillVersion entity/repository. |
| `agent_admin.get_reference_version` | `agent_admin.reference_version.get`; browser-tool/read. | Read/evidence | ReferenceDocument/ReferenceVersion entity/repository. |
| `agent_admin.get_manifest` | `agent_admin.manifest.get`; browser-tool/read. | Read/evidence | AgentSkillManifest / AgentReferenceManifest repository. |
| `agent_admin.get_tool_boundary` | `agent_admin.tool_boundary.get`; browser-tool/read. | Read/evidence | ToolPermissionBoundary repository. |
| `agent_admin.get_model_ref` | `agent_admin.model_ref.get`; browser-tool/read. | Read/evidence | ModelConfigRef/ModelPolicy records; provider secrets never exposed. |
| `agent_admin.list_seed_material` | `agent_admin.seed_material.list`; browser-tool/read. | Read/evidence | Seed provenance records/view. |
| `agent_admin.simulate_tool_boundary` | `agent_admin.tool_boundary.simulate`; browser-tool/governance, internal policy tool. | Governance/proposal | Policy evaluator service/workflow; no mutation. |
| `agent_admin.draft_behavior_change` | `agent_admin.behavior_change.draft`, `agent_admin.prompt_test.run`; browser-tool/proposal/workflow, agent-tool/proposal. | Proposal/workflow | AgentRuntimeService, behavior proposal entity/workflow, no-side-effect Agent test path. |
| `agent_admin.submit_behavior_change_for_review` | `agent_admin.behavior_change.submit`; browser-tool/proposal. | Proposal | Workflow/entity, audit. |
| `agent_admin.approve_behavior_change` / `reject` | `agent_admin.behavior_change.approve/reject`; browser-tool/approval. | Approval | Workflow with human decision, audit. |
| `agent_admin.activate_behavior_change` / `rollback` / `cancel` | `agent_admin.behavior_change.activate/rollback/cancel`; browser-tool/command. | Command/governance | Event Sourced Entity or Workflow, governed seed/customization preservation, audit. |
| `audit.trace.read` | `agent_admin.trace.open`; browser-tool/trace. | Trace/audit | Audit/Trace view/API. |

### Internal workstream agent graph

- Virtual dashboard agent identifies pending behavior proposals, high-risk tool-boundary diffs, denied model refs, seed drift, missing traces, and evaluation needs.
- BehaviorEditingAgent can draft prompt/skill/tool-boundary changes as proposals only.
- Evaluation/replay worker is a future AutonomousAgent candidate when durable replay, model-backed evaluation, and result review surfaces are implemented. It cannot activate behavior.

### Expertise bundle

| Field | Contract |
|---|---|
| Bundle id | `agent-admin-agent.expertise` |
| Prompt intent | Explain and govern managed-agent behavior without expanding authority. Draft safe changes, explain traces, and route high-impact changes to human review. |
| Skills/references | Prompt governance, skill governance, reference governance, tool-boundary simulation, model-policy, seed/upgrade, behavior proposal review. |
| ToolPermissionBoundary | Read managed-agent records; run no-side-effect prompt assembly/test; draft proposals; simulate boundaries; submit/review/activate only when actor has governance authority and approval gate passes. |
| Traces | PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, behavior proposal audit events, model binding resolution, denied provider alias, tool-boundary denied/allowed. |

### Tests

- Backend: AgentDefinition/prompt/skill/reference/manifest/boundary read auth, cross-tenant denial, denied unassigned `readSkill`, prompt assembly trace, tool-boundary denial, proposal/approval/activation idempotency.
- Frontend: catalog/detail/diff/test console/decision surfaces, redaction of provider secrets and hidden prompt internals, action result states.
- Local validation: Agent Admin full-core surfaces use governed records and fail closed for missing provider/model config.

## Workstream 4: Audit/Trace

### Identity

| Field | Contract |
|---|---|
| `workstreamId` | `audit-trace` |
| Functional agent | Audit/Trace Agent (`agent-audit-trace`) |
| Classification | Core foundation observability and investigation workstream. |
| Primary actors | Auditors, Tenant Admin/Governance steward, SaaS Owner support with support authority. |
| Responsibility | Search, timeline, detail, failure evidence, redacted explanations, trace export request, and investigation guidance for security, authorization, data access, agent/tool/model, decision, workflow, and policy activity. |

### Attention and dashboard

| Attention category | Dashboard behavior | My Account / left rail effect |
|---|---|---|
| Authorization denials | Dashboard card and search filter for denials needing review. | Count for auditors/admins only. |
| Provider/runtime failures | Failure evidence surface for model/provider blocked behavior, no secrets. | Count when repeated blocked failures affect a workstream. |
| High-risk data/tool access | Trace detail/timeline links evidence and policies. | Count for anomalies where policy says human review is needed. |
| Investigation task | Summary task is a future worker; blocked-provider/runtime until real AutonomousAgent is implemented. | Blocked count only if explicitly started and blocked. |

### Human surface graph

| `surfaceId` | Type | Purpose | Actions |
|---|---|---|---|
| `surface-audit-trace-dashboard` | `dashboard` | Trace health, recent denials/failures, investigation shortcuts. | search, timeline, failure evidence, guide, start summary task. |
| `surface-audit-trace-search` | `list-search` | Scoped, redacted trace rows with filters. | detail, timeline, failure evidence, guide. |
| `surface-audit-trace-detail` | `detail` | One trace/audit record with redaction profile, data/tool/model refs. | timeline, failure evidence, guide. |
| `surface-audit-trace-timeline` / `surface-audit-timeline` | `audit-timeline` | Correlation timeline across workstream, capability, model/tool, approval, and state events. | detail, failure evidence, guide. |
| `surface-audit-trace-failure-evidence` | `evidence-bundle` | Provider/runtime/auth failure evidence without secrets or hidden prompts. | timeline, guide. |
| `surface-audit-trace-investigation-guide` | `markdown_response` or `system_message` | Backend-authorized next steps and safe explanation. | search/timeline/open related traces. |
| `surface-audit-trace-summary-task` | `workflow-status` | Blocked summary worker readiness state until real worker exists. | start summary task, search, timeline. |
| `surface-v0-audit-trace-markdown` | `markdown_response` | Bootstrap v0 explanatory response. | Trace open only; no rich behavior claim. |

### Capabilities and governed-tools

| `capabilityId` | Governed-tool ids / exposure | Class | Akka substrate |
|---|---|---|---|
| `audit.trace.dashboard.read` | `audit_trace.dashboard.read`; browser-tool/read, agent-tool/read evidence. | Read/evidence | AuditTraceView/dashboard projection, endpoint. |
| `audit.trace.search` | `audit_trace.search`; browser-tool/read, agent-tool/read evidence. | Trace/audit | AuditTraceView/search, scoped filters, endpoint. |
| `audit.trace.detail.read` | `audit_trace.detail.read`; browser-tool/read. | Trace/audit | Trace entity/view/detail API. |
| `audit.trace.timeline.read` | `audit_trace.timeline.read`; browser-tool/read. | Trace/audit | Correlation view/projection. |
| `audit.trace.failureEvidence.read` | `audit_trace.failure_evidence.read`; browser-tool/read, agent-tool/read evidence. | Trace/audit | Trace repository/view with redaction. |
| `audit.trace.investigationGuide.read` | `audit_trace.investigation_guide.read`; browser-tool/read, request-based Agent explanation where provider configured. | Read/evidence | AuditTraceService plus request-based Agent explanation path. |
| `audit.trace.summaryTask.start` | `audit_trace.summary_task.start/read/cancel`; browser-tool/workflow, future AutonomousAgent agent-tool. | Autonomous task/workflow | Future AuditSummary AutonomousAgent + workflow/task view; current full-core task must either implement real worker or keep fail-closed. |
| `audit.trace.read` | Reusable trace open from other workstreams. | Trace/audit | Audit/Trace API and views. |

### Internal workstream agent graph

- Virtual dashboard agent identifies repeated denials, provider/runtime failures, suspicious data/tool access, missing trace links, and correlation clusters.
- AuditSummaryAgent is a durable AutonomousAgent candidate for summaries/investigations; read-only evidence tools only, no mutation authority, no raw secrets/prompt content.
- Request-based Audit/Trace Agent may explain already-authorized evidence but cannot bypass redaction or search scope.

### Expertise bundle

| Field | Contract |
|---|---|
| Bundle id | `audit-trace-agent.expertise` |
| Prompt intent | Help authorized users find and understand traces, denials, model/tool/provider failures, and policy/audit evidence with strict redaction. |
| Skills/references | Trace search, correlation timeline reading, denial diagnosis, provider failure explanation, redaction/support boundaries, export request policy. |
| ToolPermissionBoundary | Read scoped trace/dashboard/search/detail/timeline/failure evidence; no mutation except bounded export/request capabilities if separately implemented. |
| Traces | Trace reads themselves are audited; AgentWorkTrace for explanations; denied cross-tenant/support-only reads; redaction profile recorded. |

### Tests

- Backend: tenant/customer scoped search, auditor vs admin/support redaction, forbidden hidden trace not found/forbidden, provider secret redaction, trace-read audit, summary worker fail-closed/no fake success.
- Frontend: search/timeline/detail/failure/guide rendering, filters, stale/reconnect, malformed event safe no-op, no secret leakage.
- Local validation: Audit/Trace rich reads work through backend/API/UI; summary worker only succeeds if real AutonomousAgent task runtime is implemented and configured.

## Workstream 5: Governance/Policy

### Identity

| Field | Contract |
|---|---|
| `workstreamId` | `governance-policy` |
| Functional agent | Governance/Policy Agent (`agent-governance-policy`) |
| Classification | Core foundation policy, approval, simulation, and behavior governance workstream. |
| Primary actors | Governance steward, Tenant Admin with policy authority, SaaS Owner support under explicit support authority. |
| Responsibility | Policy registry, detail, simulation, proposals, decisions/approvals, activation/rollback, decision cards, and policy/work traces. |

### Attention and dashboard

| Attention category | Dashboard behavior | My Account / left rail effect |
|---|---|---|
| Proposal pending | Dashboard and proposal surfaces show draft/submitted/needs-review policy proposals. | Count for governance stewards. |
| Approval needed | Decision cards show evidence, risk, confidence, impact, alternatives. | Urgent count when approval gate blocks work. |
| Simulation issue | Simulation surface shows policy impact, warnings, affected capabilities/workstreams. | Count if policy conflict needs human review. |
| Activation/rollback blocked | System-message/decision surface shows why command is blocked or approval-required. | Count for blocked governance work. |
| Impact analysis worker | Future worker; blocked-provider/runtime until real model-backed AutonomousAgent exists. | Blocked count when explicitly started. |

### Human surface graph

| `surfaceId` | Type | Purpose | Actions |
|---|---|---|---|
| `surface-governance-policy-dashboard` | `dashboard` | Policy health, pending proposals, recent decisions, simulation shortcuts. | dashboard/list/draft/simulate/start impact analysis/open audit. |
| `surface-governance-policy-inventory` | `list-search` | Policy registry and current versions. | read detail, draft proposal, simulate, open audit. |
| `surface-governance-policy-detail` | `detail` | Policy clauses, thresholds, capability/workstream links, active version. | draft proposal, simulate, open audit. |
| `surface-governance-policy-proposal` | `decision/proposal` | Draft/submitted proposal, evidence, change summary. | submit, simulate, decide, activate, open audit. |
| `surface-governance-policy-simulation` | `evidence-bundle` | Simulation results, impacted workstreams/capabilities, risk/confidence. | decide, activate if approved, rollback/open audit. |
| `surface-governance-policy-decision` | `decision` | Human approval/rejection with evidence, alternatives, trace. | activate/rollback/open audit. |
| `surface-governance-policy-activation-blocked` | `system_message` | Safe blocked/approval-required activation result. | rollback/open audit/list. |
| `surface-governance-policy-rollback-blocked` | `system_message` | Safe blocked/approval-required rollback result. | list/open audit. |
| `surface-governance-policy-impact-analysis` | `workflow-status` | Future worker readiness/fail-closed state. | start impact analysis/open audit. |
| `surface-v0-governance-policy-markdown` | `markdown_response` | Bootstrap v0 explanatory response. | Trace open only; no rich behavior claim. |

### Capabilities and governed-tools

| `capabilityId` | Governed-tool ids / exposure | Class | Akka substrate |
|---|---|---|---|
| `governance.policy.read` | `governance_policy.dashboard.read`, `governance_policy.inventory.list`, `governance_policy.detail.read`; browser-tool/read, agent-tool/read evidence. | Read/evidence | GovernancePolicyView/repository, endpoint. |
| `governance.policy.propose` | `governance_policy.proposal.draft`, `governance_policy.proposal.submit`; browser-tool/proposal, agent-tool/proposal. | Proposal | Event Sourced PolicyProposal entity or Workflow, audit. |
| `governance.policy.simulate` | `governance_policy.simulation.run`; browser-tool/governance, future worker read-only internal-tool. | Governance/read evidence | Deterministic simulation service/view; optional workflow for long runs. |
| `governance.policy.approve` | `governance_policy.proposal.decide`; browser-tool/approval. | Approval | Workflow or Event Sourced Entity with human decision, audit. |
| `governance.policy.activate` | `governance_policy.policy.activate`; browser-tool/command after approval. | Command/governance | Event Sourced Entity/Workflow, policy view refresh, audit. |
| `governance.policy.rollback` | `governance_policy.policy.rollback`; browser-tool/command after approval. | Command/governance | Event Sourced Entity/Workflow, policy view refresh, audit. |
| `governance.policy.analysis.start` | `governance_policy.impact_analysis.start/read/cancel`; browser-tool/workflow, future AutonomousAgent agent-tool. | Autonomous task/workflow | Future ImpactAnalysis AutonomousAgent + workflow/task view; fail-closed until implemented with provider/tool boundary. |
| `audit.trace.read` | `governance_policy.trace.open`; browser-tool/trace. | Trace/audit | Audit/Trace API/view. |

### Internal workstream agent graph

- Virtual dashboard agent identifies proposals needing decision, policy conflicts, simulations requiring review, activation/rollback blockers, and traces requiring evidence.
- PolicyImpactAnalysisAgent is a durable AutonomousAgent candidate when impact analysis spans proposal state, trace evidence, capability inventory, model/provider work, progress, cancellation, and human review. It has read-only evidence tools and cannot approve/activate/rollback.
- Governance/Policy request-based Agent can explain policy state and draft proposals, but activation remains backend-authorized approval/command capability.

### Expertise bundle

| Field | Contract |
|---|---|
| Bundle id | `governance-policy-agent.expertise` |
| Prompt intent | Explain policies, draft proposals, interpret simulations, prepare decision evidence, and enforce human approval for consequential governance changes. |
| Skills/references | Policy clause review, decision card drafting, simulation interpretation, approval/exception policy, rollback safety, governance trace use. |
| ToolPermissionBoundary | Read policies/proposals/simulations/traces; draft/submit proposals; run simulations; decide/activate/rollback only with explicit role and approval gate. |
| Traces | PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, policy proposal/decision/activation/rollback traces, simulation evidence refs. |

### Tests

- Backend: policy read scoping, proposal draft/submit idempotency, simulation read-only, decision approval gate, activation/rollback blocked without approval, tenant isolation, audit/work traces.
- Frontend: registry/detail/simulation/proposal/decision/system-message/impact-analysis surfaces, approval-required and blocked states, trace links, secret boundary.
- Local validation: Governance/Policy full-core rich behavior works through runtime/API/UI; impact analysis does not fake worker success without real AutonomousAgent runtime and provider config.

## Cross-workstream implementation order

1. Shared rich surface/action runtime and shell request pipeline, preserving five v0 `markdown_response` bootstrap.
2. My Account rich self-service and aggregate attention surfaces because it owns personal cross-workstream launch/status.
3. User Admin rich vertical because other workstreams depend on roles/capabilities/membership and admin audit.
4. Agent Admin rich vertical because governed `AgentDefinition`, prompt/skill/reference/manifest/model/tool-boundary lifecycle underpins workstream expertise.
5. Audit/Trace rich vertical because every protected action and agent/tool/model path needs searchable evidence.
6. Governance/Policy rich vertical because approvals, simulations, decisions, and policy changes depend on prior traces and agent-governance foundations.
7. Terminal validation across backend, frontend, traces, provider fail-closed, and local smoke.

## Bounded follow-up decisions

No new blocking questions were discovered for this contract task. The existing pending tasks already split implementation into shared runtime plus five verticals and terminal validation. Later vertical tasks may append bounded follow-up tasks only if implementation exposes a concrete missing runtime/API/UI path, provider boundary, or security validation gap.

## Contract readiness checklist

- [x] Every full-core workstream has a `workstreamId`, functional agent, attention model, surfaces, capabilities, governed-tools, Akka substrate candidates, expertise bundle, traces, and tests.
- [x] Every planned surface action maps to `browserToolId`, `governedToolId`, and `capabilityId` semantics.
- [x] Request-based workstream Agent and durable AutonomousAgent candidate boundaries are separated.
- [x] `AgentDefinition`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` obligations are explicit.
- [x] v0 bootstrap remains limited to five `markdown_response` surfaces; rich behavior requires explicit full-core request/action/API.
- [x] No runtime feature is claimed implemented by this document.
