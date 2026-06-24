---
name: akka-ese-replication
description: Implement Akka Java SDK EventSourcedEntity replication patterns including ReadOnlyEffect vs Effect reads, strongly consistent reads, and replication filters. Use when adding multi-region behavior to event sourced entities.
---

# Akka ESE Replication

Use this skill when the task involves replicated event sourced entities or multi-region behavior.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`

## Read patterns

Use:
- `ReadOnlyEffect<T>` for normal read-only handlers
- `Effect<T>` for strongly consistent reads that should route to the primary region

Current repository note:
- The SaaS Foundation App example snapshot does not currently include a replicated EventSourcedEntity read-consistency example. Use the Akka SDK docs as the source of truth and name any new example from the target project after adding it.

## Replication filter pattern

When the task explicitly needs replication filtering:
- add `@EnableReplicationFilter`
- use `effects().updateReplicationFilter(...)`
- validate region input first
- reply after the filter update effect

Current repository note:
- The SaaS Foundation App example snapshot does not currently include replication-filter handlers. Add target-project examples only when the task explicitly needs multi-region filtering.

## Endpoint guidance

If exposing replication behavior through HTTP:
- create explicit request/response records
- validate incoming requests
- map outcomes to clear API responses

Current repository note:
- The SaaS Foundation App example snapshot does not currently expose replication-filter HTTP endpoints. Keep endpoint names target-specific and document the consistency/filtering behavior in the capability contract.

## Testing guidance

Cover:
- normal reads
- strongly consistent read command shape
- region include/exclude commands
- request validation at endpoint or entity layer as appropriate

Testing note:
- The SaaS Foundation App example snapshot does not currently include replication-specific tests. Add focused target-project tests for strongly consistent read command shape, region validation, and endpoint mapping when implementing this pattern.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Anti-patterns

Avoid:
- using strongly consistent reads everywhere without reason
- adding replication filter code when the task does not need multi-region behavior
- hiding read consistency differences from the code structure
