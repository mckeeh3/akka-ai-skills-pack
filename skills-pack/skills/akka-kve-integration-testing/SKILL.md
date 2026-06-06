---
name: akka-kve-integration-testing
description: Write integration tests for Akka key value entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of KeyValueEntity interactions.
---

# Akka KVE Integration Testing

Use this skill for service-level tests involving Key Value Entities.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

Read these first if present:

- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/akka-entity-testing-shared-patterns.md`
- `../references/akka-entity-integration-testing-patterns.md`

## Key-value specifics

Cover Key Value Entity behavior that can only be proven through persisted current state and state-change reactions:

1. endpoint or component command updates the expected durable state;
2. read paths expose the latest authorized state, not direct fixture objects;
3. duplicate/idempotent commands preserve state and do not trigger duplicate consequential side effects;
4. consumers or views react to the intended state changes and handle delete/no-op behavior safely;
5. authorization denials and validation failures avoid accidental state mutation;
6. audit/work-trace expectations preserve correlation ids, policy/evidence refs, state-change summary, and actor scope when consequential.

## Review checklist

Before finishing, verify:

- shared patterns in `../references/akka-entity-integration-testing-patterns.md` are applied;
- endpoint tests use `httpClient` and endpoint DTOs;
- internal checks use `componentClient` where appropriate;
- async projections/consumers wait explicitly;
- state update, idempotency, validation, delete/no-op, and denial behavior are asserted.
