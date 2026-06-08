# Attention Event Producers v2 Contract

## Status and scope

This contract is the implementation handoff for `workstream-attention-event-producers-v2`. It extends the completed v1 shared attention backbone with bounded starter/reference **producer** semantics.

v1 remains authoritative for `AttentionItem` fields, lifecycle commands, scoped reads, redaction, and governed-tool ids. v2 defines how backend state changes, timed checks, and worker/task states create, update, resolve, or expire those v1 items.

Out of scope for v2:

- replacing the shared v1 attention backbone;
- replacing domain queues such as invitations, governance proposals, audit evidence, or provider-readiness state;
- making frontend-only `railAttentionState` authoritative;
- broad notification/digest infrastructure; and
- fake model-backed AutonomousAgent or worker success paths.

## Core rule

```text
real backend source state/event/task/timer
→ stable AttentionProducer id + source identity + idempotency key
→ attention.upsert_item / attention.resolve_item / attention.expire_item
→ backend-owned attention summaries
→ workstream dashboards, My Account, and rail update delivery read backend state
```

The source domain remains the authority for whether the condition exists. The attention backbone is the authoritative read/lifecycle projection for actionable attention. Frontend update delivery is only a presentation refresh/notification channel.

## Producer model

An attention producer is a bounded backend integration that maps source state into an attention lifecycle transition.

Required producer fields:

| Field | Meaning |
|---|---|
| `producerId` | Stable id for the producer implementation and trace labels. |
| `sourceFamily` | Domain/service/timer/task family, e.g. `invitation_delivery`. |
| `sourceStateId` | Stable source object or aggregate id, e.g. invitation id, provider readiness id, proposal id, task id. |
| `sourceEventId` | Optional event/message id when event-driven; absent for state-derived or timed checks. |
| `idempotencyKey` | Stable key used to avoid duplicate items and make retries safe. |
| `attentionItemId` | Stable v1 item id written to the backbone. |
| `tenantId` / `customerId` | Scope copied from the selected AuthContext/source state. |
| `owningWorkstreamId` | Workstream that owns the primary dashboard/detail surface. |
| `requiredCapabilityId` | Capability required to see/open/act on the item. |
| `sourceRefs` | Source refs proving why the item exists. |
| `correlationId` | Request/event/timer/task correlation for audit/work trace. |

## Idempotency contract

Each producer must define one deterministic `idempotencyKey`:

```text
attention-producer:<producerId>:<tenantId>:<customerId-or-none>:<sourceStateId>:<attention-kind>
```

Implementation may use the key directly as `attentionItemId` or map it to a shorter stable id. Duplicate delivery of the same source event/state/timer/task condition must:

- return the existing item when content has not changed;
- update `summary`, `severity`, `sourceRefs`, `updatedAt`, `lastChangedAt`, and `correlationId` when source evidence changed;
- never create a second active item for the same source condition;
- preserve `createdAt` from the first creation; and
- append audit/work trace for producer no-op or update.

Resolution/expiry must be idempotent: repeated resolve/expire calls for the same key produce no additional active item and emit a no-op trace rather than failing.

## Lifecycle mapping

Producer operations:

| Operation | Uses v1 lifecycle | Required behavior |
|---|---|---|
| `upsertAttention` | `attention.upsert_item` | Create or update an `open` item from source state. |
| `resolveAttention` | `attention.resolve_item` or producer-internal repository save | Mark item `resolved` when the source condition is complete or cleared. |
| `expireAttention` | `attention.expire_item` or producer-internal repository save | Mark item `expired` when a timed condition is stale/no longer actionable. |
| `reopenAttention` | `attention.upsert_item` | Reopen only when the source condition recurs with the same active key and current source state says action is needed. |

Implementation tasks may add small internal service helpers around `AttentionService` if v1 lacks a public producer-oriented resolve/expire helper. Those helpers must keep v1 authorization, scope, redaction, and audit expectations intact.

## Producer ids and source mappings

### `attention.producer.user_admin.invitation_delivery`

- Source family: invitation state and email delivery/outbox result.
- Owning workstream: `agent-user-admin`.
- Category: `invitation_delivery`.
- Severity: `warning` for failed delivery; `urgent` if repeated failure or near-expiry action is required.
- Source refs: `domain_event`, `external_provider`, and/or `audit_trace` for invitation delivery attempt/result.
- Upsert when: invitation email delivery fails, bounces, is provider-blocked, or requires admin review.
- Resolve when: invitation is resent successfully, revoked, accepted, or delivery review is completed.
- Timed checks: expiring invitation reminders or expired unresolved delivery failures may update or expire attention.
- Suggested item id/key: `attention:user-admin:invitation-delivery:<invitationId>`.

### `attention.producer.governance.policy_approval`

- Source family: governance proposal, approval, simulation, activation, rollback, or policy decision state.
- Owning workstream: `agent-governance-policy`.
- Category: `governance_approval` or `policy_exception`.
- Severity: `urgent` for awaiting approval; `blocked` for activation/rollback blocked by provider/runtime/policy state.
- Source refs: `capability`, `surface_action`, `work_trace`, and/or `domain_event` for proposal/decision ids.
- Upsert when: proposal awaits authorized approval, policy exception needs decision, or activation/rollback is blocked.
- Resolve when: approved/denied/cancelled/activated/rolled back or no longer requires human review.
- Suggested item id/key: `attention:governance:policy-approval:<proposalOrDecisionId>`.

### `attention.producer.audit_trace.failure_evidence`

- Source family: audit trace, provider failure evidence, denial patterns, high-risk traces.
- Owning workstream: `agent-audit-trace`.
- Category: `audit_failure_evidence` or `security_review`.
- Severity: `warning` for evidence available; `urgent` or `blocked` for repeated/high-risk provider/security failures.
- Source refs: `audit_trace`, `work_trace`, `external_provider`, and/or `capability`.
- Upsert when: provider failure, auth denial cluster, audit anomaly, or trace summary worker state creates reviewable evidence.
- Resolve when: evidence is acknowledged/resolved by an authorized review flow, underlying provider is healthy, or anomaly is dismissed.
- Suggested item id/key: `attention:audit-trace:failure-evidence:<traceOrEvidenceId>`.

### `attention.producer.agent_admin.provider_readiness`

- Source family: provider/model/runtime configuration readiness.
- Owning workstream: `agent-agent-admin`.
- Category: `provider_readiness`.
- Severity: `blocked` when model/runtime provider configuration prevents safe model-backed work.
- Source refs: `external_provider`, `capability`, `work_trace`, and/or `surface`.
- Upsert when: provider configuration is missing, disabled, invalid, health check fails, or model-backed path is fail-closed.
- Resolve when: configured provider passes readiness validation through the governed runtime path.
- Timed checks: provider readiness can be rechecked by a bounded timer and update stale evidence; do not fake readiness.
- Suggested item id/key: `attention:agent-admin:provider-readiness:<providerOrRuntimeId>`.

### `attention.producer.worker.task_state`

- Source family: internal worker/task state, including AutonomousAgent-adjacent task records where present.
- Owning workstream: workstream responsible for the task (`agent-user-admin`, `agent-audit-trace`, `agent-governance-policy`, or `agent-agent-admin`).
- Category: `workflow_blocked`, `agent_task_failed`, `audit_failure_evidence`, or `provider_readiness` depending on source state.
- Severity: `blocked` for `blocked_provider_or_runtime`; `warning` for failed/rejected result; `urgent` for overdue review.
- Source refs: `autonomous_task`, `workflow`, `timer`, `work_trace`, and/or `audit_trace`.
- Upsert when: task is failed, stuck, rejected, blocked on provider/runtime config, or completed with human review required.
- Resolve when: task is cancelled, retried successfully through a real governed runtime path, accepted by a human, or superseded.
- Timed checks: stale tasks should update `lastChangedAt` and evidence without inventing success.
- Suggested item id/key: `attention:worker-task:<taskId>:<stateKind>`.

## Timed checks

Timed checks are producer runs triggered by schedule or bounded service call, not frontend timers.

A timed producer must record:

- `producerId`;
- `timerId` or scheduled check name;
- cutoff calculation input such as `expiresAt`, `lastAttemptAt`, or `lastStateChangeAt`;
- selected tenant/customer scope;
- source refs with `kind=timer` plus the source object ref; and
- audit/work trace result: upsert, resolve, expire, no-op, or denied.

Initial v2 timed-check candidates:

1. `attention.producer.user_admin.invitation_delivery` — invitation delivery failure that remains unresolved near expiry or after expiry.
2. `attention.producer.agent_admin.provider_readiness` — provider/runtime readiness remains fail-closed after a bounded recheck.
3. `attention.producer.worker.task_state` — blocked/stale worker task remains in `blocked_provider_or_runtime`, failed, or review-needed state.

## Task-state attention

Task-state attention must reflect real starter-supported states only. It must not mark model-backed work complete unless the normal local runtime invokes the configured Akka agent/runtime path and succeeds.

Supported source states for v2:

| State | Attention behavior |
|---|---|
| `blocked_provider_or_runtime` | Upsert `blocked` attention with recovery guidance and provider/runtime source refs. |
| `failed` | Upsert `warning`/`blocked` attention with failure evidence and retry/review surface. |
| `stuck` / stale | Timed check updates attention with stale duration and last trace. |
| `completed_review_required` | Upsert review attention until accepted/rejected. |
| `rejected_result` | Upsert failed/review attention and link to decision/evidence surface. |
| `cancelled` / accepted | Resolve matching active attention. |

## Update delivery target

v2 update delivery should use the smallest honest starter path that preserves backend authority:

1. producer changes update backend `AttentionService` state;
2. shell/workstream/My Account refresh requests call backend surfaces/APIs that include `attention.list_workstream_items`, `attention.list_my_account_items`, and `attention.list_rail_summaries`;
3. frontend rail and surfaces render backend-derived summaries/items separately from transient `railAttentionState` unseen response badges.

Preferred initial delivery target: explicit backend-derived refresh/poll on workstream open, My Account open, producer-affecting action completion, and shell rail refresh. SSE or streams may be added only if the existing starter API shape supports them without overbuilding.

Required update-delivery proof:

- rail count source is `attention.list_rail_summaries`;
- workstream dashboard item source is `attention.list_workstream_items`;
- My Account personal queue source is `attention.list_my_account_items`;
- frontend-only `railAttentionState` remains a non-authoritative background-response badge.

## Authorization, tenancy, and redaction

Producer commands must enforce:

1. tenant/customer scope copied from source state and/or selected AuthContext;
2. source capability authorization for the producing operation;
3. `AttentionItem.requiredCapabilityId` matching the read/open authority for the target surface;
4. no cross-tenant upsert/resolve/expire;
5. source refs redacted to browser/agent-safe labels;
6. unauthorized source or target returns denial/no-op without leaking hidden item existence; and
7. audit/work trace for allowed, denied, update, resolve, expire, and no-op operations.

Backend attention state remains authoritative for actionable attention. Frontend-only state must never satisfy producer completion, timed checks, or task-state attention.

## v1 gap map and v2 implementation handoff

| v1 behavior | Gap | v2 task handoff |
|---|---|---|
| `WorkstreamService.seedStarterCoreAttention` upserts provider readiness, governance approval, and audit failure evidence from dashboard/rail reads. | Read-time seeding is useful for v1 but not a real producer boundary. | Move bounded state changes into producer helpers for Agent Admin, Governance/Policy, and Audit/Trace. |
| `MyAccountService.seedStarterCoreAttention` duplicates starter core derivations before personal attention reads. | My Account read should consume backend-derived attention, not own producer decisions. | Domain/service producer task should centralize producer ids and remove duplicate derivation where practical. |
| User Admin invitation delivery attention is derived from failed invitation count. | Needs source-specific identity and resolution on resend/revoke/accept/success. | Wire invitation delivery producer with per-invitation or aggregate idempotency keys and resolve paths. |
| v1 lifecycle supports acknowledge/resolve/dismiss but producer-oriented resolve/expire helpers are not explicit. | Implementation tasks need safe internal producer APIs for upsert/resolve/expire by producer key. | Add small `upsertAttention`, `resolveAttention`, and timed `expireAttention` service boundaries if needed. |
| Rail and dashboard read backend-derived summaries/items on refresh. | Producer-affecting actions need a selected update delivery strategy. | Reuse explicit refresh/poll first; add stream only if small and covered by tests. |
| Worker/task and timed/stale states are out of scope in v1. | No task-state attention or timed checks yet. | Add bounded timed/worker producers for honest `blocked_provider_or_runtime`, stale, failed, or review-needed states. |

## Test obligations

Backend tests for producer tasks must cover:

- stable producer ids and idempotency keys;
- `upsertAttention` creates one item and duplicate upsert updates/no-ops without duplicates;
- `resolveAttention` and `expireAttention` are idempotent;
- invitation, governance, provider readiness, audit evidence, and worker/task source refs;
- timed checks updating stale/expired attention;
- task-state attention for `blocked_provider_or_runtime` without fake success;
- tenant/customer isolation and hidden capability redaction;
- audit/work trace labels for producer upsert/update/resolve/expire/no-op/denied.

Frontend/update-delivery tests must cover:

- backend-derived update delivery refreshes rail/My Account/workstream attention after producer-affecting changes;
- actionable counts use backend summaries rather than frontend-only state;
- `railAttentionState` remains visually and semantically distinct from actionable attention; and
- redacted/denied update results do not leak hidden workstream names, counts, source ids, or item labels.
