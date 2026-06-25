---
name: akka-kve-doc-snippets
description: Generate focused documentation snippets for Akka Java SDK KeyValueEntity concepts. Use when writing or replacing docs so each snippet teaches one KVE concept with minimal noise.
---

# Akka KVE Doc Snippets

Use this skill when the task is to write documentation examples for key value entities rather than full production-style reference code.

## Compile contract gate

Use this skill for explicitly scoped doc/example maintenance, or for snippets that document a compile-ready Akka component slice. When a snippet is presented as generated-app runtime guidance rather than isolated teaching material, tie it back to the accepted graph context from `../docs/app-description-to-code-compile-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/capability-first-backend-architecture.md` instead of implying that the component API is the product contract.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
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

Use real current snapshot references as source material, and simplify aggressively for docs:
- `DurableIdentityRepositoryEntity` -> edge-facing current-state write/read patterns and audit repository writes
- `DurableNotificationRepositoryEntity` -> tenant-scoped notification item, preference, outbox, and digest current-state repository patterns
- `MeEndpoint` -> protected endpoint/API mapping patterns that preserve AuthContext and tenant/customer scope
- `DurableIdentityRepositoryEntityTest` -> unit test shape for KVE current-state reads/writes
- `DurableNotificationRepositoryEntityTest` -> unit test shape for tenant-scoped notification repository behavior

For delete, TTL, NotificationStream, strongly consistent read, replication, or SSE snippets, use the Akka SDK docs or a target-project implementation that actually contains the selected concept. Do not imply the current SaaS Foundation App snapshot includes a KVE delete, TTL, `NotificationPublisher`/`NotificationStream`, or replication example unless one has been added.

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
