# Tenant admin human

workerId: tenant-admin-human
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: description-ready

## Purpose

The tenant admin uses Audit/Trace browser surfaces and confirmed read-only chat plans to investigate tenant-scoped audit/work traces, denials, correlations, support-access events, runtime-validation evidence, and redacted export requests for the selected tenant/Organization.

## Responsibility

- Owns/does:
  - Review dashboard attention for investigations, denials, trace gaps, support-access reviews, and runtime-validation evidence.
  - Search tenant-scoped trace metadata and summaries.
  - Inspect authorized detail and timelines with progressive disclosure/redaction.
  - Investigate authorization denials and support-access use involving the tenant.
  - Confirm read-only chat plans for search/correlation/summary when desired.
  - Request redacted tenant exports where policy allows.
- Does not own/do:
  - Edit/delete audit records, approve support access for themselves, access cross-tenant traces, inspect hidden records, bypass redaction, perform raw/sensitive export without approval, or ask agents to reveal payloads outside grants.

## Authority and scope

- authorityLevel: observe/investigate; request redacted export where granted; no mutation of trace facts.
- AuthContext scope: selected tenant/Organization, active membership, tenant-admin role/capability; optional customer/account filter remains tenant-bounded.
- Allowed decisions: choose filters, open authorized traces/timelines/denial/support-access review, confirm read-only plans, request export, acknowledge review state where future policy grants it.
- Requires approval when: export/support-access/sensitive detail policy requires it.
- Denied/hidden behavior: safe `forbidden`, `not_found_or_redacted`, `approval_required`, or redacted result surface without hidden target enumeration.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Audit/Trace structured surfaces | `surface_action` | browser workstream shell | `surface_action` | Backend authorizes every search/read/correlation/summary/export/support-access action. |
| Protected HTTP/workstream APIs | `api_call` | browser API client | `api_call` | Selected tenant and role/capability resolved server-side. |
| Audit/Trace assistant chat | `human_chat_tool_plan` | composer confirmation | `human_chat_tool_plan` | Read-only plans require explicit confirmation; export/support access returns approval-required when needed. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe tenant summaries | chat confirmation if via assistant | read-only; search trace emitted |
| `search-work-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe tenant work traces | chat confirmation if via assistant | read-only |
| `read-audit-trace-detail` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe authorized detail | sensitive warning/progressive disclosure | read-only detail trace emitted |
| `read-work-trace-detail` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe work-trace detail | sensitive warning/progressive disclosure | read-only |
| `lookup-trace-correlation` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe timeline/correlation | confirmation if via assistant | read-only |
| `investigate-denied-trace-access` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe denial evidence | confirmation if via assistant | read-only |
| `summarize-investigation-evidence` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | summarize authorized evidence | confirmation; cites evidence refs | summary result trace emitted |
| `request-redacted-trace-export` | `audit-and-trace-investigation` | `surface_action`, `api_call` | request redacted export | approval gate where policy requires | idempotent request/result workflow |
| `review-support-access-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call` | observe tenant support access evidence | none unless action escalates | read-only review trace emitted |

## Policies, constraints, and fail-closed behavior

- Tenant isolation and selected `AuthContext` are backend enforced.
- Detail/timeline surfaces use redacted/default views; sensitive fields require explicit grant.
- Hidden, expired, malformed, or cross-scope refs return `not_found_or_redacted`/forbidden.
- Repeated reads are idempotent; repeated export requests use idempotency/correlation keys and return existing request state.
- Full-payload keyword search and secret/provider token exposure are forbidden.

## Audit and work traces

Record worker id/type, actor adapter, selected tenant/AuthContext, actor identity, governed tool/capability id, filters/safe handles, support-access/export scope, confirmation id where applicable, correlation/session/work trace id, authorization decision, redaction class, validation/denial reason, idempotency/no-op outcome, and result surface.

## Tests and manual runtime scenarios

- Tenant admin opens dashboard/search/detail/timeline/denial/support-access surfaces with tenant-scoped results.
- Tenant admin confirms a read-only chat plan and receives a redacted result surface.
- Tenant admin requests cross-tenant detail/export and receives safe denial with trace evidence.
- Tenant admin reviews support-access usage for their tenant and sees grant/use/expiry evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
