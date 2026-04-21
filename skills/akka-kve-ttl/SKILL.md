---
name: akka-kve-ttl
description: Add automatic expiry to Akka Java SDK KeyValueEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for key value entities.
---

# Akka KVE TTL

Use this skill when a key value entity should expire automatically after a period without further writes.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `../../../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`
- `../../../src/main/java/com/example/domain/ExpiringDraftCartSession.java`
- `../../../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`

## Core pattern

Attach TTL to a state update effect:
- `effects().updateState(newState).expireAfter(Duration...).thenReply(...)`

Use TTL only on commands that actually update state.

## Rules

- TTL belongs on the entity effect, not in domain code.
- Keep TTL examples small and focused.
- Validate before state update.
- If no state update happens, no TTL is attached.
- A later update without `expireAfter(...)` cancels the TTL.
- If the entity should keep expiring after later writes, each relevant update must include `expireAfter(...)`.

## Repository example

See:
- `ExpiringDraftCartSessionEntity.addItem(...)`

## Testing guidance

Use `KeyValueEntityTestKit` and assert:
- reply value
- `stateWasUpdated()`
- `result.getExpireAfter()`
- resulting state

Repository example:
- `ExpiringDraftCartSessionEntityTest`

## Anti-patterns

Avoid:
- putting TTL logic in domain state helpers
- assuming TTL remains active after later writes that omit `expireAfter(...)`
- adding TTL to read-only handlers
