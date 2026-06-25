---
name: akka-kve-ttl
description: Add automatic expiry to Akka Java SDK KeyValueEntity code using expireAfter(...). Use when implementing or reviewing TTL behavior and tests for key value entities.
---

# Akka KVE TTL

Use this skill when a key value entity should expire automatically after a period without further writes.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

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

## Target-project pattern

Use a target-project KVE command that actually writes state and attach TTL on that update effect. The current skills-pack examples do not provide a reusable KVE TTL fixture, so do not cite repository TTL behavior unless the target code includes `expireAfter(...)`.

## Testing guidance

Use `KeyValueEntityTestKit` and assert:
- reply value
- `stateWasUpdated()`
- `result.getExpireAfter()`
- resulting state

Target-project test pattern:
- for the KVE command that attaches TTL, assert `getExpireAfter()` alongside reply and state-update expectations

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Anti-patterns

Avoid:
- putting TTL logic in domain state helpers
- assuming TTL remains active after later writes that omit `expireAfter(...)`
- adding TTL to read-only handlers
