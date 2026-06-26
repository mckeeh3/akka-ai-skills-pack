# Governance/Policy human operators

workerId: governance-policy-human-operators
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

Authorized SaaS owner admins, tenant admins, auditors, and scoped support users inspect and manage simple governance policies through structured Governance/Policy surfaces.

## Responsibility

- Owns/does:
  - SaaS owner admins set SaaS default boolean/counter policy values in defaults context.
  - Tenant admins set or reset tenant business-governance overrides in authorized tenant/customer/account scope.
  - Auditors and scoped support users inspect authorized effective values, history, and runtime policy-decision evidence.
  - All write actors provide reason text, review visible consequences, and remain accountable for confirmed submissions or confirmed chat plans.
- Does not own/do:
  - Override tenant isolation, backend authorization, secret protection, redaction, audit integrity, or platform integrity controls.
  - Create complex policy scripts, legal compliance workflows, autonomous policy commits, or enterprise delegation models.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../surfaces/surfaces.md`
  - type: human-operating-brief
  - version/governance state: current app-description
  - summary: operate only inside backend-selected `AuthContext`; use visible surfaces, validation, denials, and trace links to make scoped policy changes.
- Skills:
  - simple policy search, effective-value interpretation, default/override/reset review, reason writing, history review, safe denial recovery.
- Tools:
  - `governance.policy.list` and `governance.policy.read_effective` via `surface_action` and `api_call`.
  - `governance.policy.set_default`, `governance.policy.set_override`, and `governance.policy.reset_override` via `surface_action`, `api_call`, and bounded confirmed `human_chat_tool_plan` when explicitly offered.
  - `governance.policy.read_history` via `surface_action` and `api_call`.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, `../../../../../global/policies/foundation-security-and-governance.md`.
- Evidence profile:
  - allowed: browser-safe policy definitions, visible scopes, default/override/effective values, history summaries, runtime-decision trace links, and redacted actor/change summaries for the selected context.
  - forbidden/redacted: hidden tenant/customer/account facts, raw secrets/JWT/provider keys, raw prompts/model payloads, raw tool payloads, raw correlation/idempotency internals, and cross-tenant evidence.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, only for cataloged confirmed command plans after deterministic no-mutation routing declines or cannot satisfy the request.
  - consequential tools require confirmation: yes for chat-plan execution and for any surface policy that requires explicit confirmation.

## Authority and scope

- authorityLevel: SaaS owner admins administer defaults; tenant admins execute tenant overrides/resets; auditors and scoped support observe.
- AuthContext scope: backend-selected SaaS-owner/defaults context or tenant/customer/account/member/role/capability context.
- Allowed decisions: scoped default updates, scoped override set/reset, scoped history/effective-policy inspection.
- Requires approval when: the surface/chat-plan policy requires confirmation, an exact chat plan is executed, or a future policy marks the change as approval-gated.
- Denied/hidden behavior: hidden targets are omitted or return safe `system_message` feedback without enumeration.
- Retained human authority: humans remain accountable for all policy writes and exact chat-plan confirmations.

## Supervision and handoffs

- Supervising human workers: tenant organization governance owners and SaaS owner admins according to role scope.
- Supports: Governance/Policy functional agent worker by providing explicit confirmation and reason text when needed.
- Handoffs to: Audit/Trace for authorized trace investigation links; Agent Admin or User Admin only through backend-authorized deep links.
- Escalates to: support or SaaS owner admin guidance when visible policy scope is blocked.
- Fallback worker or process: Governance/Policy system worker returns validation, denial, stale, or failure surfaces.

## Inputs, evidence, and outputs

- Inputs/triggers: dashboard/list/detail/edit/history surface actions, deep links, deterministic surface intent routes, exact chat-plan confirmation.
- Evidence allowed: scoped policy catalog rows, effective-value breakdowns, history rows, runtime-decision summaries, redacted trace refs.
- Evidence forbidden: secrets, raw provider/model/prompt/tool payloads, hidden target existence, cross-tenant/customer facts.
- Outputs produced: read requests, default-change submissions, tenant override submissions, reset submissions, history/filter requests, confirmed plan decisions.
- Result/progress/failure surfaces: `../surfaces/surfaces.md` plus `system_message` denial/validation/stale results.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Governance/Policy structured surfaces | surface_action | browser workstream shell | surface_action | Backend authorizes every action; UI visibility is advisory. |
| Confirmed workstream assistant plan | human_chat_tool_plan | selected Governance/Policy assistant | human_chat_tool_plan | No mutation until exact plan snapshot confirmation and per-step reauthorization. |
| Protected workstream APIs | api_call | browser API client | api_call | Backend resolves selected context and returns typed surfaces/results. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.list | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |
| governance.policy.read_effective | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |
| governance.policy.set_default | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | administer defaults | reason plus submit/confirmation | one default update per idempotency key |
| governance.policy.set_override | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | execute tenant override | reason plus submit/confirmation | one override update per idempotency key |
| governance.policy.reset_override | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | execute reset | reason plus submit/confirmation | one reset per idempotency key |
| governance.policy.read_history | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: backend-selected context scopes every query and write.
- Redaction and sensitive data: browser-visible fields are scoped and redacted; hidden facts are omitted.
- Tool-boundary or role/capability constraints: human surface/chat adapters do not grant agent authority and agent tools do not grant human authority.
- Provider/configuration preconditions for model-backed workers: missing model/provider config disables assistant explanation/planning but not deterministic browser paths.
- Idempotency/replay/stale handling: side-effecting commands require idempotency and freshness/version checks where available.
- Failure behavior: validation, forbidden, stale, conflict, and provider-unavailable outcomes return safe `system_message` or typed surface state with trace refs.
- Denial behavior: no hidden scope enumeration or protected-data leakage.

## Audit and work traces

Record worker id/type, actor adapter, selected `AuthContext`, account/member/service identity, requestedBy/confirmedBy for chat plans, governed tool/capability ids, policy id/scope/value type, reason for writes, idempotency/correlation refs, authorization decision, redaction level, result state, and linked surface/workstream item.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: list/read-effective/default/override/reset/history in `../tests/coverage.md`.
  - denied/forbidden path: missing context/capability, cross-tenant/customer, hidden target, hard-platform-security override.
  - tenant isolation: selected context is resolved server-side.
  - idempotency/replay/stale behavior: repeated writes and stale versions do not duplicate history/traces.
  - approval/confirmation behavior: exact chat-plan confirmation and required reason.
  - trace/audit evidence: surface, API, and chat-plan trace refs.
- Manual runtime scenario:
  - authorized human → surface action or confirmed chat plan → governed tool → governance-policy-lifecycle → protected Akka/API/UI path → policy history/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
