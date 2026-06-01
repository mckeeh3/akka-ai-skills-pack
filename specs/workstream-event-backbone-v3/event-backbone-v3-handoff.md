# Workstream Event Backbone v3 Handoff

## Scope delivered

This handoff covers the starter/reference event-backbone increment delivered by `specs/workstream-event-backbone-v3/`.

Relationship to previous increments:

- **v1 attention backbone** (`specs/workstream-attention-backbone-v1/`): implemented shared backend-owned `AttentionItem` lifecycle state, scoped workstream/My Account/rail reads, redaction, and audit/work traces.
- **v2 attention producers and update delivery** (`specs/workstream-attention-event-producers-v2/`): implemented bounded producer paths for invitation delivery, governance approval, timed checks, worker/task blocked or review-needed states, and backend-derived refresh delivery.
- **v3 governed event backbone** (this mini-project): adds a typed `WorkstreamEventEnvelope`/`WorkstreamEventSourceRef` contract, Akka-backed `WorkstreamEventRepository` seam, event publication for bounded starter invitation and access-review lifecycle states, an idempotent `WorkstreamEventAttentionConsumer`, and backend-derived `projection.refresh.available` hints that point clients back to backend-owned attention/dashboard projections.
- **Future AutonomousAgent runtime integration**: remains the recommended next initiative. It should emit real durable task lifecycle events into the v3 backbone only after task start/query/result/cancel/notification capabilities, provider/model fail-closed behavior, tool boundaries, and traces are implemented through the governed Akka `AutonomousAgent` runtime path.

## Implemented v3 starter scope

The starter template now includes:

- `WorkstreamEventEnvelope` and `WorkstreamEventSourceRef` domain records carrying tenant/customer scope, browser-safe selected AuthContext, actor, source refs, capability refs, correlation/idempotency/trace refs, owning workstream, payload metadata, redaction hints, and projection hints.
- `WorkstreamEventRepository` plus `AkkaWorkstreamEventRepository`/`DurableWorkstreamEventRepositoryEntity` for normal Akka component-backed persistence when `ComponentClient` is available.
- `WorkstreamEventPublisher` for bounded starter events including invitation delivery success/failure and access-review lifecycle states such as blocked provider/runtime and cancellation.
- `WorkstreamEventAttentionConsumer` that allow-lists supported event types, preserves source/idempotency refs, updates/resolves attention through existing producer paths, and rejects cross-tenant mismatches.
- Workstream event-backed refresh hints from `WorkstreamService.events(...)` as `projection.refresh.available`; these are delivery hints only and require clients to reload backend-owned projections.
- Backend and frontend tests covering event envelope/source refs, idempotency, duplicate/retry behavior, tenant isolation, lifecycle events, and non-authoritative frontend refresh handling.

## Guardrails preserved

- v3 events are facts and projection triggers, not permission grants.
- Source domain state remains authoritative for whether an invitation, workflow/task state, provider/config state, or attention condition exists.
- Consumers/projections may update attention/dashboard state only through bounded backend services that preserve tenant/customer scope, capability refs, audit/work trace refs, and redaction.
- Frontend event/SSE/update delivery remains non-authoritative; rail, My Account, and dashboard surfaces must read backend attention/projection state.
- Provider blocked/fail-closed states are honest runtime states. The starter must not report model-backed worker or AutonomousAgent success unless the normal local runtime has invoked the concrete governed Akka Agent/AutonomousAgent path and succeeded.

## Known limits and future work

v3 is intentionally bounded. It does not implement:

- broad AutonomousAgent durable task runtime integration;
- enterprise notification centers, preferences, or digest infrastructure;
- events for every possible domain/workflow state in generated apps;
- provider readiness restored/ready events without a real backend readiness source;
- frontend-authoritative event state or push-only correctness.

Recommended next mini-project: **AutonomousAgent runtime integration over the v3 event backbone**. It should add governed capabilities and tests for durable task lifecycle, task notifications, progress/result surfaces, cancellation/failure attention, provider fail-closed behavior, and event emission into `WorkstreamEventEnvelope` without bypassing auth, policy, audit, or tool boundaries.
