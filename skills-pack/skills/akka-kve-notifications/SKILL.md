---
name: akka-kve-notifications
description: Add live notification streams to Akka Java SDK KeyValueEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka KVE Notifications

Use this skill when clients need live updates from a key value entity.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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

Current repository note:
- The core app example snapshot does not currently include a KeyValueEntity notification stream. Use the Akka SDK docs as the source of truth and add target-project examples only when a feature needs live KVE updates.

## Endpoint rules

When exposing notifications over HTTP:
- subscribe through the typed component client, for example `componentClient.forKeyValueEntity(id).notificationStream(Entity::notifications).source()`
- map domain notifications to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal notification types directly outside the service unless explicitly intended

Current repository note:
- The core app example snapshot does not currently expose KVE notifications through an HTTP endpoint. Keep any SSE/API mapping target-specific and return browser-safe DTOs only.

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Testing note:
- Add a focused target-project test that captures a stub `NotificationPublisher` and asserts messages are published only after successful state changes. The current core app example snapshot does not include this KVE notification test.

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
