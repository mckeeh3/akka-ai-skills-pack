# Audit/Trace system worker

workerId: audit-trace-system-worker
workerType: system
reasoningEngine: deterministic
scope: local-workstream
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: compile-ready

## Purpose

The Audit/Trace system worker represents deterministic backend/API/projection/retention participants that record immutable audit traces, execute authorized tenant-admin search/detail/retention operations, enforce retention expiry, and emit trace evidence for reads, denials, validation failures, and updates.

## Responsibility

- Owns/does:
  - Persist human request/response, agent request/response, tool-call, denial, search/detail-view, retention-view/update, and retention-expiry trace events.
  - Execute backend-authorized searches over deterministic metadata/summary fields only.
  - Return authorized full-payload detail with the sensitive tenant-admin warning and secret redaction.
  - Validate and update retention settings, including idempotent no-op handling.
  - Enforce tenant scope, no-enumeration denials, and retention expiry.
- Does not own/do:
  - Let frontend state authorize trace access, index full payload keyword text, expose secrets/tokens/provider credentials, manually edit/delete audit records, or run export/notes/acknowledgement/AI-summary features in this scope.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../behavior.md`, `../tools/governed-tools.md`, and realization files.
  - type: deterministic-instruction
  - version/governance state: current app-description
  - summary: enforce backend authorization, immutable trace semantics, scoped search/detail DTOs, retention policy, and traceability.
- Skills:
  - trace ingestion, scoped search, detail read, tool-call linkage, retention settings validation/update, retention expiry, denial evidence.
- Tools:
  - protected API/internal/consumer/timer adapters for Audit/Trace governed tools and audit trace ingestion/expiry internals.
- Policies/rubrics/examples:
  - backend-authorization-default-deny, tenant-customer-isolation, frontend-secret-boundary, redaction/export governance.
- Evidence profile:
  - allowed: protected trace store, authorized scoped DTOs, redacted payload detail, retention configuration state.
  - forbidden/redacted: secrets/tokens/provider credentials/frontend-secret material, cross-tenant records, hidden existence in denials.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: not applicable.
  - workstream assistant / functional agent may interpret human text into tool plans: not applicable.
  - consequential tools require confirmation: enforce validation/idempotency for retention updates; no chat-plan path.

## Authority and scope

- authorityLevel: execute deterministic backend operations under authenticated/service authority; no discretionary audit-admin authority beyond policy.
- AuthContext scope: selected tenant/Organization, optional customer/account filter inside tenant, service provenance for ingestion/expiry.
- Allowed decisions: authorize/deny, validate filters/retention values, classify retention-expired/not-found/redacted, no-op duplicate settings submissions, redact secrets.
- Requires approval when: a future policy adds approval; not currently modeled.
- Denied/hidden behavior: produce safe forbidden/not-found-redacted/validation surfaces and denial traces without protected existence leakage.
- Retained human authority: tenant admin chooses search/detail/retention actions; system enforces policy and emits evidence.

## Supervision and handoffs

- Supervising human workers: none directly; behavior is governed by app policies and tests.
- Supports: `tenant-admin-human`, `audit-trace-functional-agent-worker`, workstream shell, and other workstreams that emit trace records.
- Handoffs to: none for export/notes/acknowledgements/summaries because those are out of scope.
- Escalates to: safe system-message/denial surfaces and operational diagnostics where applicable.
- Fallback worker or process: validation/forbidden/not-found/redacted/retention-expired states.

## Inputs, evidence, and outputs

- Inputs/triggers: audit event ingestion from human/agent/tool/system actions, tenant-admin API calls, retention setting submissions, scheduled retention expiry.
- Evidence allowed: tenant-scoped trace records, metadata/summary indexes, authorized full-payload detail, retention state.
- Evidence forbidden: cross-tenant records, hidden trace existence in denials, full payload keyword indexes, raw secrets/tokens/provider credentials to browser/agent.
- Outputs produced: trace records, search results, detail DTOs, retention setting DTOs, retention update/no-op results, denial/validation/system messages, retention-expiry evidence.
- Result/progress/failure surfaces: `../surfaces/surfaces.md` inventory.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Protected HTTP/workstream endpoint | api_call | browser/API | api_call | Resolves selected tenant/AuthContext server-side. |
| Internal audit trace service/store | internal_call | backend | internal_call | Records immutable events and reads scoped DTOs. |
| Consumer/projection | consumer_reaction | event/entity/topic stream | consumer_reaction | Captures action/tool/denial events from source workstreams where selected. |
| Timer/timed action | timer_invocation | scheduled backend | timer_invocation | Applies retention expiry according to tenant setting. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| search-audit-traces | audit-and-trace-investigation | api_call, internal_call | observe | none | scoped read query; search trace emitted |
| read-trace-detail | audit-and-trace-investigation | api_call, internal_call | observe sensitive detail | sensitive warning required | scoped read; detail-view trace emitted |
| read-trace-tool-call-detail | audit-and-trace-investigation | api_call, internal_call | observe sensitive linked detail | sensitive warning required | scoped read; linked-detail trace emitted |
| read-audit-retention-setting | audit-and-trace-investigation | api_call, internal_call | observe | none | scoped read; view trace emitted |
| update-audit-retention-setting | audit-and-trace-investigation | api_call, internal_call | execute retention setting | validation/submit; no chat plan | one tenant setting transaction or idempotent no-op |
| audit trace ingestion/retention expiry internals | audit-and-trace-investigation | internal_call, consumer_reaction, timer_invocation | execute system bookkeeping | service provenance/policy | append immutable record or expire by retention policy |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: all queries/details/settings are tenant-scoped; optional customer/account filters never cross tenant.
- Redaction and sensitive data: full payload detail is authorized and warning-gated, but secrets/tokens/provider credentials/frontend-secret material are still absent.
- Tool-boundary or role/capability constraints: no agent/chat tools for evidence retrieval or mutation in this scope.
- Provider/configuration preconditions for model-backed workers: not required; no AI summary/search feature in this scope.
- Idempotency/replay/stale handling: retention update idempotency/no-op; repeated searches/details are read-only; expired refs return safe retention/redaction outcome.
- Failure behavior: validation/forbidden/not-found/redacted/stale/system-message states plus trace evidence.
- Denial behavior: no hidden target enumeration, no protected data leakage, safe reason categories.

## Audit and work traces

Record worker id/type, adapter/source, tenant/admin/service identity, selected context, filters/safe handles, correlation/causation/session ids, governed tool/capability id, authorization decision, full-payload-detail flag, sensitive-warning flag, status/error, retention old/new values, validation/denial reason, redaction decisions, idempotency/no-op outcome, and result surface.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: authorized search/detail/tool-call detail/retention view-update.
  - denied/forbidden path: unauthorized roles, disabled/inactive, cross-tenant, hidden/expired/malformed refs.
  - tenant isolation: storage/query/detail isolation.
  - idempotency/replay/stale behavior: retention no-op and retention-expired refs.
  - approval/confirmation behavior: no chat-plan path; retention form validation.
  - trace/audit evidence: ingestion, search/read/update/expiry/denial traces.
- Manual runtime scenario:
  - API/surface trigger or source event → system worker adapter → governed tool/internal trace operation → audit-and-trace-investigation → typed surface/event/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
