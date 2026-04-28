---
name: akka-view-from-service-stream
description: Implement Akka Java SDK Views that subscribe to public service streams from another Akka service using TableUpdater and @Consume.FromServiceStream.
---

# Akka View from Service Stream

Use this skill when a View subscribes to a public stream published by another Akka service in the same Akka project.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `../../../docs/service-to-service-views.md`
- `../../../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../../src/main/java/com/example/application/ReviewRequestsByStatusView.java`

## Use this pattern when

- another Akka service publishes a public event stream with `@Produce.ServiceStream`
- this service needs a query model built from that public stream
- the subscriber should store rows for later queries rather than trigger side effects

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
- Prefer a transformed row model tailored to subscriber-side queries.
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

## Review checklist

Before finishing, verify:
- `@Consume.FromServiceStream` is on the `TableUpdater`
- `service` matches the publishing service name
- `id` matches the producer `@Produce.ServiceStream(id = "...")`
- updater handlers accept only public event types
- `updateContext().eventSubject()` is used when row identity depends on subject metadata
- query wrappers and aliases match exactly
- non-SSE `ORDER BY` columns also appear in the query `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
- tests and callers assume eventual consistency
