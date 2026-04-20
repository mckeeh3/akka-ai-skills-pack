---
name: akka-ese-replication
description: Implement Akka Java SDK EventSourcedEntity replication patterns including ReadOnlyEffect vs Effect reads, strongly consistent reads, and replication filters. Use when adding multi-region behavior to event sourced entities.
---

# Akka ESE Replication

Use this skill when the task involves replicated event sourced entities or multi-region behavior.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `../../../src/main/java/com/example/application/OrderEntity.java`
- `../../../src/main/java/com/example/api/OrderEndpoint.java`
- `../../../src/test/java/com/example/application/OrderEntityTest.java`
- `../../../src/test/java/com/example/application/OrderEndpointIntegrationTest.java`

## Read patterns

Use:
- `ReadOnlyEffect<T>` for normal read-only handlers
- `Effect<T>` for strongly consistent reads that should route to the primary region

Repository example:
- `OrderEntity.getOrder()`
- `OrderEntity.getOrderConsistent()`

## Replication filter pattern

When the task explicitly needs replication filtering:
- add `@EnableReplicationFilter`
- use `effects().updateReplicationFilter(...)`
- validate region input first
- reply after the filter update effect

Repository example:
- `OrderEntity.includeRegion(...)`
- `OrderEntity.excludeRegion(...)`

## Endpoint guidance

If exposing replication behavior through HTTP:
- create explicit request/response records
- validate incoming requests
- map outcomes to clear API responses

Repository example:
- `OrderEndpoint.includeRegion(...)`
- `OrderEndpoint.excludeRegion(...)`
- `OrderEndpoint.getOrderConsistent()`

## Testing guidance

Cover:
- normal reads
- strongly consistent read command shape
- region include/exclude commands
- request validation at endpoint or entity layer as appropriate

Repository examples:
- `OrderEntityTest`
- `OrderEndpointIntegrationTest`

## Anti-patterns

Avoid:
- using strongly consistent reads everywhere without reason
- adding replication filter code when the task does not need multi-region behavior
- hiding read consistency differences from the code structure
