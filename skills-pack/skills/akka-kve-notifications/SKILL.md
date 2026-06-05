---
name: akka-kve-notifications
description: Add live notification streams to Akka Java SDK KeyValueEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka KVE Notifications

Use this skill when clients need live updates from a key value entity.

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
- `akka-context/sdk/key-value-entities.html.md`

## Core pattern

1. Inject `NotificationPublisher<Notification>` into the entity.
2. Expose a `NotificationStream<Notification>` method.
3. Publish only after successful state persistence.
4. Map domain notifications to API notification records before exposing externally.

## Entity rules

- Notification message type can be a simple record or sealed interface.
- Publish inside `thenReply(...)`, not before `updateState` or `deleteEntity` succeeds.
- Do not rely on notifications for business-critical correctness.
- Notifications are for live updates, not historical replay.

Repository example:
- `DurableIdentityRepositoryEntity.notifications()`
- `DurableIdentityRepositoryEntity.addItem(...)`
- `DurableIdentityRepositoryEntity.delete(...)`

## Endpoint rules

When exposing notifications over HTTP:
- subscribe through the typed component client, for example `componentClient.forKeyValueEntity(id).notificationStream(Entity::notifications).source()`
- map domain notifications to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal notification types directly outside the service unless explicitly intended

Repository example:
- `MeEndpoint.notifications(...)`
- `MeEndpoint.toNotification(...)`

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Repository example:
- `DurableIdentityRepositoryEntityTest`

This repository uses a stub `NotificationPublisher` to capture published messages and assert them.

## Generated SaaS checks

When this feature supports a generated SaaS capability, verify:
- capability reason and target tenant/customer scope are explicit;
- expired, stale, replicated, deleted, or notification-only states have safe denial/no-op behavior;
- surface/API/tool/realtime consumers receive scoped, redacted DTOs only;
- idempotency and retry behavior are tested;
- audit/work-trace requirements are recorded or explicitly delegated to the caller capability.


## Anti-patterns

Avoid:
- publishing before state update succeeds
- using notifications instead of durable business workflows
- exposing raw internal notifications directly as public API when an API shape is preferable
