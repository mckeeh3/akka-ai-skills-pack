---
name: akka-ese-notifications
description: Add live notification streams to Akka Java SDK EventSourcedEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka ESE Notifications

Use this skill when clients need live updates from an event sourced entity.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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
- The core app example snapshot does not currently include an EventSourcedEntity notification stream. Use the Akka SDK docs as the source of truth and add target-project examples only when a feature needs live ESE updates.

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
- Add a focused target-project test that captures a stub `NotificationPublisher` and asserts events are published only after successful persistence. The current core app example snapshot does not include this ESE notification test.

## Generated SaaS checks

When this feature supports a generated SaaS capability, verify:
- capability reason and target tenant/customer scope are explicit;
- expired, stale, replicated, deleted, or notification-only states have safe denial/no-op behavior;
- surface/API/tool/realtime consumers receive scoped, redacted DTOs only;
- idempotency and retry behavior are tested;
- audit/work-trace requirements are recorded or explicitly delegated to the caller capability.


## Anti-patterns

Avoid:
- publishing before persist succeeds
- using notifications instead of durable business workflows
- exposing raw domain notifications directly as public API when an API shape is preferable
