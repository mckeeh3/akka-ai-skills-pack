---
name: akka-ese-ttl
description: Add automatic expiry to Akka Java SDK EventSourcedEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for event sourced entities.
---

# Akka ESE TTL

Use this skill when an event sourced entity should expire automatically after a period without further writes.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `../../../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `../../../src/main/java/com/example/domain/ExpiringShoppingCart.java`
- `../../../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

## Core pattern

Attach TTL to a persist effect:
- `effects().persist(event).expireAfter(Duration...).thenReply(...)`

Use TTL only on write commands that actually persist events.

## Rules

- TTL belongs on the entity effect, not in domain code.
- Keep TTL examples small and focused.
- Validate before persist.
- If no events are persisted, no TTL is attached.
- A later persist without `expireAfter(...)` cancels the TTL.
- If the entity should keep expiring after later writes, each relevant persist must include `expireAfter(...)`.

## Repository example

See:
- `ExpiringShoppingCartEntity.addItem(...)`

This shows:
- validation error on blank input
- persist of one event
- `.expireAfter(Duration.ofDays(30))`
- reply with `Done`

## Testing guidance

Use `EventSourcedTestKit` and assert:
- reply value
- persisted event
- `result.getExpireAfter()`
- resulting state

Repository example:
- `ExpiringShoppingCartEntityTest`

## Anti-patterns

Avoid:
- putting TTL logic in domain state or event handlers
- assuming TTL remains active after later persists that omit `expireAfter(...)`
- adding TTL to read-only handlers
