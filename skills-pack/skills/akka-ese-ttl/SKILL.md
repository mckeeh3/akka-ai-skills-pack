---
name: akka-ese-ttl
description: Add automatic expiry to Akka Java SDK EventSourcedEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for event sourced entities.
---

# Akka ESE TTL

Use this skill when an event sourced entity should expire automatically after a period without further writes.

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
- `../examples/akka-components/src/main/java/com/example/application/ExpiringShoppingCartEntity.java`
- `../examples/akka-components/src/main/java/com/example/domain/ExpiringShoppingCart.java`
- `../examples/akka-components/src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

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

## Repository example

See:
- `ExpiringShoppingCartEntity.addItem(...)`

This shows:
- validation error on blank input
- persist of one event
- `.expireAfter(Duration.ofDays(30))`
- reply with `Done`

## Testing guidance

Use `EventSourcedTestKit` and assert:
- reply value
- persisted event
- `result.getExpireAfter()`
- resulting state

Repository example:
- `ExpiringShoppingCartEntityTest`

## Generated SaaS checks

When this feature supports a generated SaaS capability, verify:
- capability reason and target tenant/customer scope are explicit;
- expired, stale, replicated, deleted, or notification-only states have safe denial/no-op behavior;
- surface/API/tool/realtime consumers receive scoped, redacted DTOs only;
- idempotency and retry behavior are tested;
- audit/work-trace requirements are recorded or explicitly delegated to the caller capability.


## Anti-patterns

Avoid:
- putting TTL logic in domain state or event handlers
- assuming TTL remains active after later persists that omit `expireAfter(...)`
- adding TTL to read-only handlers
