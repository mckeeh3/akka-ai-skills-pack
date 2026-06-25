---
name: akka-ese-ttl
description: Add automatic expiry to Akka Java SDK EventSourcedEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for event sourced entities.
---

# Akka ESE TTL

Use this skill when an event sourced entity should expire automatically after a period without further writes.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`

## Core pattern

Attach TTL to a persist effect:
- `effects().persist(event).expireAfter(Duration...).thenReply(...)`

Use TTL only on write commands that actually persist events.

## Rules

- TTL belongs on the entity effect, not in domain code.
- Keep TTL examples small and focused.
- Validate before persist.
- If no events are persisted, no TTL is attached.
- A later persist without `expireAfter(...)` cancels the TTL.
- If the entity should keep expiring after later writes, each relevant persist must include `expireAfter(...)`.

## Example guidance

The current curated SaaS Foundation App examples do not include a dedicated expiring EventSourcedEntity fixture. When adding TTL in a target project, keep the example or implementation focused on:
- validation before persist
- persist of the event that activates expiry
- `.expireAfter(Duration...)` on that persist effect
- a clear reply after the TTL-bearing effect

## Testing guidance

Use `EventSourcedTestKit` and assert:
- reply value
- persisted event
- `result.getExpireAfter()`
- resulting state

Pattern reference:
- target-project TTL entity tests; no dedicated expiring entity exists in the current curated examples

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Anti-patterns

Avoid:
- putting TTL logic in domain state or event handlers
- assuming TTL remains active after later persists that omit `expireAfter(...)`
- adding TTL to read-only handlers
