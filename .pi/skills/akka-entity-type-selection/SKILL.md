---
name: akka-entity-type-selection
description: Decide whether an Akka Java SDK use case should be implemented as an EventSourcedEntity or a KeyValueEntity, then load the matching skill suite. Use when the task starts from requirements rather than a fixed entity type.
---

# Akka Entity Type Selection

Use this skill when the user describes behavior but has not yet chosen between:
- `EventSourcedEntity`
- `KeyValueEntity`

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/OrderEntity.java`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../references/akka-entity-comparison.md`

## Decision rule

Choose the simplest model that preserves the required business semantics.

### Prefer Event Sourced Entity when
- history of changes matters
- downstream consumers need fact-level event streams
- one command may emit several business facts
- replay/audit/debug value matters
- the user talks about events explicitly
- views or integrations are naturally driven by durable domain events

Repository examples:
- `ShoppingCartEntity`
- `OrderEntity`

### Prefer Key Value Entity when
- only latest state matters
- audit/history is not required
- each successful write can be represented as replacing current state
- the domain is simpler as snapshots than as events
- the user wants a simpler CRUD-like state model with strong consistency

Repository examples:
- `DraftCartEntity`
- `PurchaseOrderEntity`

## Important comparison points

### Storage model
- ESE: persist events, rebuild state from event history
- KVE: persist latest state only

### Domain helpers
- ESE: command helpers usually return 0..N events
- KVE: command helpers usually return updated state or no-op

### Replay logic
- ESE: needs pure `applyEvent`
- KVE: no event application step

### Teaching examples
- ESE is better when demonstrating event semantics, final events before delete, or multi-event facts
- KVE is better when demonstrating direct state replacement, simpler update flows, or snapshot-style thinking

## Load the matching suite

If you choose event sourced, load:
- `akka-event-sourced-entities`

If you choose key value, load:
- `akka-key-value-entities`

Then load the focused companion skills for domain, application, testing, TTL, notifications, replication, or flow patterns.

## Response pattern

When deciding, state explicitly:
1. chosen entity type
2. why the other type is less suitable here
3. whether history/audit/replay is required
4. which skill suite should be loaded next

## Anti-patterns

Avoid:
- choosing ESE by default when latest-state semantics are enough
- choosing KVE when durable fact history is central to the business model
- mixing event-sourced vocabulary into KVE implementations
- flattening a clearly event-driven process into snapshots without justification
