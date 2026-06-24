---
name: akka-kve-notifications
description: Add live notification streams to Akka Java SDK KeyValueEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka KVE Notifications

Use this skill when clients need live updates from a key value entity.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

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
- The SaaS Foundation App example snapshot does not currently include a KeyValueEntity notification stream. Use the Akka SDK docs as the source of truth and add target-project examples only when a feature needs live KVE updates.

## Endpoint rules

When exposing notifications over HTTP:
- subscribe through the typed component client, for example `componentClient.forKeyValueEntity(id).notificationStream(Entity::notifications).source()`
- map domain notifications to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal notification types directly outside the service unless explicitly intended

Current repository note:
- The SaaS Foundation App example snapshot does not currently expose KVE notifications through an HTTP endpoint. Keep any SSE/API mapping target-specific and return browser-safe DTOs only.

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Testing note:
- Add a focused target-project test that captures a stub `NotificationPublisher` and asserts messages are published only after successful state changes. The current SaaS Foundation App example snapshot does not include this KVE notification test.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Anti-patterns

Avoid:
- publishing before state update succeeds
- using notifications instead of durable business workflows
- exposing raw internal notifications directly as public API when an API shape is preferable
