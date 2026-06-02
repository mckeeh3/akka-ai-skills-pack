# Attention Backbone v1 Contract

## Status and scope

This contract is the implementation target for the AI-first SaaS starter template attention backbone. It is a starter/reference contract, not a separate production application.

v1 defines one **shared attention backbone**: backend-owned state/projections for actionable items that need a user's attention across workstreams. Normal workstream dashboards remain workstream-scoped. My Account and the left rail consume authorized aggregate projections from the same backbone.

Out of scope for v1: realtime streams, personal digests, advanced AutonomousAgent task notification processing, generic notification inboxes, and replacement of domain queues such as invitations or policy proposals.

## Core rule

```text
one shared backend attention backbone
→ AttentionItem has owningWorkstreamId and source links
→ workstream-scoped projections feed dashboard attention
→ My Account aggregate projections answer “what do I need to do next?”
→ left-rail summary projections expose compact authorized counts
```

Frontend-only unseen response badges are separate transient UI state. They must never be treated as authoritative actionable attention or used to satisfy backend/runtime completion claims.

## AttentionItem schema

An `AttentionItem` is a scoped, authorized, actionable signal that a human or governed participant may need to inspect, decide, approve, correct, retry, escalate, acknowledge, dismiss, resolve, or learn from something.

Required fields:

| Field | Type | Meaning |
|---|---|---|
| `itemId` | string | Stable backend id. |
| `tenantId` | string | Required tenant isolation key. |
| `customerId` | string? | Optional customer scope when selected AuthContext is customer-scoped. |
| `owningWorkstreamId` | string | Functional-agent/workstream id that owns the primary dashboard/detail projection, e.g. `agent-user-admin`. |
| `title` | string | Browser/agent-safe short label. |
| `summary` | string | Browser/agent-safe explanation after redaction. |
| `category` | enum | Why attention is needed. See categories below. |
| `severity` | enum | `info`, `warning`, `urgent`, or `blocked`. |
| `status` | enum | Lifecycle state. See lifecycle below. |
| `assigneeKind` | enum | `account`, `role`, `capability`, `workstream`, or `tenant`. |
| `assigneeId` | string? | Account id, role id, capability id, workstream id, or tenant id matching `assigneeKind`. |
| `requiredCapabilityId` | string | Capability required to see/open/act on the item. |
| `surfaceRef` | object | Target workstream surface/action information for `open_attention_item`. |
| `sourceRefs` | array | Trace/workflow/task/event/capability references that justify the item. |
| `redactionLevel` | enum | `full`, `summary_only`, or `not_found_or_redacted`. |
| `createdAt` | instant | Creation time. |
| `updatedAt` | instant | Last state/content update time. |
| `lastChangedAt` | instant | Projection ordering timestamp. |
| `expiresAt` | instant? | Optional expiry. |
| `acknowledgedAt` | instant? | Set when acknowledged. |
| `resolvedAt` | instant? | Set when resolved. |
| `dismissedAt` | instant? | Set when dismissed. |
| `correlationId` | string | Request/work trace correlation. |

Recommended Java/TypeScript DTO names for later tasks:

- `AttentionItem`
- `AttentionItemStatus`
- `AttentionCategory`
- `AttentionSeverity`
- `AttentionSourceRef`
- `AttentionSurfaceRef`
- `WorkstreamAttentionSummary`
- `MyAccountAttentionSummary`

## Lifecycle

`AttentionItem.status` values:

| Status | Meaning | Idempotency |
|---|---|---|
| `open` | Active and actionable. Counts in summaries. | Creating an equivalent open item with the same dedupe key should update/return existing item. |
| `acknowledged` | Seen or accepted for handling but not resolved. Counts only when the item still requires action. | Re-acknowledge is no-op with audit trace. |
| `resolved` | Underlying issue/action completed. Does not count. | Re-resolve is no-op with audit trace. |
| `dismissed` | User/role intentionally dismissed as not requiring action. Does not count for that scope. | Re-dismiss is no-op with audit trace. |
| `expired` | No longer current due to expiry/source state. Does not count. | Re-expire is no-op with audit trace. |

Lifecycle commands must be authorized, tenant/customer scoped, audited/traced, and safe on duplicate retries.

## Categories and severity

Starter v1 categories:

- `invitation_delivery` — User Admin invitation delivery failure or review need.
- `provider_readiness` — Agent Admin provider/model/runtime configuration blocked or not ready.
- `governance_approval` — Governance/Policy proposal, threshold, prompt/skill/tool-boundary, or policy change awaiting authorized review.
- `audit_failure_evidence` — Audit/Trace failure evidence, denial pattern, or high-risk trace needing review.
- `access_review` — User Admin access review item.
- `policy_exception` — Policy conflict, deviation, or exception.
- `workflow_blocked` — Workflow paused, blocked, or waiting on human decision.
- `agent_task_failed` — Internal/background agent task failed, was rejected, or needs escalation.
- `security_review` — Security-sensitive authorization/support-access issue.

Severity semantics:

- `info` — needs awareness but not urgent.
- `warning` — should be reviewed.
- `urgent` — time/risk-sensitive human action.
- `blocked` — work is blocked or provider/security/runtime configuration prevents safe completion.

Frontend rail code may map `urgent`/`blocked` to existing visual tones, but backend contract names remain authoritative.

## Source references

`sourceRefs` preserve why the item exists and where to inspect evidence. Supported source ref kinds:

- `audit_trace`
- `work_trace`
- `workflow`
- `autonomous_task`
- `surface`
- `surface_action`
- `capability`
- `governed_tool`
- `domain_event`
- `timer`
- `consumer`
- `external_provider`

Minimum source ref fields:

| Field | Meaning |
|---|---|
| `kind` | Source ref kind. |
| `refId` | Stable source id such as trace id, workflow id, task id, surface id, event id, or provider status id. |
| `label` | Safe label. |
| `capabilityId` | Capability that can read/open this evidence, when applicable. |
| `traceId` | Audit/work trace id, when applicable. |
| `correlationId` | Correlation for the producing operation. |

## Surface/open target contract

`surfaceRef` tells `open_attention_item` what may be opened after backend authorization:

| Field | Meaning |
|---|---|
| `targetFunctionalAgentId` | Usually equals `owningWorkstreamId`. |
| `targetSurfaceId` | Surface to open or refresh. |
| `targetSurfaceType` | `dashboard`, `system_message`, `decision_card`, `audit_timeline`, etc. |
| `targetItemId` | Optional domain/source item id. |
| `defaultActionId` | Optional action to focus. |
| `requiredCapabilityId` | Capability required to open the target. |

Unauthorized, hidden, or cross-tenant targets must return a typed `system_message` / `not_found_or_redacted` result without leaking hidden workstream names, counts, or item labels.

## Projection/read contracts

### Workstream-scoped projection

`WorkstreamAttentionSummary` powers normal workstream dashboards and can be included in dashboard surface payloads.

Fields:

- `workstreamId`
- `displayName`
- `attentionCount`
- `highestSeverity`
- `categories`
- `lastChangedAt`
- `items` (optional detail list for dashboard read; scoped to that workstream)
- `traceRefs`
- `redaction`

Rules:

- scoped to one `owningWorkstreamId` plus tenant/customer/AuthContext;
- includes only authorized items for the actor/caller;
- supports detail reads for the workstream dashboard;
- does not become a global operations page.

### My Account aggregate projection

`MyAccountAttentionSummary` powers personal attention and My Account panels.

Fields:

- `totalAttentionCount`
- `highestSeverity`
- `workstreams: WorkstreamAttentionSummary[]`
- `personalQueue: AttentionItem[]`
- `traceRefs`
- `redaction`

Rules:

- aggregates only workstreams visible to the selected AuthContext;
- filters by role/capability/assignment so it answers “what do I need to do next?”;
- hidden workstreams produce no names, counts, or implied existence;
- replaces capability-only hard-coded personal attention derivation in later implementation tasks.

### Left-rail summary projection

The left rail consumes compact backend-derived `WorkstreamAttentionSummary` data for visible workstreams:

- count;
- highest severity;
- category counts when available;
- last changed timestamp.

The left rail may additionally show transient `railAttentionState` unseen/background-response badges, but those badges are explicitly not actionable backend attention and must be rendered with a distinct data kind/state.

## Capability and governed-tool ids

Starter v1 attention capability grouping: `attention.backbone`.

Governed-tools:

| Governed-tool id | Class | Exposure channels | Purpose |
|---|---|---|---|
| `attention.list_workstream_items` | read/evidence | browser-tool, API, internal-tool, view/query | List authorized items for one workstream dashboard. |
| `attention.list_my_account_items` | read/evidence | browser-tool, agent-tool via My Account evidence, API, internal-tool, view/query | List/aggregate authorized personal attention across visible workstreams. |
| `attention.list_rail_summaries` | read/evidence | browser-tool, API, view/query | Return compact backend-derived counts for visible left-rail workstreams. |
| `attention.open_attention_item` | read/evidence shell request | browser-tool, API | Resolve an attention item to an authorized workstream/surface target or safe denial. |
| `attention.acknowledge_item` | command | browser-tool, API, internal-tool | Mark authorized item acknowledged. |
| `attention.resolve_item` | command | browser-tool, API, internal-tool | Mark item resolved when source condition is complete. |
| `attention.dismiss_item` | command | browser-tool, API | Dismiss authorized item for the actor/scope. |
| `attention.upsert_item` | command/internal | internal-tool, consumer-tool, timer-tool, workflow-tool | Create/update a durable attention item from a source event/state/trace. |
| `attention.expire_item` | command/internal | internal-tool, timer-tool | Expire stale items. |

Existing related capability ids remain source/producer authorities, not the attention backbone itself:

- `my_account.list_personal_attention` should delegate/read from `attention.list_my_account_items` after migration.
- User Admin invitation/read capabilities can produce `invitation_delivery` items.
- `agent_admin.list_definitions` and provider readiness checks can produce `provider_readiness` items.
- `governance.policy.read` and approval/proposal capabilities can produce `governance_approval` items.
- `audit.trace.read` can produce `audit_failure_evidence` items.

## Authorization and redaction

Every read or lifecycle operation must enforce:

1. authenticated account;
2. selected `AuthContext`;
3. active tenant/customer membership;
4. visible workstream and required role/capability;
5. assignment/targeting rules;
6. tenant/customer id filters;
7. browser/agent-safe output redaction;
8. audit/work trace for protected reads, lifecycle changes, and denials.

Redaction rules:

- unauthorized workstream/item: return `not_found_or_redacted` without item title, count, source, or target workstream name;
- authorized summary but restricted details: return count/severity/category with `summary_only` and omit sensitive source refs;
- authorized detail: return `full` only for permitted evidence fields;
- cross-tenant access: deny, audit, and return safe error/system-message; never redact into partial success.

## Initial producer/derivation targets for starter v1

Later implementation tasks should seed/derive initial items from existing starter core workstream state where available:

| Workstream | Category | Example item | Source capability |
|---|---|---|---|
| User Admin | `invitation_delivery` | Failed invitation delivery needs review. | `secure-tenant-user-foundation` / invitation capabilities |
| Agent Admin | `provider_readiness` | Provider/model runtime readiness is blocked. | `agent_admin.list_definitions` |
| Governance/Policy | `governance_approval` | Governance policy decision awaits review. | `governance.policy.read` |
| Audit/Trace | `audit_failure_evidence` | Provider failure or authorization-denial evidence available. | `audit.trace.read` |
| My Account | aggregate only | Personal queue and workstream summaries from authorized items. | `my_account.list_personal_attention` delegating to attention backbone |

## Audit/work trace expectations

Required trace points:

- protected read allowed/denied for workstream items, My Account aggregate, and rail summaries;
- lifecycle command allowed/denied/no-op;
- item producer/upsert/expire operations with source refs and correlation id;
- `open_attention_item` target resolution including safe denial/redaction;
- data access to source traces/workflows/tasks/surfaces when included in details.

Trace event labels should distinguish attention reads from source capability reads, e.g. `ATTENTION_LIST_WORKSTREAM_ITEMS`, `ATTENTION_LIST_MY_ACCOUNT_ITEMS`, `ATTENTION_OPEN_ITEM`, `ATTENTION_ACKNOWLEDGE_ITEM`, `ATTENTION_UPSERT_ITEM`.

## Implementation substrate guidance for later tasks

v1 may choose the smallest safe starter substrate, but normal runtime claims require backend-owned state/projections, tenant/customer/AuthContext enforcement, and tests. Preferred realization shape:

- durable attention item state where lifecycle matters;
- scoped query/projection service for workstream, My Account, and rail summaries;
- internal producer/upsert API used by existing core services;
- browser API/shell request integration for reads and lifecycle actions;
- frontend types/components that consume backend-derived counts/items.

Do not satisfy this contract with Akka component-backed frontend fixtures, deterministic hard-coded personal-attention lists, or demo-only badge state.

## Test obligations for implementation tasks

Backend tests:

- authorized workstream item read;
- My Account aggregate across visible workstreams;
- left-rail summary count/severity from backend state;
- hidden workstream/item redaction;
- tenant/customer isolation;
- lifecycle idempotency/no-op behavior;
- protected-read, denial, lifecycle, and producer/upsert audit/work traces;
- `open_attention_item` success and safe denial.

Frontend tests:

- rail renders backend-derived actionable count separately from unseen-response badge;
- My Account personal queue renders backend items, empty state, denied/redacted state, actions, and trace refs;
- workstream dashboard renders item metadata and action/trace links;
- frontend-only `railAttentionState` cannot be the sole source for actionable attention counts.
