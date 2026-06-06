---
name: akka-ese-integration-testing
description: Write integration tests for Akka event sourced entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of EventSourcedEntity interactions.
---

# Akka ESE Integration Testing

Use this skill for service-level tests involving Event Sourced Entities.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:

- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/akka-entity-testing-shared-patterns.md`
- `../references/akka-entity-integration-testing-patterns.md`

## Event-sourced specifics

Cover Event Sourced Entity behavior that can only be proven through persisted events and replayable history:

1. endpoint or component command persists the expected event sequence;
2. read paths expose state reconstructed from events, not direct fixture state;
3. duplicate/idempotent commands do not append duplicate consequential events;
4. consumer or view flows react to the intended event type and ignore irrelevant events;
5. authorization denials and validation failures avoid accidental event persistence;
6. audit/work-trace expectations preserve event ids, correlation ids, policy/evidence refs, and actor scope when consequential.

## Review checklist

Before finishing, verify:

- shared patterns in `../references/akka-entity-integration-testing-patterns.md` are applied;
- endpoint tests use `httpClient` and endpoint DTOs;
- internal checks use `componentClient` where appropriate;
- async projections/consumers wait explicitly;
- event history, idempotency, validation, and denial behavior are asserted.
