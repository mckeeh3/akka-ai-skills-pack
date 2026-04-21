---
name: akka-kve-application-entity
description: Implement core Akka Java SDK KeyValueEntity classes in the application package, including command handlers, read handlers, updateState, and delete behavior. Use companion skills for TTL, notifications, and replication.
---

# Akka KVE Application Entity

Use this skill for the `application` package entity class itself.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`

Load companion skills when needed:
- `akka-kve-ttl`
- `akka-kve-notifications`
- `akka-kve-replication`

## Entity skeleton

The entity should:
- extend `KeyValueEntity<State>`
- have `@Component(id = "...")`
- override `emptyState()`
- implement command handlers that return `Effect<T>` or `ReadOnlyEffect<T>`

## Constructor rules

Use constructor injection for supported dependencies, for example:
- `KeyValueEntityContext`
- `NotificationPublisher<Notification>`

If `emptyState()` needs the entity id, store `context.entityId()` in a field.

## Command handler algorithm

For each command:
1. inspect `currentState()`
2. validate input
3. return `effects().error(...)` on invalid input when appropriate
4. delegate business decision logic to domain helpers
5. if no state change is needed, reply without calling `updateState`
6. if state should change, compute the new full state
7. call `effects().updateState(newState)`
8. reply in `thenReply(...)`

## Read handler rules

Use:
- `ReadOnlyEffect<T>` for ordinary reads
- `Effect<T>` for strongly consistent reads in replicated deployments

Repository example:
- `PurchaseOrderEntity.getOrderConsistent()`

## Delete pattern

When deleting an entity:
- call `.deleteEntity()`
- then reply
- do not call `updateState(...)` afterward
- if the task only needs a reset, consider updating to empty state instead of deleting

Repository example:
- `DraftCartEntity.delete(...)`

## Feature-specific companion skills

For focused guidance, load:
- `akka-kve-ttl`
- `akka-kve-notifications`
- `akka-kve-replication`

## Anti-patterns

Never:
- mutate state directly in command handlers
- call external services from the entity
- skip `emptyState()` when a sensible default exists
- model KVE writes as persisted events

## Review checklist

Before finishing, verify:
- entity extends `KeyValueEntity<State>`
- `@Component(id = ...)` exists
- `emptyState()` is sensible
- validation happens before `updateState`
- no-op commands do not update state
- delete behavior is explicit when needed
- TTL, notifications, and replication use the companion skills when included
