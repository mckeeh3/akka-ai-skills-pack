---
name: akka-kve-integration-testing
description: Write integration tests for Akka key value entity flows using TestKitSupport, httpClient, and componentClient. Use for endpoint round trips, consumer-driven flows, and end-to-end validation of KeyValueEntity interactions.
---

# Akka KVE Integration Testing

Use this skill for service-level tests involving Key Value Entities.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or runtime-validation reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or runtime-validation failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of runtime-validation findings through `../docs/runtime-validation-reconciliation.md`.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/runtime-validation-reconciliation.md` when tests are part of a runtime-validation readiness claim or remediation loop

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
