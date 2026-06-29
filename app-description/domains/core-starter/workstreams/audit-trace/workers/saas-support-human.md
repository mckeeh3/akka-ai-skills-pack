# SaaS support human

workerId: saas-support-human
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: description-ready

## Purpose

The SaaS support human uses Audit/Trace to investigate platform/support incidents, trace gaps, provider/config fail-closed events, and tenant issues only under platform support authority or an active support-access grant recorded in `AuthContext`.

## Responsibility

- Owns/does:
  - Search and inspect support-scoped trace summaries and timelines.
  - Review support-access grants, usage, denials, expiry, and related trace reads.
  - Investigate trace gaps and provider/config/runtime-loader failures without exposing secrets.
  - Request redacted support evidence exports where policy permits.
- Does not own/do:
  - Access tenant payloads without support-access scope, approve their own support access, bypass redaction, inspect unrelated tenants, export raw sensitive payloads by default, or use agent/chat plans to widen authority.

## Authority and scope

- authorityLevel: observe/investigate within support-access/platform support scope; request redacted export where granted.
- AuthContext scope: active support-access grant for tenant/customer/workstream or platform support scope; all support access is time-bounded and audit-traced.
- Allowed decisions: choose support-scoped filters, open authorized support-access review/timeline/detail, request redacted support export, escalate missing evidence/trace-gap findings.
- Requires approval when: support-access grant/extension, sensitive detail, or export policy requires approval.
- Denied/hidden behavior: expired/missing support access, tenant mismatch, hidden trace refs, or unsupported sensitive export return safe denial/redacted surfaces.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Audit/Trace structured surfaces | `surface_action` | support browser workstream shell | `surface_action` | Backend enforces support-access scope and redaction. |
| Protected HTTP/workstream APIs | `api_call` | browser/API client | `api_call` | Selected support scope resolved server-side. |
| Audit/Trace assistant chat | `human_chat_tool_plan` | composer confirmation | `human_chat_tool_plan` | Read-only support investigations require confirmation and support scope. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe support-scoped summaries | chat confirmation if via assistant | read-only |
| `search-work-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe support-scoped work traces | chat confirmation if via assistant | read-only |
| `read-audit-trace-detail` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe redacted/support detail | sensitive detail approval where required | read-only |
| `lookup-trace-correlation` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe support timeline/correlation | confirmation if via assistant | read-only |
| `investigate-denied-trace-access` | `audit-and-trace-investigation` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan` | observe denial evidence | confirmation if via assistant | read-only |
| `review-support-access-traces` | `audit-and-trace-investigation` | `surface_action`, `api_call` | observe support-access evidence | cannot self-approve support access | read-only review trace emitted |
| `request-redacted-trace-export` | `audit-and-trace-investigation` | `surface_action`, `api_call` | request redacted support export | approval gate where policy requires | idempotent export request/result workflow |

## Policies, constraints, and fail-closed behavior

- Support access is least-privilege, time-bounded, scoped, and reviewable by authorized tenant/SaaS administrators.
- Support views redact tenant payloads by default and never expose provider secrets, bearer/session tokens, hidden cross-tenant identifiers, or frontend-secret material.
- Missing/expired support access and unapproved sensitive/raw export fail closed and emit denial traces.
- Agent/chat plans cannot widen support scope.

## Audit and work traces

Record support actor identity, support-access grant id/scope/expiry, selected tenant/customer/workstream, actor adapter, governed tool/capability id, redaction class, approval/export refs, authorization decision, trace-gap/provider/config failure refs, correlation id, result surface, and denial reason where applicable.

## Tests and manual runtime scenarios

- Active support-access grant permits redacted tenant investigation and emits support-access use trace.
- Missing/expired support access denies search/detail/export without hidden target enumeration.
- Support operator cannot approve own support access or raw export.
- Trace-gap/provider fail-closed investigation surfaces omit secrets while preserving diagnosability.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
