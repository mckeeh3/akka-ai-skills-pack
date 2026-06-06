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

| producerId | Target workstream | Source family | Categories | Idempotency key strategy | My Account / rail effect | Required trace/tests |
| --- | --- | --- | --- | --- | --- | --- |
| `my-account-attention-summary-producer.v1` | `my-account` | authorized source workstream attention projections, profile/settings/context state | personal queue, context/profile/settings issues, accessible-workstream summaries | `my-account:<tenantId>:<accountId>:<sourceWorkstreamId>:<sourceItemId|state>` | My Account aggregate panels only; source workstream ownership preserved. | Trace source refs; test hidden-workstream non-enumeration and cross-tenant denial. |
| `user-admin-attention-producer.v1` | `user-admin` | invitation lifecycle, access-review tasks, support-access expiry, membership/role risk signals | failed action, overdue item, security review, manual review | `user-admin:<tenantId>:<customerId|none>:<sourceId>:<category>:<stable-state>` | Left rail/User Admin dashboard counts; My Account only for assigned actor. | Test duplicate source no-op, tenant isolation, support/auditor redaction. |
| `agent-admin-attention-producer.v1` | `agent-admin` | governed AgentDefinition/prompt/skill/reference/manifest/tool-boundary proposals, provider readiness checks | approval, policy conflict, provider blocked, manual review | `agent-admin:<tenantId>:<artifactId>:<category>:<proposal-or-version>` | Count only review/provider items visible to steward/reviewer. | Test authority-expansion denial, inactive artifact redaction, trace links. |
| `audit-trace-attention-producer.v1` | `audit-trace` | audit anomaly, denied access investigation, correlation gaps, export review state | audit anomaly, security review, manual review | `audit-trace:<tenantId>:<correlationOrTraceId>:<category>:<stable-state>` | Investigation items only, not every audit event. | Test scoped search visibility, support grant redaction, replay. |
| `governance-policy-attention-producer.v1` | `governance-policy` | policy proposal, simulation/replay finding, approval, rollback/outcome drift state | approval, policy conflict, decision, outcome drift | `governance-policy:<tenantId>:<policyOrProposalId>:<category>:<stable-state>` | Count items assigned to allowed owners/reviewers. | Test approval authority, rollback denial, policy trace evidence. |

## Lifecycle operations

- `open_attention_item` opens the owning workstream/surface through governed authorization.
- `acknowledge`, `resolve`, `dismiss`, `escalate`, and `expire` require backend capability checks and return updated dashboards, result surfaces, or typed `system_message` surfaces.
- Hidden/denied workstreams do not leak attention counts.
- Frontend-only unread badges are presentation state, not authoritative attention.

## Tests

Cover producer idempotency, tenant/customer isolation, role-specific dashboard visibility, My Account and rail counts, lifecycle transitions, stale/recompute behavior, redaction, trace links, and denial/result system-message surfaces.
