---
name: akka-key-value-entities
description: Orchestrate Akka Java SDK KeyValueEntity work across domain modeling, application entities, edge/internal flow patterns, unit tests, and integration tests. Use when the task spans more than one part of key value entity implementation.
---

# Akka Key Value Entities

Use this as the top-level skill for Akka Java SDK key value entity work.

## Goal

Generate or review key value entity code that is:
- correct for Akka SDK 3.4+
- cleanly split across `domain`, `application`, and `api`
- state-replacement based rather than event-sourced
- easy for AI agents to extend
- backed by tests

## Required reading before coding

If these files exist, read them first:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project examples under `src/main/java/**/application/*Entity.java`
- matching domain files under `src/main/java/**/domain/*`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../../../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`
- `../../../src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../../../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../../../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-kve-domain-modeling`
  - state, commands, validators, command-to-state handlers, business-decision helpers
- `akka-kve-application-entity`
  - core entity class, effects, reads, writes, and delete behavior
- `akka-kve-ttl`
  - `expireAfter(...)` and automatic expiry
- `akka-kve-notifications`
  - `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-kve-replication`
  - strongly consistent reads and replication filters
- `akka-kve-edge-and-flow-patterns`
  - endpoint-facing entities, downstream/internal entities, consumers, idempotent no-op behavior
- `akka-kve-unit-testing`
  - `KeyValueEntityTestKit` and entity unit tests
- `akka-kve-integration-testing`
  - `TestKitSupport`, endpoint tests, consumer-driven flows

If the task spans multiple areas, use this skill plus the relevant companion skills.

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

1. Entity extends `KeyValueEntity<State>`.
2. Entity has `@Component(id = "...")`.
3. `emptyState()` should return a sensible default.
4. Command handlers update state only with `effects().updateState(...)`.
5. Validate before updating state.
6. Domain code returns updated state or no-op decisions, not Akka effects.
7. Command handlers never mutate entity state directly.
8. External side effects do not belong inside the entity.
9. Use `KeyValueEntityContext` when `emptyState()` needs the entity id.
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
- `DraftCartEntity`
- `DraftCartEndpoint`

### 2. Downstream/internal entity
Use when a consumer or workflow drives the entity.

Typical behavior:
- malformed input may be rejected
- duplicate or stale commands often become no-ops
- reply type is often `Done`
- one command may replace the full current state in one update

Repository example:
- `PurchaseOrderEntity`
- `DraftCartCheckoutConsumer`

### 3. Focused doc/example snippet
Use when teaching one concept only.

Prefer a minimal example per topic:
- basic update + read
- delete
- TTL
- notifications
- replication/consistent read
- testing

Repository example:
- `ExpiringDraftCartSessionEntity`

## Final review checklist

Before finishing, verify:
- package split is correct
- component id is stable
- `emptyState()` exists and is sensible
- validation happens before update
- no-op cases do not update state
- side effects are outside the entity
- endpoints do not expose domain types directly
- tests cover the important behavior

## Response style

When answering coding tasks:
- name the files used or changed
- state which pattern each file demonstrates
- call out any no-op, TTL, delete, replication, or notification behavior explicitly
- if comparing with event sourced entities, state that KVE persists the latest state rather than events
