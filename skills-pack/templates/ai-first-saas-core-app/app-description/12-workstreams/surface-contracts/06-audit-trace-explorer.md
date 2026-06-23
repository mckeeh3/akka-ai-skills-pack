# Surface Contract: Audit Trace Explorer

- surface-id: `audit-trace-explorer`
- type/version: search/timeline/v1
- owner functional agent: `audit-trace-agent` (Audit/Trace)
- reusable by: User Admin, Agent Admin, Governance/Policy, My Account, and domain-specific workstreams for scoped evidence drill-ins.

## Placement and graph role

This surface is the investigation and evidence graph node for audit events, work traces, authorization decisions, prompt/skill/reference loads, tool calls, decisions, denials, and side effects. It may open from trace links, decision cards, dashboard anomaly cards, or prompt requests. Its default UX is an investigation summary for the authorized actor; raw event/tool/policy/correlation diagnostics are role-gated drilldowns, not ordinary user-facing content.


## User-visible/internal metadata boundary

Default rendering must use SaaS product language and show only information the current actor needs to decide, act, recover, or understand the business outcome. Internal ids, raw trace/event/correlation data, governed-tool/capability ids, backend component names, prompt/provider/model details, and policy implementation references are implementation metadata. Expose them only in authorized admin, support, auditor, or developer drilldowns, and keep them visually subordinate to user-meaningful labels.

## Payload summary

Payload must include:

- selected scope label, trace query/filter state, redaction profile, freshness marker, and user-readable investigation summary;
- query results or selected timeline as business/audit events with actor label, time, user-safe action/outcome label, data-access summary, evidence summary, and redaction/omission markers;
- role-gated diagnostics: selected `AuthContext`, `correlationId`, trace ids, event ids/types, actor/service/agent/workflow ids, source surface/action/capability ids, authorization basis, policy refs, tool/model usage summaries, and raw evidence links;
- pagination/sort/filter metadata;
- export and evidence-open action descriptors with capability ids, governed-tool ids, browser-tool ids, and denial categories for implementation mapping, translated to user-safe labels in the default UI.

## Compact payload schema

```ts
type AuditTraceExplorerData = {
  scopeLabel: string;
  query: { text?: string; from?: string; to?: string; redactionProfile: string };
  results: Array<{ displayId: string; actorLabel: string; actionLabel: string; occurredAt: string; outcome: string; evidenceSummary?: string; omittedFieldKeys: string[] }>;
  selectedTimeline?: Array<{ label: string; occurredAt?: string; evidenceSummary?: string; redactionMarkers: string[] }>;
  diagnostics?: {
    authContext: SurfaceAuthContext;
    correlationId?: string;
    traceIds?: string[];
    eventTypes?: string[];
    rawTimelineRefs?: Array<{ eventId: string; sequence?: number; sourceSurfaceId?: string; sourceActionId?: string; capabilityId?: string; evidenceRefs: string[] }>;
  };
  pagination: { pageToken?: string; nextPageToken?: string; pageSize: number; sort: string };
};
```

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Search traces | `audit.traces.search` | browser-tool, agent-tool | update `audit-trace-explorer` |
| Open trace detail | `audit.traces.view` | browser-tool, agent-tool | timeline/detail variant |
| Filter by correlation id | `audit.traces.search` | browser-tool surface-request | update search results |
| Open source surface | `workstream.surface.open` | browser-tool surface-request | source surface or denial |
| Export scoped traces | `audit.traces.export` | browser-tool | export status/approval `system_message` |
| Escalate anomaly | `audit.anomalies.escalate` | browser-tool | decision/exception card |

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `audit.search-traces` | `audit-trace.search` | `audit.traces.search` | `governance-decisions-audit` | browser-tool, agent-tool | `audit-trace-explorer` | query fingerprint | true |
| `audit.open-trace-detail` | `audit-trace.detail.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool, agent-tool | `audit-trace-explorer` | trace id | true |
| `audit.filter-by-correlation` | `audit-trace.correlation.filter` | `audit.traces.search` | `governance-decisions-audit` | browser-tool, surface-request | `audit-trace-explorer` | correlation id | true |
| `audit.open-source-surface` | `audit-trace.source-surface.open` | `workstream.surface.open` | `frontend-shell-integration-patterns` | browser-tool, surface-request | source surface or `system_message` | source surface id + trace id | true |
| `audit.export-scoped-traces` | `audit-trace.export` | `audit.traces.export` | `governance-decisions-audit` | browser-tool | deferred `trace-export-status`, `decision-card`, or `system_message` | export request id | true |
| `audit.escalate-anomaly` | `audit-trace.anomaly.escalate` | `audit.anomalies.escalate` | `governance-decisions-audit` | browser-tool | `decision-card` or `system_message` | anomaly id + request id | true |



Action mappings must preserve the shared tool-use contract: `governedToolId`, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), `confirmationRequired`, `approvalPolicy`, idempotency key, transaction boundary, result/partial-failure behavior, `traceSource`, and `traceRequired`. If this surface exposes only the browser-tool adapter, state `surface_action` and keep any chat/agent adapter in the workstream tool catalog instead of duplicating business semantics.

## UI states

- `loading`: preserve submitted filters and show timeline skeletons.
- `empty`: no visible traces for this query/scope; do not imply global absence.
- `error`: safe error category and a user-readable support/reference label; raw `correlationId` appears only in authorized diagnostic detail.
- `forbidden`: no matched count or hidden trace existence leakage.
- `partial-data`: show omitted-field markers and role-specific redaction.
- `stale`: mark results stale after reconnect or projection lag.

## Auth/security

- Audit reads enforce tenant/customer/support/auditor boundaries before result counts.
- Sensitive payloads, prompts, prompt internals, provider/model details, provider secrets, tokens, raw ids not needed by the actor, and cross-tenant facts are redacted or omitted.
- Export requires explicit capability and may require decision-card approval.
- Reading sensitive traces may itself create an audit event.

## Rendering and capability tests

- Organization Admin, auditor, support-access, ordinary authorized user, and forbidden variants show correct redactions, summary/detail split, and actions.
- Search, detail, export, source-surface, and escalation actions carry capability/governed-tool/browser-tool ids for implementation/tests, while rendering user-safe labels by default.
- Trace links from all SaaS Foundation App surfaces open through shell request routing.
- Malformed/missing trace ids produce safe system-message surfaces.
- Sensitive-read, export denial, and investigation actions produce audit/work traces.
