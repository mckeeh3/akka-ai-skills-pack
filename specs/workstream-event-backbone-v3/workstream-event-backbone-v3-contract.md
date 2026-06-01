# Workstream Event Backbone v3 Contract

## Status and scope

This contract is the implementation handoff for `workstream-event-backbone-v3`. It generalizes the completed attention v1/v2 starter scope from direct producer-to-attention writes into a bounded, governed event backbone that can feed attention, dashboard projections, trace references, update delivery, and future digest/notification hooks.

v3 does **not** replace governed capabilities, authorization checks, audit/work traces, or the v1 attention lifecycle. It introduces a typed event envelope and source-ref contract so selected backend state changes can be published once and consumed by bounded projection paths.

Out of scope for v3:

- broad AutonomousAgent runtime integration;
- model-backed worker success without a real governed Akka runtime path;
- enterprise notification/digest infrastructure;
- frontend-authoritative event or badge state;
- a loose global event bus that can bypass capability, policy, tenant/customer, audit, or tool-boundary rules.

Future AutonomousAgent runtime integration remains the next-after-v3 follow-up. It should emit durable task lifecycle events into this contract instead of inventing separate ad hoc attention wiring.

## Core rule

```text
governed backend capability / timer / workflow / provider check
→ WorkstreamEventEnvelope with tenant/customer/AuthContext + source refs + capability provenance
→ idempotent consumer/projection path
→ attention.upsert_item / attention.resolve_item / attention.expire_item and/or dashboard/trace projection
→ backend-owned workstream/My Account/rail/update delivery reads
```

The source domain remains the authority for whether a condition exists. Events are durable evidence and projection triggers; they are not permission grants, frontend commands, or substitutes for source-state validation.

## Event envelope

Every event published to the v3 backbone must carry a browser-safe `WorkstreamEventEnvelope` equivalent with these fields:

| Field | Required | Meaning |
|---|---:|---|
| `eventId` | yes | Globally unique event id for this event record. Prefer deterministic ids only when the same source transition is intentionally replayed. |
| `eventType` | yes | Stable dotted type such as `invitation.delivery.failed`, `governance.proposal.submitted`, `workflow.task.blocked`, or `provider.readiness.failed`. |
| `eventFamily` | yes | One of the event families below. |
| `schemaVersion` | yes | Envelope/payload schema version, starting with `1`. |
| `occurredAt` | yes | Source occurrence time, not consumer processing time. |
| `publishedAt` | yes | Backend publish time. |
| `tenantId` | yes | Tenant scope copied from selected AuthContext or trusted source state. |
| `customerId` | nullable | Customer scope copied from selected AuthContext or trusted source state. |
| `authContext` | yes | Browser-safe selected AuthContext basis: selected context id when available, scope type, tenant/customer ids, role ids or capability ids needed for projection decisions. Do not include raw JWTs or provider secrets. |
| `actor` | yes | Actor/caller summary: `actorType` (`account`, `system`, `timer`, `provider`, `worker`), `accountId` when applicable, and safe display label. |
| `sourceRefs` | yes | Source refs proving why the event exists. See source-ref contract. |
| `capabilityRefs` | yes | Product capability and governed-tool ids that authorized or produced the source transition. |
| `correlationId` | yes | Request/timer/task correlation id for audit/work trace stitching. |
| `idempotencyKey` | yes | Stable semantic key for duplicate handling by consumers/projections. |
| `causationId` | nullable | Prior command/event/task id that caused this event. |
| `traceRefs` | yes | Audit/work/prompt/tool trace refs already written or atomically associated with the source transition. |
| `owningWorkstreamId` | yes | Primary functional-agent workstream that owns the event's dashboard/attention projection. |
| `targetSurfaceId` | nullable | Preferred browser-safe surface for opening related detail, decision, or workflow state. |
| `payloadClass` | yes | Stable payload class name, e.g. `InvitationDeliveryEventPayload`. |
| `payload` | yes | Browser-safe payload snapshot for consumers. Secrets, raw tokens, raw prompts, and hidden tool payloads are forbidden. |
| `redactionHints` | yes | Omitted field keys, minimum redaction level, and whether the payload may be exposed to browser/agent surfaces. |
| `projectionHints` | optional | Suggested attention category/severity, projection name, or refresh scope. Consumers must still enforce their own rules. |

### Source refs

A `sourceRef` must be explicit enough for audit and projection without exposing sensitive data:

| Field | Meaning |
|---|---|
| `refType` | `domain_event`, `aggregate`, `workflow`, `autonomous_task`, `timer`, `external_provider`, `capability`, `surface_action`, `audit_trace`, `work_trace`, `attention_item`, or `projection`. |
| `refId` | Stable source id such as invitation id, proposal id, task id, timer id, provider id, trace id, or attention item id. |
| `label` | Browser-safe label. |
| `capabilityId` | Capability required to inspect this source when exposed. |
| `traceId` | Audit/work trace proving production or source read. |
| `correlationId` | Correlation id tied to the event envelope. |

Source refs are not authority. Opening a referenced source still requires the relevant backend capability and selected AuthContext.

## Event families

### Domain events

State changes owned by starter domain/application services.

Initial candidates:

- `invitation.delivery.queued`
- `invitation.delivery.failed`
- `invitation.delivery.sent`
- `invitation.revoked`
- `invitation.accepted`
- `governance.proposal.drafted`
- `governance.proposal.submitted`
- `governance.proposal.decided`
- `governance.proposal.activated`
- `governance.proposal.rolled_back`

### Workflow/process lifecycle events

Bounded workflow or process-like transitions that users can inspect.

Initial candidates:

- `workflow.access_review.started`
- `workflow.access_review.blocked_provider_or_runtime`
- `workflow.access_review.completed_review_required`
- `workflow.access_review.cancelled`
- `workflow.access_review.result_accepted`
- `workflow.access_review.result_rejected`
- `workflow.governance_impact_analysis.blocked_provider_or_runtime`

### Task/worker events

Internal task state changes, including AutonomousAgent-adjacent records currently represented by deterministic starter task records.

Initial candidates:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

These events must honestly represent real starter-supported states. They must not report model-backed success unless the normal local runtime has invoked the concrete governed Akka Agent/AutonomousAgent path and succeeded.

### Provider/config events

Provider/model/runtime readiness and fail-closed state.

Initial candidates:

- `provider.readiness.missing_config`
- `provider.readiness.invalid_config`
- `provider.readiness.failed_health_check`
- `provider.readiness.fail_closed`
- `provider.readiness.restored`

Provider events may create blocked attention but must never fake readiness or silently fall back to deterministic model-less runtime behavior.

### Attention lifecycle events

Events emitted when an attention projection changes lifecycle state. These are useful for traces/update delivery but must not recursively create new attention items without a bounded consumer rule.

Initial candidates:

- `attention.item.opened`
- `attention.item.updated`
- `attention.item.resolved`
- `attention.item.expired`
- `attention.item.acknowledged`
- `attention.item.dismissed`

### Audit/work trace events

Trace/evidence events that can feed Audit/Trace dashboards or attention when they represent a reviewable failure, denial cluster, policy issue, or provider problem.

Initial candidates:

- `audit.trace.appended`
- `work.trace.appended`
- `tool.invocation.denied`
- `tool.invocation.failed`
- `prompt.assembly.blocked`
- `agent.work.failed_closed`

## Idempotency

Every event must define a stable `idempotencyKey`:

```text
workstream-event:<eventFamily>:<eventType>:<tenantId>:<customerId-or-none>:<source-ref-id>:<semantic-transition>
```

Consumer/projection idempotency must be based on the event id plus semantic idempotency key:

- duplicate delivery of the same event id is a no-op;
- duplicate semantic transition with new evidence may update projection fields but must not create a second active attention item;
- stale events must not overwrite newer source evidence;
- retries must preserve first-created projection timestamps where the projection already exists;
- no-op, denied, update, resolve, and expire outcomes must append audit/work trace evidence.

Attention item ids may continue to follow v2 keys such as `attention:user-admin:invitation-delivery:<invitationId>` while consumers store `eventId`/`idempotencyKey` as projection evidence.

## Publication rules

1. Publish only after the producing operation has passed its own authorization, scope, policy, idempotency, and validation checks.
2. Copy `tenantId`, `customerId`, and selected AuthContext from trusted backend state; never trust browser-provided scope alone.
3. Include the capability/governed-tool that caused the transition, such as `user_admin.invitation.send`, `governance.policy.proposal.submit`, `user_admin.access_review.start`, or `attention.producer.*` during migration.
4. Include source refs and trace refs written by the producing operation.
5. Sanitize payload and refs using the same redaction doctrine as attention and surfaces: no raw invitation tokens, raw JWTs, provider credentials, API keys, raw prompt text, hidden prompt text, raw tool payloads, or secret config.
6. Missing provider/security configuration should publish honest blocked/fail-closed events when a governed operation detects that state; it must not produce successful worker or provider-ready events.
7. Publication must not invoke browser-only state updates directly. Update delivery reads backend projections.

## Consumer/projection rules

Consumers/projections are bounded governed integration points. Initial v3 may use repository-backed application services in the starter template; generated apps can realize the same contract with Akka Consumers, Views, TimedActions, Workflows, or Event Sourced Entities once the component shape is selected.

A consumer/projection must:

1. validate event scope and reject cross-tenant/customer mismatches;
2. verify the event family/type is in its allow-list;
3. enforce projection capability/read authority when creating attention or dashboard rows;
4. use `idempotencyKey` and source refs to make duplicate/retry delivery safe;
5. preserve source refs, correlation ids, trace refs, and redaction hints on the target projection;
6. map only supported event types to attention lifecycle operations;
7. append audit/work trace for allowed, denied, failed, duplicate, and no-op projection outcomes;
8. never grant mutation authority or execute a domain side effect merely because an event exists.

## Initial projections and consumers

| Projection/consumer | Source event families | Target behavior |
|---|---|---|
| `workstream.event.consumer.attention` | domain, workflow/process, task/worker, provider/config | Maps allowed events to v1 attention upsert/resolve/expire while preserving source refs and idempotency. |
| `workstream.event.projection.dashboard` | domain, workflow/process, task/worker, provider/config, audit/work trace | Updates backend dashboard projection state and workstream summaries. |
| `workstream.event.projection.trace` | all families | Stitches event refs into audit/work trace surfaces. |
| `workstream.event.delivery.refresh` | attention lifecycle, selected projection updates | Emits or exposes backend-derived refresh hints. Frontend state remains non-authoritative. |

## v2 producer to v3 candidate map

| Current v2 producer/path | Existing source | v3 events | Initial bounded task target |
|---|---|---|---|
| `attention.producer.user_admin.invitation_delivery` | `InvitationService.recordDeliveryResult`, `runInvitationDeliveryTimedCheck`, invitation resend/revoke/accept paths | `invitation.delivery.failed`, `invitation.delivery.sent`, `invitation.delivery.near_expiry`, `invitation.delivery.expired`, `invitation.revoked`, `invitation.accepted` | Publish invitation delivery events and consume them into existing `attention:user-admin:invitation-delivery:<invitationId>` upsert/resolve/expire path. |
| `attention.producer.governance.policy_approval` | `GovernancePolicyService.submitProposal`, decision/activation lifecycle | `governance.proposal.submitted`, `governance.proposal.decided`, `governance.proposal.activated`, `governance.proposal.rolled_back` | Publish proposal lifecycle events and consume submitted/cleared states into governance attention/projection state. |
| `attention.producer.worker.task_state` | `UserAdminAccessReviewService.start`, `recordWorkerResult`, `cancel`, `acceptResult`, `rejectResult` | `workflow.access_review.started`, `workflow.access_review.blocked_provider_or_runtime`, `workflow.access_review.completed_review_required`, `workflow.access_review.cancelled`, `workflow.access_review.result_accepted`, `workflow.access_review.result_rejected` | Publish access-review lifecycle events and consume blocked/review/rejected/resolved states into attention/dashboard projections. |
| provider readiness blocked system-message paths | workstream agent/provider fail-closed surfaces, Agent Admin readiness | `provider.readiness.missing_config`, `provider.readiness.fail_closed`, `provider.readiness.restored` | Add bounded provider-readiness events only where a real backend readiness check exists; do not invent success. |
| audit/trace failure evidence surfaces | Audit/Trace service, denied traces, provider failures | `audit.trace.appended`, `work.trace.appended`, `tool.invocation.denied`, `agent.work.failed_closed` | Feed Audit/Trace dashboard/source refs; create attention only for explicit reviewable failure categories. |
| SSE/current `WorkstreamEvent` examples | `WorkstreamService.initialEvents` presentation stream examples | `attention.item.updated`, `projection.refresh.available`, `surface.stale` | Treat as delivery hints after backend projection changes, not as authoritative state. |

## Bounded implementation sequence

1. Add starter event records/envelopes and an in-memory/durable event repository seam matching this contract.
2. Add event publication for invitation delivery failure/success and corresponding attention consumer/projection path.
3. Add governance proposal and access-review workflow lifecycle publication.
4. Add provider/fail-closed lifecycle events only for existing starter blocked/readiness paths.
5. Harden update delivery so rail/workstream/My Account refreshes read backend event-backed attention/projections.
6. Update docs and verify event idempotency, tenant isolation, authorization/redaction, source refs, duplicate consumer behavior, and projection results.

## Guardrails

- Events never bypass governed capabilities, backend authorization, policy checks, audit, or ToolPermissionBoundary enforcement.
- Events may trigger projections; they must not directly perform protected domain mutations.
- Consumers must be allow-listed by event family/type and target projection.
- Source refs and payloads are browser-safe by construction.
- Frontend/SSE/update streams are refresh surfaces only; backend projections remain authoritative.
- Model-backed worker success requires the real governed runtime path. Missing provider/configuration yields blocked/fail-closed events and attention, not canned success.

## Gaps and blockers

- There is no starter `WorkstreamEventEnvelope` domain record or repository seam yet.
- Existing v2 producers write attention directly; v3 needs event publication plus an idempotent consumer/projection layer before producer logic can be retired.
- Existing `WorkstreamService.WorkstreamEvent` stream examples are presentation-oriented and do not carry the full envelope, source refs, idempotency, capability refs, or redaction hints.
- Provider readiness events need a real backend readiness source. Do not add restored/ready events until such a source exists.
- Broad AutonomousAgent task runtime integration is intentionally deferred until after v3; the next mini-project should wire real AutonomousAgent lifecycle events into this backbone.
