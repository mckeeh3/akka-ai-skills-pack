---
name: akka-event-sourced-entities
description: Orchestrate Akka Java SDK EventSourcedEntity work across domain modeling, application entities, edge/internal flow patterns, unit tests, and integration tests. Use when the task spans more than one part of event sourced entity implementation.
---

# Akka Event Sourced Entities

Use this as the top-level skill for Akka Java SDK event sourced entity work.

Use it only after the relevant backend capability contract is clear enough, or as part of a decomposition task that is explicitly deciding whether an event-sourced entity should carry a capability. For broad product work, route through `capability-first-backend` and `akka-solution-decomposition` before implementing entities.

## Goal

Generate or review event sourced entity code that is:
- correct for Akka SDK 3.4+
- cleanly split across `domain`, `application`, and `api`
- replay-safe
- aligned to a named capability's AuthContext, scope, idempotency, audit/trace, and approval semantics
- easy for AI agents to extend
- backed by tests

## AI-first substrate role

In AI-first SaaS work, use Event Sourced Entities for audit-grade objects whose history matters: goals, policy documents and clauses, approval/decision records, exceptions, precedents, work traces, policy invocations, outcome links, and consequential domain facts.

Prefer event sourcing when the app must answer what changed, who or what authorized it, which evidence or policy version applied, how a decision evolved, or why an agent/workflow action is explainable later. Do not use an ESE just because an object is important; use a Key Value Entity when only replaceable current state is required.

Pair AI-first ESEs with:
- `akka-workflows` for long-running plan, approval, exception, rollback, or simulation flows
- `akka-agents` for bounded recommendations, explanations, evaluations, and policy proposal drafting
- `akka-views` for command centers, decision queues, policy catalogs, audit search, and outcome dashboards
- `akka-consumers` for trace enrichment, notifications, and downstream publication

## Required reading before coding

If these files exist, read them first:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project examples under `src/main/java/**/application/*Entity.java`
- matching domain files under `src/main/java/**/domain/*`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../examples/akka-components/src/main/java/com/example/application/ShoppingCartEntity.java`
- `../examples/akka-components/src/main/java/com/example/application/OrderEntity.java`
- `../examples/akka-components/src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `../examples/akka-components/src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../examples/akka-components/src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../examples/akka-components/src/main/java/com/example/api/OrderEndpoint.java`
- `../examples/akka-components/src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../examples/akka-components/src/test/java/com/example/application/OrderEntityTest.java`
- `../examples/akka-components/src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

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

Use the target project's selected Java base package. For this core-app-first repository and downstream forks, preserve the existing package when present; default to `ai.first` only when the user accepts or defers the package choice. Reference examples may use `com.example`, but generated application code must not use template package placeholders or `com.example` unless explicitly selected.

Typical layer paths are:
- `<base>.domain`
- `<base>.application`
- `<base>.api`

Rules:
- domain has no Akka effects
- application contains entities and consumers/workflows/timed actions
- api exposes request/response types and maps domain to API shapes

## Capability-first rules

An Event Sourced Entity command or read handler is an implementation surface for a named capability, not the product boundary by itself.

Before coding command/query handlers, identify:
- capability id/name and class: command, approval, proposal, trace/audit, or read/evidence;
- authorized actors/callers and where AuthContext/scope is enforced: endpoint, workflow, agent tool, timer, consumer, or internal caller;
- tenant/customer scope keys carried by the command/query and persisted events;
- input validation, idempotency key/correlation id, duplicate/no-op semantics, and denial shape;
- audit/work-trace obligations and whether domain events themselves are the audit-grade facts;
- selected exposure surfaces, including whether any command/read is safe to expose as an agent component tool.

Do not expose every entity method as an agent tool just because Akka supports component tools. Prefer scoped read/evidence handlers for tool exposure; side-effecting ESE commands require explicit capability permission, idempotency, audit, and approval policy.

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
- `ShoppingCartEntity`
- `ShoppingCartEndpoint`

### 2. Downstream/internal entity
Use when a consumer or workflow drives the entity.

Typical behavior:
- caller carries capability authority, correlation id, and tenant/customer scope
- malformed input may be rejected
- duplicate or stale commands often become idempotent no-ops
- reply type is often `Done`
- one command may emit zero, one, or many audit-grade facts

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
- entity handlers map to named capabilities rather than raw CRUD by default
- events have `@TypeName`
- `emptyState()` exists and is sensible
- validation and capability scope checks happen before persist at the correct boundary
- `applyEvent` is pure
- no-op/idempotent cases do not persist events
- audit/trace obligations are represented by events or caller-side audit records
- side effects are outside the entity
- endpoints and tools do not expose domain types directly
- agent tool exposure is deliberate and preserves auth/scope, approval, audit, and idempotency
- tests cover the important capability behavior

## Response style

When answering coding tasks:
- name the files used or changed
- state which pattern each file demonstrates
- call out any no-op, TTL, delete, replication, or notification behavior explicitly
