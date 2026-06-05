---
name: akka-ese-replication
description: Implement Akka Java SDK EventSourcedEntity replication patterns including ReadOnlyEffect vs Effect reads, strongly consistent reads, and replication filters. Use when adding multi-region behavior to event sourced entities.
---

# Akka ESE Replication

Use this skill when the task involves replicated event sourced entities or multi-region behavior.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `../examples/akka-components/src/main/java/ai/first/application/OrderEntity.java`
- `../examples/akka-components/src/main/java/ai/first/api/OrderEndpoint.java`
- `../examples/akka-components/src/test/java/ai/first/application/OrderEntityTest.java`
- `../examples/akka-components/src/test/java/ai/first/application/OrderEndpointIntegrationTest.java`

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

## Generated SaaS checks

When this feature supports a generated SaaS capability, verify:
- capability reason and target tenant/customer scope are explicit;
- expired, stale, replicated, deleted, or notification-only states have safe denial/no-op behavior;
- surface/API/tool/realtime consumers receive scoped, redacted DTOs only;
- idempotency and retry behavior are tested;
- audit/work-trace requirements are recorded or explicitly delegated to the caller capability.


## Anti-patterns

Avoid:
- using strongly consistent reads everywhere without reason
- adding replication filter code when the task does not need multi-region behavior
- hiding read consistency differences from the code structure
