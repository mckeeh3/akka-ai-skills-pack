---
name: akka-view-from-service-stream
description: Implement Akka Java SDK Views that subscribe to public service streams from another Akka service using TableUpdater and @Consume.FromServiceStream.
---

# Akka View from Service Stream

Use this skill when a View subscribes to a public stream published by another Akka service in the same Akka project.

Capability-first framing: use service-stream Views for subscriber-owned read/evidence capabilities based on another service's public event contract. Treat the public stream as an integration boundary and expose only scoped, redacted query rows appropriate for the subscriber's UI/API/MCP/agent callers.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `../docs/service-to-service-views.md`
- `../examples/akka-components/src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../examples/akka-components/src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../docs/capability-first-backend-architecture.md`

## Use this pattern when

- another Akka service publishes a public event stream with `@Produce.ServiceStream`
- this service needs a query model built from that public stream
- the subscriber should store rows for later queries rather than trigger side effects
- the subscriber needs a curated read/evidence capability, not a command or integration side-effect capability

If the subscriber should trigger side effects or downstream writes instead of maintaining a query model, use a Consumer subscriber.

## Source-specific rules

1. Subscribe on the `TableUpdater` with `@Consume.FromServiceStream(service = "producer-service", id = "stream_id")`.
2. Consume only the producer's public event contract, not internal entity events.
3. Use `onEvent(...)` handlers for event-style public messages.
4. Use `updateContext().eventSubject()` when the producer includes subject metadata for row identity.
5. Use `rowState()` for incremental projections.
6. Return `effects().updateRow(...)`, `effects().deleteRow()`, or `effects().ignore()` explicitly.
7. Keep query methods and wrapper aliases aligned with the projected row shape.
8. For non-SSE `ORDER BY`, include each ordered column in the same query's `WHERE` conditions; split optional filters into separate query methods rather than using `OR` branches.
9. For view queries exposed as SSE, do not include `ORDER BY`; SSE events are delivered in created/event order.
10. Preserve tenant/customer scope, public-event provenance, correlation ids, and caller-safe/redacted fields required by the subscriber capability contract.

## Repository references

- `docs/service-to-service-views.md`
  - dedicated producer/subscriber view guidance
- `docs/service-to-service-consumers.md`
  - compare against consumer subscribers when deciding between projection and side effects
- `ShoppingCartPublicEventsConsumer`
  - producer-side service-stream example
- `ReviewRequestsByStatusView`
  - local reference for query wrappers and updater/query layout

## Design rules

- Treat `service`, `id`, and the public event types as part of the cross-service API.
- Prefer a transformed row model tailored to subscriber-side queries and authorization boundaries.
- Do not couple the subscriber view to the producer's internal package layout or private event types.
- If row selection depends on a stable aggregate id, require producer-side `ce-subject` metadata.
- Remember eventual consistency when testing and when designing endpoint callers.

## Testing rules

This repository does not include a two-service executable fixture for service-stream views.

For real local testing:
- run producer and subscriber as separate Akka services
- use different local ports
- verify producer `@Acl` allows the subscriber service
- publish producer events and query the subscriber view until it reflects the public stream

## Generated SaaS view contract

For generated SaaS read/evidence capabilities, require:
- tenant/customer scoped row keys and query filters aligned with the selected `AuthContext`;
- redacted DTO rows for the chosen UI/API/MCP/agent-tool consumers, not raw state dumps;
- stable surface payload or evidence-bundle mapping when used by structured surfaces;
- data-access audit/work-trace requirements at the query or endpoint boundary;
- tests for authorized query, forbidden/cross-tenant query, redaction, projection update/delete behavior, and surface/API/tool consumers where exposed.


## Review checklist

Before finishing, verify:
- `@Consume.FromServiceStream` is on the `TableUpdater`
- `service` matches the publishing service name
- `id` matches the producer `@Produce.ServiceStream(id = "...")`
- updater handlers accept only public event types
- `updateContext().eventSubject()` is used when row identity depends on subject metadata
- query wrappers and aliases match exactly
- protected subscriber queries enforce scope/redaction in the selected exposure path and do not leak producer-private details
- non-SSE `ORDER BY` columns also appear in the query `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
- tests and callers assume eventual consistency
