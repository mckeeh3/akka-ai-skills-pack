# Surface Contract: Audit Trace Explorer

- surface-id: `audit-trace-explorer`
- type/version: search/timeline/v1
- owner functional agent: `audit-trace-agent` (Audit/Trace)
- reusable by: User Admin, Agent Admin, Governance/Policy, My Account, and domain-specific workstreams for scoped evidence drill-ins.

## Placement and graph role

This surface is the investigation and evidence graph node for audit events, work traces, authorization decisions, prompt/skill/reference loads, tool calls, decisions, denials, and side effects. It may open from trace links, decision cards, dashboard anomaly cards, or prompt requests.

## Payload summary

Payload must include:

- selected `AuthContext`, trace query/filter state, `correlationId`, trace ids, redaction profile, freshness marker;
- query results or selected timeline: event ids, event types, actor/service/agent/workflow ids, source surface/action/capability ids, timestamps, authorization basis, policy refs, data-access summaries, tool/model usage summaries, outcome, and redacted evidence links;
- pagination/sort/filter metadata;
- export and evidence-open action descriptors with capability ids, governed-tool ids, browser-tool ids, and denial categories.

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Search traces | `audit.traces.search` | browser-tool, agent-tool | update `audit-trace-explorer` |
| Open trace detail | `audit.traces.view` | browser-tool, agent-tool | timeline/detail variant |
| Filter by correlation id | `audit.traces.search` | browser-tool surface-request | update search results |
| Open source surface | `workstream.surface.open` | browser-tool surface-request | source surface or denial |
| Export scoped traces | `audit.traces.export` | browser-tool | export status/approval `system_message` |
| Escalate anomaly | `audit.anomalies.escalate` | browser-tool | decision/exception card |

## UI states

- `loading`: preserve submitted filters and show timeline skeletons.
- `empty`: no visible traces for this query/scope; do not imply global absence.
- `error`: safe error category and `correlationId`.
- `forbidden`: no matched count or hidden trace existence leakage.
- `partial-data`: show omitted-field markers and role-specific redaction.
- `stale`: mark results stale after reconnect or projection lag.

## Auth/security

- Audit reads enforce tenant/customer/support/auditor boundaries before result counts.
- Sensitive payloads, prompts, provider secrets, tokens, and cross-tenant facts are redacted or omitted.
- Export requires explicit capability and may require decision-card approval.
- Reading sensitive traces may itself create an audit event.

## Rendering and capability tests

- Tenant Admin, auditor, support-access, and forbidden variants show correct redactions and actions.
- Search, detail, export, source-surface, and escalation actions carry capability/governed-tool/browser-tool ids.
- Trace links from all core app surfaces open through shell request routing.
- Malformed/missing trace ids produce safe system-message surfaces.
- Sensitive-read, export denial, and investigation actions produce audit/work traces.
