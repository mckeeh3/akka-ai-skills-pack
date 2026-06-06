# Attention and Dashboards

Attention is backend-owned, scoped, authorized, actionable state. It answers: what needs this actor's attention in this workstream under the selected `AuthContext`?

## Core summary contract

```ts
type WorkstreamAttentionSummary = {
  workstreamId: string;
  displayName: string;
  owningFunctionalAgentId: string;
  authContext: { tenantId: string; selectedContextId: string; customerId?: string; visibleCapabilityIds: string[] };
  attentionCount: number;
  highestSeverity?: "info" | "warning" | "urgent" | "blocked";
  categories?: { decisions?: number; approvals?: number; exceptions?: number; blockedRuns?: number; policyIssues?: number; overdueItems?: number; failedActions?: number; providerBlocked?: number };
  lastChangedAt?: string;
  stale?: { isStale: boolean; reason?: string; lastKnownEventId?: string };
  traceIds: string[];
};
```

## Foundation workstream dashboard contracts

| Workstream | Dashboard | Attention categories | My Account / rail aggregation |
|---|---|---|---|
| My Account | `my-account-dashboard` | personal queue, context/profile/settings issues, accessible-workstream summaries | Owns aggregate inbox panels; does not own source workstreams. |
| User Admin | `user-admin-dashboard` | expired/failed invitations, stale access review, last-admin risk, support-access expiry, risky role changes | Count only items visible to the current admin/support/auditor role. |
| Agent Admin | `agent-governance-center` | draft behavior changes, prompt/skill/reference review, tool-boundary risk, test-console failures, provider/model readiness | Count only governed-agent items visible to the actor. |
| Audit/Trace | `audit-trace-explorer` | audit anomaly, denied access investigation, export review, correlation gaps | Count investigation items and assigned review work, not every audit event. |
| Governance/Policy | `agent-governance-center` | approval requests, policy conflicts, simulation/replay findings, rollback needs | Count policy/governance items assigned to allowed reviewers/owners. |

## Producer rules

Each attention producer must define stable producer id/version, source family, target workstream, category/severity rules, lifecycle, source refs, evidence refs, idempotency key, redaction, traces, and replay behavior. Duplicate source events update/no-op the same item rather than creating duplicates.

## Lifecycle operations

- `open_attention_item` opens the owning workstream/surface through governed authorization.
- `acknowledge`, `resolve`, `dismiss`, `escalate`, and `expire` require backend capability checks and return updated dashboards, result surfaces, or typed `system_message` surfaces.
- Hidden/denied workstreams do not leak attention counts.
- Frontend-only unread badges are presentation state, not authoritative attention.

## Tests

Cover producer idempotency, tenant/customer isolation, role-specific dashboard visibility, My Account and rail counts, lifecycle transitions, stale/recompute behavior, redaction, trace links, and denial/result system-message surfaces.
