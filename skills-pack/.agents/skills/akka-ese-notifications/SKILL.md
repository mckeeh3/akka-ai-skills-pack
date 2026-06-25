---
name: akka-ese-notifications
description: Add live notification streams to Akka Java SDK EventSourcedEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka ESE Notifications

Use this skill when clients need live updates from an event sourced entity.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`

## Core pattern

1. Inject `NotificationPublisher<Event>` into the entity.
2. Expose a `NotificationStream<Event>` method.
3. Publish only after successful persistence.
4. Map domain events to API notification records before exposing externally.

## Entity rules

- Notification message type is often the domain event type.
- Publish inside `thenReply(...)`, not before persist.
- Do not rely on notifications for business-critical correctness.
- Notifications are for live updates, not replay of history.

Current repository note:
- The SaaS Foundation App example snapshot does not currently include an EventSourcedEntity notification stream. Use the Akka SDK docs as the source of truth and add target-project examples only when a feature needs live ESE updates.

## Endpoint rules

When exposing notifications over HTTP:
- subscribe through the typed component client, for example `componentClient.forEventSourcedEntity(id).notificationStream(Entity::notifications).source()`
- map domain events to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal event types directly outside the service unless explicitly intended

Related repository example:
- `WorkstreamEndpoint.events(...)` demonstrates SSE response mapping for backend-owned workstream events, but it is not an entity `NotificationStream` example.

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Testing note:
- Add a focused target-project test that captures a stub `NotificationPublisher` and asserts events are published only after successful persistence. The current SaaS Foundation App example snapshot does not include this ESE notification test.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Anti-patterns

Avoid:
- publishing before persist succeeds
- using notifications instead of durable business workflows
- exposing raw domain notifications directly as public API when an API shape is preferable
