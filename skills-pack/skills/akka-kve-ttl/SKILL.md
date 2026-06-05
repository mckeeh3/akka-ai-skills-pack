---
name: akka-kve-ttl
description: Add automatic expiry to Akka Java SDK KeyValueEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for key value entities.
---

# Akka KVE TTL

Use this skill when a key value entity should expire automatically after a period without further writes.

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
- `akka-context/sdk/key-value-entities.html.md`

## Core pattern

Attach TTL to a state update effect:
- `effects().updateState(newState).expireAfter(Duration...).thenReply(...)`

Use TTL only on commands that actually update state.

## Rules

- TTL belongs on the entity effect, not in domain code.
- Keep TTL examples small and focused.
- Validate before state update.
- If no state update happens, no TTL is attached.
- A later update without `expireAfter(...)` cancels the TTL.
- If the entity should keep expiring after later writes, each relevant update must include `expireAfter(...)`.

## Repository example

See:
- `DurableNotificationRepositoryEntity.addItem(...)`

## Testing guidance

Use `KeyValueEntityTestKit` and assert:
- reply value
- `stateWasUpdated()`
- `result.getExpireAfter()`
- resulting state

Repository example:
- `DurableNotificationRepositoryEntityTest`

## Generated SaaS checks

When this feature supports a generated SaaS capability, verify:
- capability reason and target tenant/customer scope are explicit;
- expired, stale, replicated, deleted, or notification-only states have safe denial/no-op behavior;
- surface/API/tool/realtime consumers receive scoped, redacted DTOs only;
- idempotency and retry behavior are tested;
- audit/work-trace requirements are recorded or explicitly delegated to the caller capability.


## Anti-patterns

Avoid:
- putting TTL logic in domain state helpers
- assuming TTL remains active after later writes that omit `expireAfter(...)`
- adding TTL to read-only handlers
