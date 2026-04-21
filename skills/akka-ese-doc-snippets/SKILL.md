---
name: akka-ese-doc-snippets
description: Generate focused documentation snippets for Akka Java SDK EventSourcedEntity concepts. Use when writing or replacing docs so each snippet teaches one ESE concept with minimal noise.
---

# Akka ESE Doc Snippets

Use this skill when the task is to write documentation examples for event sourced entities rather than full production-style reference code.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/OrderEntity.java`
- `../../../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../../../src/test/java/com/example/application/OrderEntityTest.java`
- `../../../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`
- `../references/akka-entity-comparison.md`

## Mission

Produce snippets that are optimized for teaching AI coding agents one idea at a time.

Documentation snippets should be:
- minimal
- concept-focused
- internally consistent
- easy to map to a larger reference example later
- explicit about event sourced semantics: persist facts as events, then evolve state by replaying them

## Snippet design rules

1. One snippet should teach one concept.
2. Avoid feature-heavy examples unless the section is explicitly about feature composition.
3. Prefer short state records and simple events.
4. Do not mix ESE and KVE terminology.
5. If comparing with key value entities, state the difference explicitly.
6. Keep endpoint types API-specific.
7. Keep side effects outside the entity.
8. Keep `applyEvent(...)` pure and replay-safe.

## Preferred snippet set

When rewriting or extending ESE documentation, prefer a separate snippet for each topic:

1. state + events model
   - small immutable state record
   - sealed event interface with `@TypeName`
2. entity declaration
   - `@Component`, `EventSourcedEntity<State, Event>`, `emptyState()`
3. write handler + event persistence
   - validate, decide events, `persist(...)` or `persistAll(...)`, `thenReply(...)`
4. event application
   - pure `applyEvent(...)`
5. read handler
   - `ReadOnlyEffect<T>` with `currentState()`
6. delete handler
   - final event, then `deleteEntity()`
7. TTL handler
   - `persist(...).expireAfter(...)`
8. strongly consistent read / replication filter
   - `Effect<T>` for primary-routed read
   - `updateReplicationFilter(...)`
9. notification stream
   - `NotificationPublisher`, `NotificationStream`, SSE mapping
10. unit test pattern
   - `EventSourcedTestKit`
11. integration test pattern
   - `TestKitSupport`, `httpClient`, `componentClient`
12. consumer-driven flow pattern
   - consume events, perform side effects outside entity

## Mapping to repository references

Use these as source material, but simplify aggressively for docs:
- `ShoppingCartEntity` -> edge-facing validate/persist/read/delete/notifications
- `OrderEntity` -> downstream/internal no-op, multi-event command, consistent read, replication
- `ExpiringShoppingCartEntity` -> TTL
- `ShoppingCartCheckoutConsumer` -> side effects outside entity
- `ShoppingCartEndpoint` -> SSE mapping
- `ShoppingCartEntityTest` -> unit test shape
- `OrderEntityTest` -> multi-event test shape
- `ExpiringShoppingCartEntityTest` -> TTL assertion shape

## What to trim out of docs

When extracting from reference implementations, usually remove:
- extra helper methods not relevant to the section
- multiple unrelated command handlers in the same snippet
- broad endpoint APIs when showing only one command
- detailed business rules not central to the concept being taught
- rich domain decomposition when the snippet only needs a minimal state + event example

## Snippet review checklist

Before finishing, verify:
- snippet teaches one concept only
- ESE semantics are clear
- events have `@TypeName`
- `applyEvent(...)` is pure when shown
- no key-value vocabulary leaks in
- code compiles in principle for the shown concept
- names are stable and descriptive
- the snippet would help an AI agent reproduce the pattern later

## Response style

When generating doc snippets:
- say which concept each snippet teaches
- say which repository reference it was derived from
- mention any intentional simplifications from the full example
