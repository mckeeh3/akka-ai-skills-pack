---
name: akka-kve-doc-snippets
description: Generate focused documentation snippets for Akka Java SDK KeyValueEntity concepts. Use when writing or replacing docs so each snippet teaches one KVE concept with minimal noise.
---

# Akka KVE Doc Snippets

Use this skill when the task is to write documentation examples for key value entities rather than full production-style reference code.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../../../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../../../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`
- `../references/akka-entity-comparison.md`

## Mission

Produce snippets that are optimized for teaching AI coding agents one idea at a time.

Documentation snippets should be:
- minimal
- concept-focused
- internally consistent
- easy to map to a larger reference example later
- explicit about key value semantics: update full state, do not persist events

## Snippet design rules

1. One snippet should teach one concept.
2. Avoid feature-heavy examples unless the section is explicitly about feature composition.
3. Prefer short state records and simple commands.
4. Do not mix KVE and ESE terminology.
5. If comparing with event sourcing, state the difference explicitly.
6. Keep endpoint types API-specific.
7. Keep side effects outside the entity.

## Preferred snippet set

When rewriting or extending KVE documentation, prefer a separate snippet for each topic:

1. state model
   - small immutable record
2. entity declaration
   - `@Component`, `KeyValueEntity<State>`, `emptyState()`
3. write handler
   - validate, compute next state, `updateState(...)`, `thenReply(...)`
4. read handler
   - `ReadOnlyEffect<T>` with `currentState()`
5. delete handler
   - `deleteEntity()`
6. TTL handler
   - `updateState(...).expireAfter(...)`
7. strongly consistent read / replication filter
   - `Effect<T>` for primary-routed read
   - `updateReplicationFilter(...)`
8. notification stream
   - `NotificationPublisher`, `NotificationStream`, SSE mapping
9. unit test pattern
   - `KeyValueEntityTestKit`
10. integration test pattern
   - `TestKitSupport`, `httpClient`, `componentClient`

## Mapping to repository references

Use these as source material, but simplify aggressively for docs:
- `DraftCartEntity` -> edge-facing write/read/delete/notifications
- `PurchaseOrderEntity` -> downstream/internal no-op, consistent read, replication
- `ExpiringDraftCartSessionEntity` -> TTL
- `DraftCartEndpoint` -> SSE mapping
- `DraftCartEntityTest` -> unit test shape
- `ExpiringDraftCartSessionEntityTest` -> TTL assertion shape

## What to trim out of docs

When extracting from reference implementations, usually remove:
- extra helper methods not relevant to the section
- multiple unrelated command handlers in the same snippet
- consumer-driven flow details unless the section is about consumers
- broad endpoint APIs when showing only one command
- detailed business rules not central to the concept being taught

## Snippet review checklist

Before finishing, verify:
- snippet teaches one concept only
- KVE semantics are clear
- no event-sourced vocabulary leaks in
- code compiles in principle for the shown concept
- names are stable and descriptive
- the snippet would help an AI agent reproduce the pattern later

## Response style

When generating doc snippets:
- say which concept each snippet teaches
- say which repository reference it was derived from
- mention any intentional simplifications from the full example
