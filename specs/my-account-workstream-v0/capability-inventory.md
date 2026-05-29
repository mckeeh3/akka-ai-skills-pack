# My Account Workstream v0 Capability Inventory

## Inventory rules

These capabilities are the backend contract for My Account v0. Exposure through browser actions, HTTP APIs, request/response Akka Agent tools, shell requests, views, workflows, timers, consumers, or optional Akka `AutonomousAgent` tasks must preserve the same authority, validation, idempotency, audit/trace, and denial semantics.

Common requirements for all capabilities:

- authenticated account;
- selected `AuthContext` when tenant/customer-scoped data or actions are requested;
- active membership for selected tenant/customer scopes;
- backend role/capability checks independent of frontend visibility and prompt text;
- safe denial shape with correlation id and trace reference;
- tenant/customer filters on every scoped read/write;
- no provider secrets or backend-only policy/tool-boundary data in outputs;
- durable audit/work trace for protected reads, writes, denials, and agent/tool activity.

## Capability summary

| Capability id | Class | Primary exposure | Akka substrate selection | Agent-type selection |
|---|---|---|---|---|
| `my_account.view_summary` | Read/evidence | `/api/me`, My Account surface, optional read-only agent tool | View/API + deterministic service | deterministic non-AI service; request Agent may consume as tool evidence |
| `my_account.view_context` | Read/evidence | Context panel, `/api/me` scoped detail, optional read-only agent tool | View/API + deterministic service | deterministic non-AI service; request Agent may consume as tool evidence |
| `my_account.update_profile_settings` | Command | Personal settings form/API; optional proposal-only agent tool unless explicitly allowed | Key Value Entity or existing account/settings component + API | deterministic non-AI command; request Agent may draft/explain only by default |
| `my_account.list_next_steps` | Read/evidence | Next steps panel and agent evidence | View/API + deterministic aggregator | deterministic non-AI service; request Agent may consume as tool evidence |
| `my_account.open_authorized_workstream` | Shell request | Surface action/deep link shell request | API/service facade | deterministic non-AI resolver |
| `my_account.ask_agent` | Request/response | My Account composer | Request-based Akka Agent + governed runtime | request-based Akka `Agent` |
| `my_account.view_own_trace_refs` | Trace/audit read | Trace links/correlation details | scoped trace view/API | deterministic non-AI service; request Agent may cite returned refs |

## `my_account.view_summary`

- Purpose: return browser-safe account, profile, settings, selected-context headline, authority summary, and trace/correlation metadata for the signed-in user tile and My Account summary surface.
- Actors/callers: signed-in human account holder, My Account UI, `/api/me` handler, My Account request-based Agent as read-only evidence tool when authorized.
- AuthContext: authenticated account required; selected tenant/customer `AuthContext` required for scoped authority details; account-global safe fields may render without selected context plus a blocked scoped-actions state.
- Inputs: account id from session/JWT; selected context id if present; optional correlation id; no caller-supplied target account id for self view.
- Outputs: browser-safe account id, email/display name, profile/settings snapshot, selected tenant/customer label, membership status, role/capability summary, workstream launcher metadata, trace refs, safe denial/blocked state.
- Data access: Account, UserProfile, UserSettings, selected Membership, Role/Permission/Capability summaries, tenant/customer labels; all scoped by account and selected context.
- Side effects: protected-read audit/work trace; no state mutation.
- Idempotency: repeat reads return current projection; duplicate correlation ids append/read trace according to platform trace rules without mutation.
- Policy/approval: no approval; denied for disabled account, missing selected scoped context when scoped fields are requested, inactive membership, or tenant/customer mismatch.
- Audit/trace: record account id, selected context, data categories read, capability decision, denial reason, correlation id, and returned trace refs.
- Exposure channels: `/api/me`, My Account user tile/summary card, optional read-only request Agent tool, internal view query.
- Tests: success with selected context, safe no-selected-context blocked state, disabled account denial, wrong-tenant context denial, browser-secret boundary, audit/read trace, UI launch from user tile only.

## `my_account.view_context`

- Purpose: explain the selected tenant/customer `AuthContext`, membership status, role/capability basis, and why actions are allowed or unavailable.
- Actors/callers: signed-in human account holder, My Account UI, My Account request-based Agent as read-only evidence tool when authorized.
- AuthContext: authenticated account plus selected tenant/customer context; active membership required for authority details.
- Inputs: selected context id from backend session/state; optional requested detail level; correlation id.
- Outputs: selected tenant/customer identifiers safe for browser, membership id/status, role/scope/capability summaries, unavailable-action reasons, trace ref, safe denial.
- Data access: Membership, Role, Permission/Capability projections, tenant/customer labels; scoped to selected context and account.
- Side effects: protected-read trace; no mutation.
- Idempotency: read-only.
- Policy/approval: no approval; fail closed for missing, stale, cross-tenant, inactive, or disabled context.
- Audit/trace: record authority basis, missing/denied authority details, redaction markers, correlation id.
- Exposure channels: context panel, `/api/me` scoped detail, optional read-only request Agent tool, internal view query.
- Tests: selected-context success, missing-context blocked surface, inactive membership denial, cross-tenant denial, disabled user denial, trace creation, redaction of backend-only capability metadata.

## `my_account.update_profile_settings`

- Purpose: let the signed-in user update allowed self-service profile/settings fields without administrative authority escalation.
- Actors/callers: signed-in human account holder through UI/API; My Account request-based Agent may draft guidance or propose values but must not perform side effects unless a later explicit tool boundary grants it.
- AuthContext: authenticated active account; selected context required only for context-scoped preferences; backend verifies the caller is updating self-owned settings.
- Inputs: typed settings command with allowed fields only, validation version/etag if available, idempotency key, correlation id.
- Outputs: updated browser-safe settings/profile snapshot, validation errors, no-op result, trace ref, safe denial.
- Data access: UserProfile/UserSettings and, for scoped preferences, selected Membership/context projection; no role/capability mutation.
- Side effects: persist allowed profile/settings changes; emit audit/work trace; may publish profile/settings changed event if existing starter architecture uses one.
- Idempotency: duplicate idempotency key returns prior result; identical update may return no-op with trace; conflicting version returns validation/conflict error.
- Policy/approval: no human approval for allowed self fields; deny unsupported fields, identity/security-sensitive changes, cross-user target ids, disabled account, inactive membership, or missing authority.
- Audit/trace: record changed field names/categories, old/new redaction markers, actor account id, context if scoped, validation failures, idempotency key, and correlation id.
- Exposure channels: settings form/browser API; optional agent proposal/explanation surface only by default; internal component command.
- Tests: success, validation for unsupported fields, forbidden cross-user target, disabled account denial, scoped tenant isolation, idempotent duplicate, no-op identical update, audit/work trace, frontend safe rendering.

## `my_account.list_next_steps`

- Purpose: provide aggregate next-step guidance and links for authorized sibling workstreams without granting authority or duplicating their capabilities.
- Actors/callers: signed-in human account holder, My Account UI, My Account request-based Agent as read-only evidence tool when authorized.
- AuthContext: authenticated account; selected context required for tenant/customer-scoped sibling workstream availability.
- Inputs: selected context, optional attention categories, correlation id.
- Outputs: ordered next-step cards with workstream id, label, reason, severity/status, allowed/openable flag, blocked reason, trace/correlation refs; no secrets or raw policy internals.
- Data access: `/api/me` capability summary, workstream launcher projection, readiness/attention summaries from existing sibling baseline where available; tenant/customer scoped.
- Side effects: protected-read trace; no mutation; no sibling workstream action execution.
- Idempotency: read-only aggregator.
- Policy/approval: no approval; backend filters or marks unavailable workstreams according to actual capabilities and selected context.
- Audit/trace: record source projections consulted, selected context, capability decisions, redaction markers, correlation id.
- Exposure channels: next steps panel, My Account Agent evidence tool, possible system-message surface.
- Tests: authorized links visible, unauthorized links hidden or safely blocked, no-selected-context guidance, cross-tenant filtering, audit/read trace, UI action maps to `my_account.open_authorized_workstream`.

## `my_account.open_authorized_workstream`

- Purpose: resolve a My Account next-step action into an authorized shell request to open another workstream or return a safe denial/system message.
- Actors/callers: signed-in human account holder via surface action; shell/router; deterministic backend resolver.
- AuthContext: authenticated account plus selected context for scoped targets; required capability for target workstream/action.
- Inputs: target workstream id, optional target surface/action id, origin surface id, correlation id, idempotency/request id.
- Outputs: shell request result (`open_workstream`, `show_surface`, or safe denial/system-message), target route metadata safe for browser, trace ref.
- Data access: workstream registry/launcher projection, capability summary, selected context/membership, target visibility rules.
- Side effects: audit/work trace for shell request and denial; no target-side mutation.
- Idempotency: repeated same request returns equivalent shell resolution; no duplicate side effects beyond trace policy.
- Policy/approval: no approval; denies missing target capability, missing selected context, disabled account, inactive membership, or target unavailable.
- Audit/trace: record origin, target, selected context, authority basis, denial reason, correlation id.
- Exposure channels: structured surface action, browser API/shell request resolver, internal service.
- Tests: authorized open, unauthorized target denial, missing context blocked surface, stale/unknown target validation, tenant isolation, trace link visible, no frontend-only authorization.

## `my_account.ask_agent`

- Purpose: answer My Account questions and guide safe self-service using the governed request/response Akka Agent runtime.
- Actors/callers: signed-in human account holder through My Account composer; workstream runtime; request-based Akka Agent.
- AuthContext: authenticated account and selected context for scoped answers/tools; active managed-agent `AgentDefinition` and `ToolPermissionBoundary` required.
- Inputs: user message, workstream/session id, selected context, allowed surface/action context, correlation id; provider/model config resolved backend-side.
- Outputs: sanitized `markdown_response` or typed blocked/error surface, trace refs for prompt/model/tool/skill/reference loads, safe denial if unauthorized.
- Data access: only through authorized runtime tools such as `readSkill`, `readReferenceDoc`, `my_account.view_summary`, `my_account.view_context`, and `my_account.list_next_steps`; no direct secret access.
- Side effects: durable workstream request/response timeline entry; prompt/model/tool/skill/reference/work traces; no profile/settings mutation by default.
- Idempotency: duplicate request id returns prior response or safe duplicate handling according to workstream runtime; repeated prompts create distinct traced turns unless deduped by request id.
- Policy/approval: must fail closed for missing provider/model config, inactive governed managed-agent definition, missing tool boundary, unassigned skill/reference, unauthorized tool, disabled account, or missing scoped context for scoped tools.
- Audit/trace: `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, model invocation/provider failure trace, tool invocation/denial trace, `AgentWorkTrace`, response trace ref.
- Exposure channels: My Account composer; request-based Akka Agent component; optional streaming/HTTP endpoint if starter uses it.
- Tests: authorized model-backed path invokes concrete Agent through governed runtime, missing provider blocked surface with trace, denied tool trace, unassigned skill/reference denial, disabled user denial, tenant isolation for tools, sanitized markdown rendering, no deterministic normal-runtime fallback.

## `my_account.view_own_trace_refs`

- Purpose: let the user inspect or navigate to trace/correlation references for their own My Account actions and denials, subject to redaction and Audit/Trace availability.
- Actors/callers: signed-in human account holder, My Account UI trace links, My Account Agent as evidence-citation support when authorized.
- AuthContext: authenticated account; selected context required for tenant/customer-scoped traces; only traces involving the account and selected context are readable.
- Inputs: trace id or correlation id, selected context, optional redaction level/detail request.
- Outputs: redacted trace summary, event type, timestamp, capability id, denial/decision summary, related Audit/Trace deep link when authorized, safe denial.
- Data access: AdminAuditEvent/AgentWorkTrace/prompt-skill-reference/tool/capability trace projections; filtered by account and selected tenant/customer.
- Side effects: protected-read audit trace for trace access; no mutation.
- Idempotency: read-only.
- Policy/approval: no approval; deny cross-user, cross-tenant, privileged support/SaaS-owner-only, secret-bearing, or unavailable trace details.
- Audit/trace: record trace-read attempt, redaction decision, selected context, denial reason, correlation id.
- Exposure channels: My Account trace link, optional Audit/Trace workstream deep link, internal trace view/API, read-only agent evidence citation.
- Tests: own trace success, cross-user/cross-tenant denial, redaction of secrets/provider details, missing trace safe error, read audit trace, UI link behavior.

## Deferred or optional capability candidates

The following are not required for v0 unless a later queue task explicitly adds them with lifecycle and validation scope:

- `my_account.start_personal_audit_digest_task`: optional Akka `AutonomousAgent` task for a durable personal access/audit digest with progress snapshots, cancellation/failure, notification-to-attention behavior, and result surfaces.
- `my_account.cancel_personal_audit_digest_task` / `my_account.view_personal_audit_digest_task`: companion autonomous task control/read capabilities if the digest task is introduced.

If added, these must be modeled as autonomous task capabilities with tenant/customer-scoped task ids, backend authorization for start/read/cancel/notifications, `ToolPermissionBoundary`, provider fail-closed behavior, task lifecycle traces, and UI progress/result/blocked surfaces.
