---
name: akka-key-value-entities
description: Orchestrate Akka Java SDK KeyValueEntity work across domain modeling, application entities, edge/internal flow patterns, unit tests, and integration tests. Use when the task spans more than one part of key value entity implementation.
---

# Akka Key Value Entities

Use this as the top-level skill for Akka Java SDK key value entity work.

Use it only after the relevant backend capability contract is clear enough, or as part of a decomposition task that is explicitly deciding whether a key value entity should carry a capability. For broad product work, route through `capability-first-backend` and `akka-solution-decomposition` before implementing entities.

## Goal

Generate or review key value entity code that is:
- correct for Akka SDK 3.4+
- cleanly split across `domain`, `application`, and `api`
- state-replacement based rather than event-sourced
- aligned to a named capability's AuthContext, scope, idempotency, audit/trace, and approval semantics
- easy for AI agents to extend
- backed by tests

## AI-first substrate role

In AI-first SaaS work, use Key Value Entities for replaceable current-state objects where audit-grade history is not required: current preferences, low-risk configuration, draft goal or plan state, agent runtime settings, cached evidence, dashboard filters, notification cursors, and operational summaries.

Prefer KVE when the product needs the latest durable state and can rely on separate durable traces, event-sourced records, workflow history, or authoritative integrations for accountability. Do not use KVE as the only store for consequential decisions, policy commits, approvals, or traces that must preserve temporal history.

Pair AI-first KVEs with:
- `akka-event-sourced-entities` when some related decisions, policies, or trace facts require history
- `akka-workflows` for plan lifecycle, approvals, retries, and exception handling around the current state
- `akka-views` for supervision queues, current-state dashboards, policy lists, and outcome summaries
- `akka-consumers` for asynchronous state enrichment or notifications

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
- `akka-kve-doc-snippets`
  - focused documentation snippets for teaching one KVE concept at a time
- `akka-kve-unit-testing`
  - `KeyValueEntityTestKit` and entity unit tests
- `akka-kve-integration-testing`
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

## Capability-first rules

A Key Value Entity command or read handler is an implementation surface for a named capability, not the product boundary by itself.

Before coding command/query handlers, identify:
- capability id/name and class: current-state command, configuration/preference update, draft update, cursor update, or read/evidence;
- authorized actors/callers and where AuthContext/scope is enforced: endpoint, workflow, agent tool, timer, consumer, or internal caller;
- tenant/customer scope keys stored in the state and carried by commands/queries;
- input validation, idempotency key/correlation id, duplicate/no-op semantics, and denial shape;
- audit/work-trace obligations, especially when a separate event-sourced trace or audit entity must record consequential changes;
- selected exposure surfaces, including whether any command/read is safe to expose as an agent component tool.

Do not expose every entity method as an agent tool just because Akka supports component tools. Prefer scoped read/evidence handlers for tool exposure; side-effecting KVE updates require explicit capability permission, idempotency, audit, and approval policy. Do not use a KVE as the only record for consequential decisions or policy commits that require history.

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
10. Tests must cover success, invalid input, forbidden/scope denial at the caller boundary or entity guard, tenant isolation when scoped ids are present, audit/trace effects where applicable, and no-op or idempotent cases.

## Decision guide

Choose one of these modes before coding:

### 1. Edge-facing entity
Use when an endpoint calls the entity directly.

Typical behavior:
- explicit validation
- AuthContext and tenant/customer scope checked at the endpoint or command boundary
- `effects().error(...)` for business rejection or denied capability when the entity owns the guard
- useful success reply, often current state or a capability response shape
- endpoint translates `CommandException` to HTTP response and records required audit/trace data

Repository example:
- `DraftCartEntity`
- `DraftCartEndpoint`

### 2. Downstream/internal entity
Use when a consumer or workflow drives the entity.

Typical behavior:
- caller carries capability authority, correlation id, and tenant/customer scope
- malformed input may be rejected
- duplicate or stale commands often become idempotent no-ops
- reply type is often `Done`
- one command may replace the full current state in one update and trigger separate audit/trace recording when consequential

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
- entity handlers map to named capabilities rather than raw CRUD by default
- `emptyState()` exists and is sensible
- validation and capability scope checks happen before update at the correct boundary
- no-op/idempotent cases do not update state
- audit/trace obligations are handled by caller-side audit records, notifications, or a separate audit-grade component when needed
- side effects are outside the entity
- endpoints and tools do not expose domain types directly
- agent tool exposure is deliberate and preserves auth/scope, approval, audit, and idempotency
- tests cover the important capability behavior

## Response style

When answering coding tasks:
- name the files used or changed
- state which pattern each file demonstrates
- call out any no-op, TTL, delete, replication, or notification behavior explicitly
- if comparing with event sourced entities, state that KVE persists the latest state rather than events
