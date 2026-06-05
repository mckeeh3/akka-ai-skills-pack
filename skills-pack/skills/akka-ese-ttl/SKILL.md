---
name: akka-ese-ttl
description: Add automatic expiry to Akka Java SDK EventSourcedEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for event sourced entities.
---

# Akka ESE TTL

Use this skill when an event sourced entity should expire automatically after a period without further writes.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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

## Repository example

See:
- `ExpiringAgentDefinitionEntity.addItem(...)`

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

Pattern reference:
- target-project TTL entity tests; no dedicated expiring entity exists in the current curated examples

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
