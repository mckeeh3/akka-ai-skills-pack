# Workstream Attention Contracts

## Status and scope

This document standardizes actionable attention for generated secure AI-first SaaS workstream applications. Use it with `./workstream-contract.md`, `./requirements-to-workstream-development-process.md`, `./agent-workstream-application-architecture.md`, and `./structured-surface-contracts.md`.

Attention answers the product question:

> What needs this authorized actor's attention in this workstream, right now, under this selected AuthContext?

Attention is not merely a notification, unread chat badge, frontend-only count, analytics metric, or event stream. It is a backend-owned, scoped, authorized, actionable signal that a human or governed participant may need to inspect, decide, approve, correct, retry, escalate, acknowledge, dismiss, or learn from something.

## Contract chain

```text
attention source
→ governed producer with idempotency
→ AttentionItem lifecycle state
→ WorkstreamAttentionSummary projection
→ role-specific dashboard card/list
→ My Account and left rail aggregate counts where authorized
→ surface/action edge such as open_attention_item, acknowledge, resolve, dismiss, retry, approve, escalate
→ capability/governed-tool authorization
→ audit/work trace and tests
```

Frontend-only presentation badges may show transient local state, but they must not be the source of authoritative workstream attention.

## `AttentionItem` shape

```ts
type AttentionItem = {
  attentionItemId: string;
  workstreamId: string;
  owningFunctionalAgentId: string;
  tenantId: string;
  selectedContextId: string;
  customerId?: string;

  category:
    | "approval"
    | "decision"
    | "exception"
    | "policy_conflict"
    | "blocked_work"
    | "overdue_item"
    | "failed_action"
    | "sla_risk"
    | "audit_anomaly"
    | "outcome_drift"
    | "provider_blocked"
    | "security_review"
    | "manual_review";
  severity: "info" | "warning" | "urgent" | "blocked";
  lifecycle: "open" | "acknowledged" | "resolved" | "dismissed" | "expired" | "escalated";

  title: string;
  summary: string;
  safeReasonCode: string;
  sourceRefs: AttentionSourceRef[];
  evidenceRefs?: AttentionEvidenceRef[];

  targetSurfaceId?: string;
  targetItemId?: string;
  relatedCapabilityId?: string;
  relatedGovernedToolId?: string;
  participantRefs?: Array<{ kind: "account" | "agent" | "workflow" | "autonomous_task" | "service"; id: string; safeLabel: string }>;

  assignedRoleIds?: string[];
  assignedAccountIds?: string[];
  visibleToCapabilityIds: string[];
  allowedActionIds: string[];

  producerId: string;
  producerVersion: string;
  idempotencyKey: string;
  correlationId: string;
  causationId?: string;
  traceIds: string[];

  createdAt: string;
  lastChangedAt: string;
  dueAt?: string;
  expiresAt?: string;
  freshness: { status: "fresh" | "stale" | "recomputing"; lastSourceEventId?: string; recomputeReason?: string };
  redaction: { profile: "self" | "tenant-admin" | "customer-admin" | "support" | "auditor" | "agent"; omittedFieldKeys?: string[] };
};

type AttentionSourceRef = {
  kind: "domain_event" | "workstream_event" | "workflow" | "autonomous_task" | "trace" | "policy" | "projection" | "external_state" | "computed";
  refId: string;
  label: string;
  capabilityId?: string;
  traceId?: string;
  correlationId?: string;
};

type AttentionEvidenceRef = {
  refType: "surface" | "trace" | "document" | "decision" | "audit_event" | "task_result" | "view_row";
  refId: string;
  label: string;
  redacted: boolean;
};
```

## `WorkstreamAttentionSummary` shape

Use this for dashboards, left rail indicators, My Account aggregate panels, digests, and realtime refresh hints.

```ts
type WorkstreamAttentionSummary = {
  workstreamId: string;
  displayName: string;
  owningFunctionalAgentId: string;
  authContext: {
    tenantId: string;
    selectedContextId: string;
    customerId?: string;
    visibleCapabilityIds: string[];
  };
  attentionCount: number;
  highestSeverity?: "info" | "warning" | "urgent" | "blocked";
  categories?: {
    decisions?: number;
    approvals?: number;
    exceptions?: number;
    blockedRuns?: number;
    policyIssues?: number;
    overdueItems?: number;
    failedActions?: number;
    providerBlocked?: number;
  };
  lastChangedAt?: string;
  stale?: { isStale: boolean; reason?: string; lastKnownEventId?: string };
  traceIds: string[];
};
```

The count means: there are `N` open or escalated items in this workstream that currently require this user's attention, given identity, tenant/customer context, memberships, roles, permissions, workstream availability, redaction, and authority.

## Producers and idempotency

Every attention item is produced by a bounded backend producer, not by arbitrary UI logic. `workstream-manifest.json` uses workstream-local `attentionCategories` ids; producer contracts in `attention-and-dashboards.md` must map each local id to one canonical `AttentionItem.category`, the canonical severity vocabulary `info | warning | urgent | blocked`, lifecycle behavior, and producer ownership.

A producer contract must define:

- stable `producerId` and version;
- source families consumed: domain events, workstream events, workflow state, AutonomousAgent task snapshots/results, timers, views, audit traces, policy outcomes, or external state;
- target workstream and functional agent;
- local manifest category id and canonical `AttentionItem.category` / severity / lifecycle rules;
- actor/role assignment rules;
- source refs and evidence refs;
- idempotency key strategy;
- upsert/resolve/dismiss/expire semantics;
- authorization and redaction for readers;
- audit/work trace behavior;
- tests for duplicate source events, stale source state, cross-tenant denial, and replay.

Idempotency keys should be deterministic from source family, source id, tenant/customer scope, target workstream, category, and stable state when possible. Replays must update or no-op the same item instead of multiplying attention.

## Lifecycle operations

| Operation | Meaning | Required authority |
|---|---|---|
| `open_attention_item` | Open the target workstream/surface/item and append a surface-request item. | Read target workstream and target surface capability. |
| `acknowledge` | Actor has seen the item; it may remain open. | Assigned actor, allowed role, or capability-specific acknowledgement authority. |
| `resolve` | Underlying issue is corrected or accepted as complete. | Capability-specific command/decision authority; backend checks source state. |
| `dismiss` | Actor intentionally removes from their queue without resolving underlying source. | Dismiss authority; must not hide audit-required/risky items if policy forbids dismissal. |
| `escalate` | Route to higher authority, decision card, policy review, support, or internal worker. | Escalation capability and target authority. |
| `expire` | Item is no longer actionable due to time/source change. | Producer/timer/system authority; preserve trace. |

Lifecycle operations usually return an updated dashboard/attention surface, target surface, decision card, or typed `system_message` surface.

## My Account and left rail aggregation

- My Account may show the current user's aggregate authorized attention across accessible workstreams.
- My Account aggregate panels must preserve the source `workstreamId` and open source workstreams through governed `open_attention_item` or `open_workstream` actions.
- Left rail counts are compact summaries of authorized backend projections; hidden/denied workstreams must not leak existence through counts.
- Zero-count, stale, forbidden, and unavailable states should be explicit UI states, not missing data.
- Aggregate counts must not include cross-tenant/customer items, hidden workstreams, unauthorized roles, or support-only data unless the current support grant allows it.

## Dashboard and surface graph integration

A role-specific dashboard is attention-first. Its top region should prominently answer what needs the current authorized user's attention now in the selected `AuthContext`. Its second primary region should show what the current user can do next: allowed actions, safe shortcuts, and surface requests backed by governed capabilities. Supporting context such as what is happening, what is blocked/risky/failed/overdue, who or what participates, and what changed recently should reinforce those two actionable goals rather than displacing them. Detailed attention cards should link to their owning source, evidence, traces, capability, and target surface. Dashboard cards, lists, badges, charts, counters, rows, task/progress panels, shortcuts, and buttons are actionable graph affordances when they represent attention or available work; clicking or keyboard-activating them should append a request-like workstream item and then append/open an attention item, target structured surface, detail/decision/progress surface, evidence/trace node, governed capability-backed action result, updated dashboard, or typed `system_message`. Ready dashboards should show authorized work the actor can do; unauthorized/forbidden attention targets should be omitted from dashboard payloads rather than displayed as disabled work objects, while safe denials still apply for stale clicks, deep links, manual requests, and changed authorization.

Common graph edges:

- `open_attention_item` → target surface or detail card;
- `acknowledge` / `dismiss` / `resolve` → updated dashboard or `system_message`;
- `retry_failed_action` → command/progress/result surface;
- `request_approval` / `approve` / `reject` → decision card;
- `escalate` → internal agent task, support handoff, workflow pause/resume, or policy review;
- `start_investigation` → Audit/Trace workstream or internal worker progress/result surface.

Every protected edge maps to a capability and governed-tool exposure channel. Frontend action visibility is advisory only.

## Test expectations

Attention tests should cover:

- producer idempotency and duplicate source-event no-op behavior;
- open, acknowledge, resolve, dismiss, expire, and escalate lifecycle transitions;
- tenant/customer isolation and hidden-workstream non-enumeration;
- role-specific dashboard counts, My Account aggregate counts, and dashboard object interaction targets;
- left rail count, zero, denied, unavailable, and stale states;
- redaction for support/auditor/customer/self profiles;
- source refs, evidence refs, correlation ids, and trace ids;
- stale/recompute behavior and projection refresh hints;
- action-to-capability authorization and denial `system_message` surfaces;
- replay/backfill safety;
- AutonomousAgent task progress/result attention when durable internal work is in scope.
