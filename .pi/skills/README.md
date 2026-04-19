# Akka Event Sourced Entity Skills

This directory contains AI-focused skills for generating and reviewing Akka Java SDK event sourced entity code.

## Recommended usage

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

## Practical combinations

### New endpoint-facing entity
Load:
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-unit-testing`
- `akka-ese-integration-testing`

### Add TTL to an entity
Load:
- `akka-event-sourced-entities`
- `akka-ese-application-entity`
- `akka-ese-ttl`
- `akka-ese-unit-testing`

### Add live notifications
Load:
- `akka-event-sourced-entities`
- `akka-ese-application-entity`
- `akka-ese-notifications`
- `akka-ese-edge-and-flow-patterns`
- `akka-ese-integration-testing`

### Add replication support
Load:
- `akka-event-sourced-entities`
- `akka-ese-application-entity`
- `akka-ese-replication`
- `akka-ese-unit-testing`

## Repository reference examples

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
