---
name: akka-ese-replication
description: Implement Akka Java SDK EventSourcedEntity replication patterns including ReadOnlyEffect vs Effect reads, strongly consistent reads, and replication filters. Use when adding multi-region behavior to event sourced entities.
---

# Akka ESE Replication

Use this skill when the task involves replicated event sourced entities or multi-region behavior.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`

## Read patterns

Use:
- `ReadOnlyEffect<T>` for normal read-only handlers
- `Effect<T>` for strongly consistent reads that should route to the primary region

Current repository note:
- The core app example snapshot does not currently include a replicated EventSourcedEntity read-consistency example. Use the Akka SDK docs as the source of truth and name any new example from the target project after adding it.

## Replication filter pattern

When the task explicitly needs replication filtering:
- add `@EnableReplicationFilter`
- use `effects().updateReplicationFilter(...)`
- validate region input first
- reply after the filter update effect

Current repository note:
- The core app example snapshot does not currently include replication-filter handlers. Add target-project examples only when the task explicitly needs multi-region filtering.

## Endpoint guidance

If exposing replication behavior through HTTP:
- create explicit request/response records
- validate incoming requests
- map outcomes to clear API responses

Current repository note:
- The core app example snapshot does not currently expose replication-filter HTTP endpoints. Keep endpoint names target-specific and document the consistency/filtering behavior in the capability contract.

## Testing guidance

Cover:
- normal reads
- strongly consistent read command shape
- region include/exclude commands
- request validation at endpoint or entity layer as appropriate

Testing note:
- The core app example snapshot does not currently include replication-specific tests. Add focused target-project tests for strongly consistent read command shape, region validation, and endpoint mapping when implementing this pattern.

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
