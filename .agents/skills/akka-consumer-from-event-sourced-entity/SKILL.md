---
name: akka-consumer-from-event-sourced-entity
description: Implement Akka Java SDK Consumers that subscribe to EventSourcedEntity events, optionally start from snapshots, and trigger idempotent downstream effects.
---

# Akka Consumer from Event Sourced Entity

Use this skill when a Consumer reacts to persisted events from an Event Sourced Entity.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`

## Use this pattern when

- the downstream behavior depends on event facts, not just latest state
- the consumer should react differently to different event subtypes
- replay of historical events matters
- snapshots may help a new consumer catch up faster

## Core pattern

1. Annotate the class with `@Consume.FromEventSourcedEntity(MyEntity.class)`.
2. Accept the entity event type in `onEvent(...)`.
3. Use a `switch` to handle only the event subtypes you care about.
4. Return `effects().ignore()` for irrelevant event types.
5. Use `messageContext().eventSubject()` for the entity id.
6. If needed, read current entity state via `ComponentClient` before calling another component.
7. Keep downstream writes idempotent because delivery is at least once.

## Snapshot pattern

Add `@SnapshotHandler` only when catch-up from current state is more useful than replaying the full event history.

Rules:
- the snapshot parameter type must match the entity state type
- use the snapshot to initialize downstream state or emit a summary message
- keep snapshot logic compatible with later event processing

## Pattern references

These are Akka substrate mechanics examples, not generated-product architecture templates.

- `WorkstreamEventAttentionConsumer`
  - filters to `CheckedOut`
  - reads current workstream event state
  - creates a downstream order entity
- `WorkstreamEventAttentionConsumer`
  - republishes every workstream-event event to a broker topic
  - sets `ce-subject` metadata from the entity id
- `WorkstreamEventAttentionConsumer`
  - transforms internal events into service-stream public events
  - uses `@Acl` because other services subscribe

## Gotchas

- deleting an event sourced entity does not itself emit a consumer callback unless you persist a final deletion event before delete
- do not mutate Akka component-backed consumer fields to track progress
- do not assume one delivery per event
- do not place side effects inside entity `thenReply()` when a consumer is the right downstream boundary

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- `@Consume.FromEventSourcedEntity` points at the right entity
- `onEvent(...)` accepts the entity event type
- ignored event types are explicit
- `messageContext().eventSubject()` is used when the entity id is needed
- snapshot usage is intentional if `@SnapshotHandler` is present
- downstream component calls are idempotent
