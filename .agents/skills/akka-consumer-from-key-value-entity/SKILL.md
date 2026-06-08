---
name: akka-consumer-from-key-value-entity
description: Implement Akka Java SDK Consumers that subscribe to KeyValueEntity state changes and optional deletes, using idempotent downstream updates.
---

# Akka Consumer from Key Value Entity

Use this skill when a Consumer reacts to Key Value Entity state updates.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`

## Source-specific semantics

A Key Value Entity consumer receives the latest updated state, but not necessarily every intermediate state change.

Therefore:
- base reactions on the current state snapshot
- do not depend on every historical transition being delivered
- use `@DeleteHandler` when deletions matter

## Core pattern

1. Annotate the class with `@Consume.FromKeyValueEntity(MyEntity.class)`.
2. Accept the state type in `onChange(...)` or `onUpdate(...)`.
3. Filter on state fields such as flags or status.
4. Use `messageContext().eventSubject()` for the entity id when needed.
5. Add `@DeleteHandler` if the entity can be deleted and that delete matters downstream.
6. Keep downstream writes idempotent.

## Repository example

- `WorkstreamEventAttentionConsumer`
  - reacts only when the workstream event is checked out
  - uses the workstream id from metadata or state
  - calls a downstream key value entity with an idempotent create command
  - includes `@DeleteHandler`

## Gotchas

- do not model KVE consumers as if they receive a full event log
- do not omit `@DeleteHandler` when delete behavior is required
- do not store mutable progress in the consumer instance

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
- `@Consume.FromKeyValueEntity` points at the right entity
- the handler accepts the entity state type
- delete handling is explicit when needed
- downstream logic depends on state, not historical event ordering
- duplicate delivery is tolerated
