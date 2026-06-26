# Tenant admin human

workerId: tenant-admin-human
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: compile-ready

## Purpose

The tenant admin uses Audit/Trace browser surfaces to answer “who did what?” for their selected tenant/Organization by searching immutable activity records, opening authorized full-payload trace detail, following linked tool-call/parent traces, and configuring tenant audit-retention settings.

## Responsibility

- Owns/does:
  - Search tenant-scoped deterministic metadata/summary fields.
  - Inspect authorized full-payload detail with the sensitive tenant-admin warning.
  - Follow authorized parent/child tool-call links.
  - Configure tenant retention within the 30–365 day policy range.
- Does not own/do:
  - Edit or delete audit records, export/compliance bundles, add investigation notes, acknowledge suspicious activity, ask agents to reveal payloads, search full payload text, or inspect cross-tenant/hidden traces.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../surfaces/surfaces.md`
  - type: human-operating-brief
  - version/governance state: current app-description
  - summary: use browser surfaces for scoped search/detail/retention work; treat full payloads as sensitive tenant-admin evidence.
- Skills:
  - activity-log filtering, trace-detail review, linked tool-call navigation, retention-setting review, safe denial interpretation.
- Tools:
  - `search-audit-traces`, `read-trace-detail`, `read-trace-tool-call-detail`, `read-audit-retention-setting`, and `update-audit-retention-setting` via `surface_action`/`api_call` only.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, `../../../../../global/policies/foundation-security-and-governance.md`.
- Evidence profile:
  - allowed: tenant-scoped rows, authorized full payloads in detail, denial reason/policy reference for authorized tenant traces, retention configuration facts.
  - forbidden/redacted: cross-tenant data, hidden trace existence, secrets/tokens/provider credentials, frontend-secret material, raw implementation internals outside the authorized detail contract.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes, using visible surface context only.
  - workstream assistant / functional agent may interpret human text into tool plans: no for tenant-admin activity-log scope.
  - consequential tools require confirmation: retention save uses normal form validation/submit semantics; no chat-plan execution.

## Authority and scope

- authorityLevel: observe for trace search/detail; execute for tenant retention setting update; no administer authority beyond this tenant retention setting.
- AuthContext scope: selected tenant/Organization, active membership, tenant-admin role/capability; optional customer/account filter remains tenant-bounded.
- Allowed decisions: choose filters, open visible trace/detail links, set retention days between 30 and 365.
- Requires approval when: a future policy adds approval; not currently modeled for tenant-admin activity-log scope.
- Denied/hidden behavior: hidden/cross-tenant/expired/malformed trace refs return safe `not_found_or_redacted` or forbidden feedback without enumeration.
- Retained human authority: the tenant admin is responsible for retention changes and sensitive full-payload viewing through browser surfaces.

## Supervision and handoffs

- Supervising human workers: none within this scope.
- Supports: tenant-admin activity investigation within the selected tenant.
- Handoffs to: none for export/notes/acknowledgements/summaries because those are out of scope.
- Escalates to: safe system-message/denial surfaces when access or validation fails.
- Fallback worker or process: Audit/Trace system worker returns validation, forbidden, stale, retention-expired, or not-found/redacted states.

## Inputs, evidence, and outputs

- Inputs/triggers: activity-log filters/search, row/detail/link openings, retention settings form submissions.
- Evidence allowed: scoped rows, authorized detail/full payloads, linked parent/child traces, retention setting values, correlation/session ids.
- Evidence forbidden: payload keyword index results, hidden trace ids/counts, cross-tenant records, secrets/tokens/provider credentials.
- Outputs produced: search requests, detail/read requests, retention update submissions.
- Result/progress/failure surfaces: `surface-audit-trace-activity-log`, `surface-audit-trace-detail`, `surface-audit-trace-retention-settings`, `surface-audit-trace-system-message`.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Audit/Trace structured surfaces | surface_action | browser workstream shell | surface_action | Backend authorizes every search/detail/retention action; UI visibility is advisory. |
| Protected HTTP/workstream APIs | api_call | browser API client | api_call | Selected tenant and role/capability are resolved server-side. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| search-audit-traces | audit-and-trace-investigation | surface_action, api_call | observe | none | read-only query; emits search evidence |
| read-trace-detail | audit-and-trace-investigation | surface_action, api_call | observe sensitive detail | sensitive warning required | read-only scoped detail; emits detail-view evidence |
| read-trace-tool-call-detail | audit-and-trace-investigation | surface_action, api_call | observe sensitive linked detail | sensitive warning required | read-only scoped linked detail |
| read-audit-retention-setting | audit-and-trace-investigation | surface_action, api_call | observe | none | read-only scoped setting |
| update-audit-retention-setting | audit-and-trace-investigation | surface_action, api_call | execute tenant setting update | form submit; future policy may add approval | one retention update/no-op per idempotency/correlation context |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: every query/read/update is selected-tenant scoped.
- Redaction and sensitive data: rows/search indexes omit full payloads and secrets; detail payloads still strip secrets/tokens/provider credentials/frontend-secret material.
- Tool-boundary or role/capability constraints: no `agent_tool_call` or `human_chat_tool_plan` authority in this scope.
- Provider/configuration preconditions for model-backed workers: not needed for browser scope; model-backed assistant is optional and cannot retrieve evidence.
- Idempotency/replay/stale handling: retention update validates 30–365 days and no-ops same-value/idempotent submissions; expired refs return safe retention/redaction states.
- Failure behavior: typed validation/forbidden/not-found/redacted/system-message states with trace refs where safe.
- Denial behavior: no protected-data leakage, hidden count/id/target omission, denial evidence emitted.

## Audit and work traces

Record worker id/type, actor adapter, selected tenant/AuthContext, tenant-admin identity, governed tool/capability id, filters or safe trace handle, correlation/session id, authorization decision, sensitive-detail flag, retention old/new value where applicable, validation/denial reason, idempotency/no-op outcome, and result surface.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: authorized tenant-admin search/detail/tool-call detail/retention read-update.
  - denied/forbidden path: non-tenant-admin, disabled/inactive, cross-tenant, hidden/expired/malformed refs.
  - tenant isolation: tenant A cannot discover tenant B rows/detail.
  - idempotency/replay/stale behavior: repeated retention update and expired trace refs.
  - approval/confirmation behavior: no chat-plan path; retention save uses form submit semantics.
  - trace/audit evidence: search/read/update/denial traces.
- Manual runtime scenario:
  - tenant admin → browser surface action → governed tool → audit-and-trace-investigation → protected API/UI path → trace/view evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
