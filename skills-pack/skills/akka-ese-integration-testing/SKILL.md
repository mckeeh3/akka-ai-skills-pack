---
name: akka-ese-integration-testing
description: Write integration tests for Akka event sourced entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of EventSourcedEntity interactions.
---

# Akka ESE Integration Testing

Use this skill for service-level tests involving Event Sourced Entities.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or manual-test reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or manual-failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of manual findings through `../docs/manual-test-reconciliation.md`.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/manual-test-reconciliation.md` when tests are part of a manual/runtime readiness claim or remediation loop

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
