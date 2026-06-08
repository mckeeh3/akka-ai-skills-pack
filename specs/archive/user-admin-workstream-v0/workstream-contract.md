# User Admin Workstream v0 Contract

## Purpose

Define the v0 `User Admin Agent` vertical for the secure AI-first SaaS starter/reference runtime. This contract inherits:

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`

The v0 workstream is vertically useful when authorized tenant/customer administrators can inspect user administration readiness, manage invitations and memberships through governed backend capabilities, review role/capability assignments, start or inspect bounded access-review work, and receive safe request/response guidance from the User Admin Agent with trace links.

## Scope boundary

In scope:

- one role-authorized `User Admin` functional workstream;
- request/response composer turns backed by a request-based Akka `Agent` when model-backed guidance is enabled;
- deterministic authorization, validation, invitation, membership, role/capability, projection, idempotency, and trace services;
- optional Akka `AutonomousAgent` task support only for durable access-review or admin-risk investigation work;
- structured surfaces/actions for invitation readiness, user/member directory, role/capability assignments, access-review status, safe denials, and trace links;
- local runtime/API/UI validation for the named v0 scope.

Out of scope:

- full Agent Admin, Audit/Trace, Governance/Policy, or domain-specific workstream completion;
- public self-registration or silent privileged account creation;
- prompt-only authorization, frontend-only permission checks, or deterministic/model-less normal runtime substitutes for model-backed agent behavior;
- broad full-core SaaS readiness beyond this workstream's v0 vertical.

## Functional agent

| Field | Contract |
|---|---|
| Functional agent | `User Admin Agent` |
| Workstream id | `user_admin` |
| Primary users | Tenant administrators, customer administrators where customer-scoped administration is enabled, SaaS owner/support roles only through explicitly separate support/SaaS-owner authority. |
| Responsibility | Help authorized administrators understand user administration readiness, invite users, manage memberships and roles/capabilities, review access risk, explain denials, and navigate traces without granting authority through prompt text. |
| Normal turn type | Request/response Akka `Agent` for bounded composer turns and `markdown_response`/structured surface responses. |
| Durable background type | Akka `AutonomousAgent` only for access-review/admin-risk investigations with durable task ids, progress snapshots, notifications, cancellation/failure, and result surfaces. |
| Deterministic services | Authorization, membership status checks, invitation lifecycle, role/capability mutation validation, projection/query shaping, idempotency, audit event creation, trace redaction, and provider/configuration fail-closed checks. |

## AuthContext and authority

Every protected capability must resolve a selected `AuthContext` before reading or mutating state.

Required backend gates:

- authenticated Account;
- selected tenant/customer context as applicable;
- active membership in the selected context;
- non-disabled user/account status;
- required role/scope/capability for the requested operation;
- tenant/customer filters on every read and write;
- separate support/SaaS-owner authority when such callers are introduced;
- safe denial and audit/work-trace emission for missing, disabled, cross-tenant, unsupported-context, or insufficient-authority cases.

Frontend launcher visibility, buttons, prompt hints, hidden fields, and loaded skill text are not authorization controls.

## Structured surfaces and actions

### `user_admin_overview`

Purpose: summarize user administration readiness and next steps.

States:

- `loading`
- `ready`
- `needs_invitation_setup`
- `needs_membership_review`
- `forbidden`
- `blocked_provider_or_runtime`
- `validation_error`

Actions:

- `refresh_user_admin_overview` → `USERADMIN_VIEW_OVERVIEW`
- `open_invitation_panel` → surface request for `user_admin_invitations`
- `open_member_directory` → surface request for `user_admin_member_directory`
- `open_access_review` → surface request for `user_admin_access_review`
- `show_trace` → `USERADMIN_VIEW_TRACE_REFERENCE` or delegated Audit/Trace deep link when available

### `user_admin_invitations`

Purpose: manage invitation lifecycle and readiness.

Actions:

- `list_invitations` → `USERADMIN_LIST_INVITATIONS`
- `send_invitation` → `USERADMIN_SEND_INVITATION`
- `resend_invitation` → `USERADMIN_RESEND_INVITATION`
- `revoke_invitation` → `USERADMIN_REVOKE_INVITATION`
- `show_invitation_trace` → `USERADMIN_VIEW_TRACE_REFERENCE`

### `user_admin_member_directory`

Purpose: list scoped accounts/memberships and inspect status.

Actions:

- `list_members` → `USERADMIN_LIST_MEMBERS`
- `update_member_status` → `USERADMIN_UPDATE_MEMBER_STATUS`
- `change_member_roles` → `USERADMIN_CHANGE_MEMBER_ROLES`
- `show_member_trace` → `USERADMIN_VIEW_TRACE_REFERENCE`

### `user_admin_role_capability_matrix`

Purpose: show role/capability basis for administration and allowed changes.

Actions:

- `list_roles_capabilities` → `USERADMIN_LIST_ROLES_CAPABILITIES`
- `preview_role_change` → `USERADMIN_PREVIEW_ROLE_CHANGE`
- `apply_role_change` → `USERADMIN_CHANGE_MEMBER_ROLES`

### `user_admin_access_review`

Purpose: expose durable access-review/admin-risk investigation progress and results when the contract's AutonomousAgent-backed task support is implemented.

Actions:

- `start_access_review` → `USERADMIN_START_ACCESS_REVIEW_TASK`
- `view_access_review_progress` → `USERADMIN_VIEW_ACCESS_REVIEW_TASK`
- `cancel_access_review` → `USERADMIN_CANCEL_ACCESS_REVIEW_TASK`
- `open_access_review_result` → `USERADMIN_VIEW_ACCESS_REVIEW_RESULT`

If AutonomousAgent support is not yet implemented, this surface must render a safe unavailable/blocked state rather than fake progress or deterministic AI-like findings.

### `user_admin_agent_response`

Purpose: render safe `markdown_response` or typed guidance from the request/response User Admin Agent.

Requirements:

- response content is sanitized before HTML rendering;
- response includes trace/correlation references where available;
- model/provider missing configuration returns an actionable blocked surface and trace;
- agent tool results are scoped, redacted, and trace-linked.

## Capability classes in this workstream

The companion `capability-inventory.md` defines the initial v0 capabilities. Capability classes used here:

- read/evidence capabilities for overview, lists, role/capability evidence, trace references, and task progress;
- command capabilities for invitation, membership status, and role changes;
- proposal/preview capability for role changes before commit;
- autonomous task capabilities for access-review start/read/cancel/result;
- trace/audit capability for visible correlation and audit references;
- request/response agent turn capability for User Admin Agent guidance.

## Agent-type selection

### Request/response Akka Agent

Use for normal composer turns, for example:

- explain why an invitation is blocked;
- summarize readiness next steps;
- help interpret member status or role/capability evidence;
- draft a safe recommendation without applying a side effect.

Completion requires the governed runtime path from the shared contract: active managed-agent `AgentDefinition`, approved prompt, compact skill/reference manifests, model configuration, `ToolPermissionBoundary`, governed `readSkill`/`readReferenceDoc` loader tools when assigned, concrete Akka Agent invocation with `effects().tools(runtimeTools)`, provider boundary use, and prompt/skill/reference/model/tool/work traces.

### Akka AutonomousAgent

Use only for durable access-review/admin-risk investigations when implementation includes:

- task start/query/result/cancel capabilities;
- tenant/customer-scoped task ids;
- progress snapshots and notification mapping;
- result and failure/cancellation surfaces;
- tool-boundary checks and audit/work traces;
- tests for lifecycle, authorization, denials, and trace emission.

### Deterministic non-AI services

Use for mechanical behavior:

- authorization decisions and capability resolution;
- invitation email/outbox lifecycle and idempotency;
- membership/role validation and no-op detection;
- tenant/customer query filters and redaction;
- audit event and trace normalization;
- frontend-safe DTO shaping.

These services must not be described as model-backed agents.

## Exposure channels

Allowed v0 exposure channels:

- Workstream structured surface actions;
- browser HTTP/API routes backing those actions;
- request/response agent tools for selected read/preview/request capabilities;
- deterministic workflows/timers/consumers only where invitation lifecycle or retry semantics require them;
- AutonomousAgent task APIs only for access-review/admin-risk investigation lifecycle;
- trace links or correlation ids that can later deep-link into Audit/Trace.

Each exposure channel must enforce the same capability contract. Agent tools must call governed capability facades or component methods that enforce AuthContext and audit; they must not bypass backend checks.

## Trace and audit requirements

Emit or preserve trace/audit records for:

- user administration overview reads where protected data is accessed;
- invitation send/resend/revoke and delivery/outbox outcomes;
- membership status and role/capability changes, including no-op/idempotent cases;
- denied user-admin reads/actions;
- prompt assembly, model invocation, tool invocation, skill/reference load, provider failure, and agent response;
- access-review AutonomousAgent task start/progress/result/cancel/fail events when implemented;
- trace-reference lookups and redaction decisions.

UI surfaces must show correlation ids or trace links for consequential actions and denials.

## Provider and fail-closed rules

Model-backed User Admin Agent behavior must fail closed if provider/model/security/tool-boundary configuration is missing, blank, disabled, or unauthorized. The user-visible result must be a safe blocked/error surface with an actionable explanation and trace reference. It must not silently return canned User Admin guidance as normal runtime behavior.

## Validation path

The runtime implementation tasks that follow this contract must prove the smallest local path for the stated scope:

- backend tests for capability success, validation, forbidden/missing authority, tenant isolation, disabled users, idempotency/no-op behavior, audit/work trace, and provider fail-closed behavior where model-backed;
- frontend tests/typecheck for launcher visibility, surfaces/actions, denial/blocked states, trace links, sanitized markdown, and secret-boundary expectations;
- local API/UI smoke or starter validation command when named runtime behavior changes;
- `git diff --check` for every task.

If provider credentials are unavailable, validation may verify explicit skip/block behavior only while preserving fail-closed runtime semantics; it must not mark a model-backed normal path complete through deterministic fallback output.
