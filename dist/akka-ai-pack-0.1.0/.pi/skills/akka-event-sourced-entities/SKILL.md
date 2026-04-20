---
name: akka-event-sourced-entities
description: Orchestrate Akka Java SDK EventSourcedEntity work across domain modeling, application entities, edge/internal flow patterns, unit tests, and integration tests. Use when the task spans more than one part of event sourced entity implementation.
---

# Akka Event Sourced Entities

Use this as the top-level skill for Akka Java SDK event sourced entity work.

## Goal

Generate or review event sourced entity code that is:
- correct for Akka SDK 3.4+
- cleanly split across `domain`, `application`, and `api`
- replay-safe
- easy for AI agents to extend
- backed by tests

## Required reading before coding

If these files exist, read them first:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project examples under `src/main/java/**/application/*Entity.java`
- matching domain files under `src/main/java/**/domain/*`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/OrderEntity.java`
- `../../../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/main/java/com/example/api/OrderEndpoint.java`
- `../../../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../../../src/test/java/com/example/application/OrderEntityTest.java`
- `../../../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-ese-domain-modeling`
  - state, commands, events, validators, command handlers, event handlers
- `akka-ese-application-entity`
  - core entity class, effects, reads, writes, and delete behavior
- `akka-ese-ttl`
  - `expireAfter(...)` and automatic expiry
- `akka-ese-notifications`
  - `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-ese-replication`
  - strongly consistent reads and replication filters
- `akka-ese-edge-and-flow-patterns`
  - endpoint-facing entities, downstream/internal entities, consumers, idempotent no-op behavior
- `akka-ese-doc-snippets`
  - focused documentation snippets for teaching one ESE concept at a time
- `akka-ese-unit-testing`
  - `EventSourcedTestKit` and entity unit tests
- `akka-ese-integration-testing`
  - `TestKitSupport`, endpoint tests, consumer-driven flows

If the task spans multiple areas, use this skill plus the relevant companion skills.

For ESE vs KVE choice questions, also read:
- `../references/akka-entity-comparison.md`

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

Rules:
- domain has no Akka effects
- application contains entities and consumers/workflows/timed actions
- api exposes request/response types and maps domain to API shapes

## Core rules

1. Entity extends `EventSourcedEntity<State, Event>`.
2. Entity has `@Component(id = "...")`.
3. Events implement one sealed interface.
4. Every event subtype has `@TypeName`.
5. `applyEvent` is pure and replay-safe.
6. Validate before persisting.
7. Command handlers never mutate entity state directly.
8. External side effects do not belong inside the entity.
9. Use `EventSourcedEntityContext` when `emptyState()` needs the entity id.
10. Tests must cover success, invalid input, and no-op or idempotent cases.

## Decision guide

Choose one of these modes before coding:

### 1. Edge-facing entity
Use when an endpoint calls the entity directly.

Typical behavior:
- explicit validation
- `effects().error(...)` for business rejection
- useful success reply, often current state
- endpoint translates `CommandException` to HTTP response

Repository example:
- `ShoppingCartEntity`
- `ShoppingCartEndpoint`

### 2. Downstream/internal entity
Use when a consumer or workflow drives the entity.

Typical behavior:
- malformed input may be rejected
- duplicate or stale commands often become no-ops
- reply type is often `Done`
- one command may emit zero, one, or many events

Repository example:
- `OrderEntity`
- `ShoppingCartCheckoutConsumer`

### 3. Focused doc/example snippet
Use when teaching one concept only.

Prefer a minimal example per topic:
- basic persist + applyEvent
- delete
- TTL
- notifications
- replication/consistent read
- testing

Repository example:
- `ExpiringShoppingCartEntity`

## Final review checklist

Before finishing, verify:
- package split is correct
- component id is stable
- events have `@TypeName`
- `emptyState()` exists and is sensible
- validation happens before persist
- `applyEvent` is pure
- no-op cases do not persist events
- side effects are outside the entity
- endpoints do not expose domain types directly
- tests cover the important behavior

## Response style

When answering coding tasks:
- name the files used or changed
- state which pattern each file demonstrates
- call out any no-op, TTL, delete, replication, or notification behavior explicitly
