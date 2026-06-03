# Akka Entity Comparison for AI Coding Agents

Use this reference when deciding whether to implement an Akka Java SDK stateful component as an Event Sourced Entity (ESE) or a Key Value Entity (KVE).

## Decision summary

| If the requirement is... | Prefer |
|---|---|
| history and durable fact streams matter | Event Sourced Entity |
| only latest state matters | Key Value Entity |
| one command may produce several business facts | Event Sourced Entity |
| one command naturally computes one new snapshot | Key Value Entity |
| audit/debug/replay value is important | Event Sourced Entity |
| simpler CRUD-like strong consistency is enough | Key Value Entity |

## Core mental model

| Topic | Event Sourced Entity | Key Value Entity |
|---|---|---|
| Storage model | Persist events | Persist latest state |
| Domain write output | 0..N events | updated state or no-op |
| Replay step | `applyEvent(...)` required | no replay step |
| State reconstruction | from event history + snapshots | load latest state |
| Best fit | fact-driven business processes | current-state-driven business processes |

## Entity class shape

| Topic | ESE | KVE |
|---|---|---|
| Base class | `EventSourcedEntity<State, Event>` | `KeyValueEntity<State>` |
| Empty state | `emptyState()` | `emptyState()` |
| Read-only reply | `ReadOnlyEffect<T>` | `ReadOnlyEffect<T>` |
| Write reply | `Effect<T>` | `Effect<T>` |
| State change API | `persist(...)` / `persistAll(...)` | `updateState(...)` |
| Delete API | `deleteEntity()` after final event if needed | `deleteEntity()` |

## Domain modeling differences

| Topic | ESE pattern | KVE pattern |
|---|---|---|
| Main helper output | events | next state |
| Business decision helper | choose minimal fact events | choose next full state |
| Event/state purity | `applyEvent` must be pure | state transition methods must be pure |
| No-op representation | `Optional.empty()` or `List.of()` events | `Optional.empty()` state or boolean no-op |

## Reference examples in this repository

### Event Sourced Entity references
- `src/main/java/com/example/application/ShoppingCartEntity.java`
- `src/main/java/com/example/application/OrderEntity.java`
- `src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `src/main/java/com/example/domain/ShoppingCart.java`
- `src/main/java/com/example/domain/Order.java`

### Key Value Entity references
- `src/main/java/com/example/application/DraftCartEntity.java`
- `src/main/java/com/example/application/PurchaseOrderEntity.java`
- `src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`
- `src/main/java/com/example/domain/DraftCart.java`
- `src/main/java/com/example/domain/PurchaseOrder.java`

## Command handler algorithms

### Event Sourced Entity
1. inspect `currentState()`
2. validate input
3. return `effects().error(...)` if invalid
4. compute 0..N events in domain logic
5. if no-op, reply without persisting
6. if one event, `persist(event)`
7. if many events, `persistAll(events)`
8. reply in `thenReply(...)`

### Key Value Entity
1. inspect `currentState()`
2. validate input
3. return `effects().error(...)` if invalid
4. compute updated state or no-op in domain logic
5. if no-op, reply without `updateState(...)`
6. otherwise `updateState(newState)`
7. reply in `thenReply(...)`

## Delete patterns

| Pattern | ESE | KVE |
|---|---|---|
| Business trace of deletion matters | persist final `Deleted` event, then `deleteEntity()` | usually just `deleteEntity()` |
| Reset instead of delete | persist event(s) leading to empty state if modeled that way | update to empty state |

## TTL patterns

| Pattern | ESE | KVE |
|---|---|---|
| Attach TTL to write | `persist(...).expireAfter(...)` | `updateState(...).expireAfter(...)` |
| Later write without TTL | cancels TTL | cancels TTL |

Repository references:
- ESE: `ExpiringShoppingCartEntity`
- KVE: `ExpiringDraftCartSessionEntity`

## Notifications

| Pattern | ESE | KVE |
|---|---|---|
| Typical notification payload | domain event | notification record or sealed interface |
| Publish timing | after persist succeeds | after update/delete succeeds |
| SSE mapping | map domain events to API records | map notifications to API records |

Repository references:
- ESE: `ShoppingCartEntity`, `ShoppingCartEndpoint`
- KVE: `DraftCartEntity`, `DraftCartEndpoint`

## Replication and consistent reads

| Pattern | ESE | KVE |
|---|---|---|
| Ordinary read | `ReadOnlyEffect<T>` | `ReadOnlyEffect<T>` |
| Strong read from primary | `Effect<T>` | `Effect<T>` |
| Replication filters | `@EnableReplicationFilter` + `updateReplicationFilter(...)` | same |

Repository references:
- ESE: `OrderEntity`
- KVE: `PurchaseOrderEntity`

## Consumers and downstream flows

| Pattern | ESE | KVE |
|---|---|---|
| Consume source | events | updated state / deletes |
| Delete callback | final event or downstream handling | `@DeleteHandler` often useful |
| Typical downstream command style | fact-driven | snapshot-driven / idempotent |

Repository references:
- ESE: `ShoppingCartCheckoutConsumer`
- KVE: `DraftCartCheckoutConsumer`

## Testing differences

| Topic | ESE | KVE |
|---|---|---|
| Unit test kit | `EventSourcedTestKit` | `KeyValueEntityTestKit` |
| Unit result assertions | events, state, errors, TTL | updated/deleted state, errors, TTL |
| Integration style | `TestKitSupport`, `httpClient`, `componentClient` | same |

Repository references:
- ESE tests under `src/test/java/com/example/application/*EntityTest.java`
- KVE tests under `src/test/java/com/example/application/*EntityTest.java`

## Quick choice heuristic

Choose ESE if the user says things like:
- "we need an audit trail"
- "other components should react to facts/events"
- "we need to know exactly what happened over time"
- "a single command may produce multiple domain facts"

Choose KVE if the user says things like:
- "we just need the current state"
- "this is basically a strongly consistent snapshot"
- "history is not important"
- "the simplest model is to replace the latest value"

## Anti-confusion rules for AI agents

1. Do not write `applyEvent(...)` for a KVE.
2. Do not model ESE commands as `updateState(...)` writes.
3. Do not use event-sourced language like "persist event" when writing KVE code.
4. Do not flatten an explicitly event-driven domain into KVE without justification.
5. Do not introduce fake events into KVE code unless they are notification payloads and clearly named as such.
