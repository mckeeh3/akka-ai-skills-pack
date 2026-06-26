# My Account system worker

workerId: my-account-system-worker
workerType: system
reasoningEngine: deterministic
scope: local-workstream
owningDomain: core-starter
owningWorkstream: my-account
runtimeReadiness: compile-ready

## Purpose

The My Account system worker represents deterministic backend/API/projection/workflow participants that resolve selected context, render backend-owned surfaces, aggregate personal attention, manage in-app notification state, route authorized openings, and orchestrate personal digest lifecycle plumbing.

## Responsibility

- Owns/does:
  - Resolve `/api/me` and selected `AuthContext` from authenticated backend state.
  - Produce browser-safe dashboard/profile/settings/context/notification/digest/open-denied payloads.
  - Reauthorize every workstream/source/trace open and every personal action.
  - Apply idempotency, stale/conflict handling, no-enumeration denials, and audit/work trace emission.
  - Run deterministic workflow/task/projection/timer/consumer support for notifications and digest lifecycle where selected by realization.
- Does not own/do:
  - Reason with a model, fabricate model-backed digest success, grant authority from frontend state, expose hidden data, or execute unsupported provider/external-channel operations.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../behavior.md`, `../tools/governed-tools.md`, and realization files.
  - type: deterministic-instruction
  - version/governance state: current app-description
  - summary: enforce backend authorization, scoped DTOs, traceability, idempotency, and fail-closed provider/runtime behavior.
- Skills:
  - deterministic context resolution, scoped surface assembly, notification lifecycle state handling, digest task orchestration, source-opening reauthorization, audit/trace emission.
- Tools:
  - protected internal/API/workflow/timer/consumer adapters for My Account governed tools.
- Policies/rubrics/examples:
  - backend-authorization-default-deny, tenant-customer-isolation, frontend-secret-boundary, redaction/export governance.
- Evidence profile:
  - allowed: protected state needed to produce scoped redacted DTOs and audit traces.
  - forbidden/redacted: raw credentials/secrets/provider records to browser/agent, hidden facts in denial messages.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: not applicable.
  - workstream assistant / functional agent may interpret human text into tool plans: not applicable.
  - consequential tools require confirmation: enforce when policy/surface/chat-plan requires it.

## Authority and scope

- authorityLevel: execute deterministic backend operations under authenticated/service authority; no product-admin discretion.
- AuthContext scope: authenticated account, selected context, service provenance, and stored task/projection authority where applicable.
- Allowed decisions: authorize/deny based on policy, no-op/replay/stale/conflict classification, provider/runtime blocked classification, target reauthorization.
- Requires approval when: policy, confirmation surface, chat-plan confirmation, digest review, or provider/export rule requires it.
- Denied/hidden behavior: produce safe forbidden/not-found-redacted/system-message surfaces and traces without enumeration.
- Retained human authority: humans submit/confirm consequential self-service actions; model-backed digest work must be human reviewed where declared.

## Supervision and handoffs

- Supervising human workers: none directly; behavior is governed by app policies and tests.
- Supports: `signed-in-member-human`, `my-account-functional-agent-worker`, workstream shell.
- Handoffs to: source workstreams through reauthorized open actions; Audit/Trace through visible trace refs; admin/support guidance only when configured.
- Escalates to: blocked/provider/runtime/system-message surfaces.
- Fallback worker or process: safe no-access/no-selected-context/open-denied/provider-blocked surfaces.

## Inputs, evidence, and outputs

- Inputs/triggers: JWT/auth session, `/api/me`, protected workstream API calls, surface action submissions, confirmed chat-plan execution requests, workflow/timer/consumer events, digest task transitions.
- Evidence allowed: backend identity/membership/capability state, personal notification state, visible attention projections, digest task state, audit/work trace state.
- Evidence forbidden: browser/agent exposure of secrets, hidden targets, raw provider/model/prompt/tool payloads, unsupported external-channel/provider state.
- Outputs produced: scoped surface envelopes, events, traces, no-op/conflict/denial/system-message results, digest progress/result/blocked state.
- Result/progress/failure surfaces: all My Account surfaces in `../surfaces/surfaces.md`.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Protected HTTP/workstream endpoint | api_call | browser/API | api_call | Resolves AuthContext server-side and returns scoped DTOs. |
| Internal services/components | internal_call | backend | internal_call | Applies invariants, idempotency, and audit. |
| Workflow/durable task runtime | workflow_step | backend workflow/task | workflow_step | Digest progress/review/cancel flow where selected. |
| Timer/timed action | timer_invocation | scheduled backend | timer_invocation | Snooze expiry, stale refresh, reminder, or digest follow-up where selected. |
| Consumer/projection | consumer_reaction | event/topic/entity stream | consumer_reaction | Attention/notification projection updates with provenance. |
| Confirmed chat plan dispatcher | human_chat_tool_plan | backend dispatcher | human_chat_tool_plan | Executes only exact confirmed, reauthorized catalog steps. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| read-current-account-context | account-context-and-profile | api_call, internal_call | observe/execute read | none | scoped read projection |
| my_account.update_profile_settings / update-own-profile-settings | account-context-and-profile | api_call, internal_call, human_chat_tool_plan | execute self-service mutation | surface submit or exact chat-plan confirmation | one self-account update transaction |
| notification.* personal tools | account-context-and-profile | api_call, internal_call, timer_invocation, consumer_reaction, human_chat_tool_plan where cataloged | execute personal notification state | surface submit/chat confirmation/policy | one visible notification/preference transaction or idempotent no-op |
| request-personal-digest-export | account-context-and-profile | api_call, workflow_step, internal_call, timer_invocation | execute digest task lifecycle | policy/surface review gates | one durable digest task/action transaction |
| my_account.open_authorized_workstream / attention.open_attention_item / my_account.view_own_trace_refs | account-context-and-profile | api_call, internal_call | observe/open | backend authorization | reauthorize target every time; no source mutation |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: all state access is selected-context or service-provenance scoped.
- Redaction and sensitive data: output DTOs are browser/agent safe; raw evidence remains support/audit-gated.
- Tool-boundary or role/capability constraints: backend checks are authoritative for every adapter.
- Provider/configuration preconditions for model-backed workers: digest/model/agent work fails closed; deterministic recovery may still render blocked state.
- Idempotency/replay/stale handling: side effects use idempotency keys; duplicate/stale events are safe no-ops or conflict states.
- Failure behavior: typed system/progress/blocked surfaces plus durable trace evidence.
- Denial behavior: no-enumeration, safe categories, no protected existence leakage.

## Audit and work traces

Record worker id/type, adapter/source, service or account identity, selected context, provenance/correlation/causation ids, governed tool/capability id, policy decision, idempotency key or redacted hash, state transition/no-op/conflict/denial, redaction level, result surface, and runtime/provider readiness decision.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: `/api/me`, dashboard, profile/settings, notification, digest, open-source flows.
  - denied/forbidden path: hidden/cross-tenant/disabled/no-membership/provider-blocked cases.
  - tenant isolation: all endpoints/projections scoped by backend-selected context.
  - idempotency/replay/stale behavior: duplicate notification/digest/settings/chat-plan actions.
  - approval/confirmation behavior: confirmed chat-plan dispatcher and surface confirmations.
  - trace/audit evidence: API/internal/workflow/timer/consumer trace source coverage.
- Manual runtime scenario:
  - API/surface trigger → system worker adapter → governed tool → account-context-and-profile → Akka implementation → typed surface/event/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/account-context-and-profile.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
