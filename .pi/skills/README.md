# Akka Entity Skills

This directory contains AI-focused skills for generating and reviewing Akka Java SDK entity code.

It is split into two parallel suites:
- Event Sourced Entities
- Key Value Entities

If you have requirements but have not yet chosen the entity type, start with:
- `akka-entity-type-selection`

## Event Sourced Entity skills

Start with:
- `akka-event-sourced-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, events, commands, validators, command-to-event logic, or pure replay logic.
- `akka-ese-domain-modeling`

### Application entity core
Use when writing the `EventSourcedEntity` class itself.
- `akka-ese-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-ese-ttl` — `expireAfter(...)` and automatic expiry
- `akka-ese-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-ese-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-ese-edge-and-flow-patterns`

### Testing
Use:
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

## Key Value Entity skills

Start with:
- `akka-key-value-entities`

Then load the focused skill that matches the current task:

### Domain modeling
Use when working on state, commands, validators, command-to-state logic, or pure business-decision helpers.
- `akka-kve-domain-modeling`

### Application entity core
Use when writing the `KeyValueEntity` class itself.
- `akka-kve-application-entity`

### Application entity feature skills
Load these only when the task needs the feature:
- `akka-kve-ttl` — `expireAfter(...)` and automatic expiry
- `akka-kve-notifications` — `NotificationPublisher`, `NotificationStream`, SSE mapping
- `akka-kve-replication` — strong reads, replication filters, `@EnableReplicationFilter`

### Flow selection
Use when deciding how the entity participates in endpoint or internal flows.
- `akka-kve-edge-and-flow-patterns`

### Testing
Use:
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

## Practical combinations

### First decide between ESE and KVE
Load:
- `akka-entity-type-selection`

Then continue with either the ESE or KVE suite.

### New endpoint-facing event sourced entity
Load:
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

### New endpoint-facing key value entity
Load:
- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-application-entity`
- `akka-kve-edge-and-flow-patterns`
- `akka-kve-unit-testing`
- `akka-kve-integration-testing`

### Add TTL to an entity
Load either:
- `akka-ese-application-entity` + `akka-ese-ttl`
- `akka-kve-application-entity` + `akka-kve-ttl`

### Add live notifications
Load either:
- `akka-ese-application-entity` + `akka-ese-notifications`
- `akka-kve-application-entity` + `akka-kve-notifications`

### Add replication support
Load either:
- `akka-ese-application-entity` + `akka-ese-replication`
- `akka-kve-application-entity` + `akka-kve-replication`

## Repository reference examples

### Event sourced entities
Core entities:
- `../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../src/main/java/com/example/application/OrderEntity.java`
- `../src/main/java/com/example/application/ExpiringShoppingCartEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/ShoppingCart.java`
- `../src/main/java/com/example/domain/Order.java`
- `../src/main/java/com/example/domain/ExpiringShoppingCart.java`

Testing examples:
- `../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../src/test/java/com/example/application/OrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

### Key value entities
Core entities:
- `../src/main/java/com/example/application/DraftCartEntity.java`
- `../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../src/main/java/com/example/application/ExpiringDraftCartSessionEntity.java`

Domain examples:
- `../src/main/java/com/example/domain/DraftCart.java`
- `../src/main/java/com/example/domain/PurchaseOrder.java`
- `../src/main/java/com/example/domain/ExpiringDraftCartSession.java`

Testing examples:
- `../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`
