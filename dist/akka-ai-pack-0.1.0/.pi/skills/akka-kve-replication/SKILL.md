---
name: akka-kve-replication
description: Implement Akka Java SDK KeyValueEntity replication patterns including ReadOnlyEffect vs Effect reads, strongly consistent reads, and replication filters. Use when adding multi-region behavior to key value entities.
---

# Akka KVE Replication

Use this skill when the task involves replicated key value entities or multi-region behavior.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `../../../src/main/java/com/example/application/PurchaseOrderEntity.java`
- `../../../src/main/java/com/example/api/PurchaseOrderEndpoint.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEndpointIntegrationTest.java`

## Read patterns

Use:
- `ReadOnlyEffect<T>` for normal read-only handlers
- `Effect<T>` for strongly consistent reads that should route to the primary region

Repository example:
- `PurchaseOrderEntity.getOrder()`
- `PurchaseOrderEntity.getOrderConsistent()`

## Replication filter pattern

When the task explicitly needs replication filtering:
- add `@EnableReplicationFilter`
- use `effects().updateReplicationFilter(...)`
- validate region input first
- reply after the filter update effect

Repository example:
- `PurchaseOrderEntity.includeRegion(...)`
- `PurchaseOrderEntity.excludeRegion(...)`

## Endpoint guidance

If exposing replication behavior through HTTP:
- create explicit request/response records
- validate incoming requests
- map outcomes to clear API responses

Repository example:
- `PurchaseOrderEndpoint.includeRegion(...)`
- `PurchaseOrderEndpoint.excludeRegion(...)`
- `PurchaseOrderEndpoint.getOrderConsistent()`

## Testing guidance

Cover:
- normal reads
- strongly consistent read command shape
- region include/exclude commands
- request validation at endpoint or entity layer as appropriate

Repository examples:
- `PurchaseOrderEntityTest`
- `PurchaseOrderEndpointIntegrationTest`

## Anti-patterns

Avoid:
- using strongly consistent reads everywhere without reason
- adding replication filter code when the task does not need multi-region behavior
- hiding read consistency differences from the code structure
