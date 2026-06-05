---
name: akka-consumer-from-event-sourced-entity
description: Implement Akka Java SDK Consumers that subscribe to EventSourcedEntity events, optionally start from snapshots, and trigger idempotent downstream effects.
---

# Akka Consumer from Event Sourced Entity

Use this skill when a Consumer reacts to persisted events from an Event Sourced Entity.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

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

## Generated SaaS consumer contract

For generated SaaS reactive capabilities, require:
- reactive capability id, event provenance, correlation id, and tenant/customer scope;
- system-principal or service-authority basis for downstream calls;
- idempotency key/dedupe strategy for at-least-once delivery;
- retry, poison, obsolete, forbidden, and no-op behavior;
- scoped/redacted publication when producing topics or service streams;
- audit/work-trace records for side effects, denials, retries, and emitted public events;
- tests for duplicate delivery, cross-tenant/forbidden input, idempotent downstream effects, audit/trace, and surface/realtime/API outcomes where exposed.


## Review checklist

Before finishing, verify:
- `@Consume.FromEventSourcedEntity` points at the right entity
- `onEvent(...)` accepts the entity event type
- ignored event types are explicit
- `messageContext().eventSubject()` is used when the entity id is needed
- snapshot usage is intentional if `@SnapshotHandler` is present
- downstream component calls are idempotent
