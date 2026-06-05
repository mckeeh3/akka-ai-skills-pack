---
name: akka-ese-notifications
description: Add live notification streams to Akka Java SDK EventSourcedEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka ESE Notifications

Use this skill when clients need live updates from an event sourced entity.

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

Repository example:
- `AgentDefinitionEntity.notifications()`
- `AgentDefinitionEntity.persistAndReply(...)`
- `AgentDefinitionEntity.deleteAndReply(...)`

## Endpoint rules

When exposing notifications over HTTP:
- use `ComponentClient.notificationStream(...)`
- map domain events to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal event types directly outside the service unless explicitly intended

Repository example:
- `WorkstreamEndpoint.notifications(...)`
- `WorkstreamEndpoint.toNotification(...)`

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Repository example:
- `AgentDefinitionEntityTest`

This repository uses a stub `NotificationPublisher` to capture published events and assert them.

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
