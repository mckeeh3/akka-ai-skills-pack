---
name: akka-consumer-testing
description: Write Akka Java SDK Consumer integration tests using TestKitSupport, mocked incoming entity/workflow/topic messages, and outgoing topic assertions.
---

# Akka Consumer Testing

Use this skill when testing Consumer behavior.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or manual-test reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or manual-failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of manual findings through `../docs/manual-test-reconciliation.md`.

## Capability-first test role

Consumer tests should verify reactive capability behavior, not only that a handler runs. Cover authority/provenance, tenant/customer scope, idempotent duplicate delivery, retry versus terminal denial/no-op semantics, scoped publication, and audit/work-trace effects for protected or consequential reactions.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/manual-test-reconciliation.md` when tests are part of a manual/runtime readiness claim or remediation loop
- `akka-context/sdk/consuming-producing.html.md`

## Test modes

### 1. End-to-end through endpoint or component flow
Use when the consumer is triggered by the real upstream component in the same service.

Pattern references:
- current curated consumer shape: `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`
- current curated flow test shape: `../examples/akka-components/src/test/java/ai/first/application/foundation/workstream/WorkstreamEventBackboneServiceTest.java`

Pattern:
- trigger the upstream entity through `httpClient` or `componentClient`
- poll or await the downstream result
- assert final state

### 2. Mocked incoming topic messages
Use when the consumer source is a broker topic.

Pattern reference:
- target-project topic consumer integration test; the current curated tree does not include a broker-topic fixture class

Pattern:
- configure `withTopicIncomingMessages(...)`
- optionally configure `withTopicOutgoingMessages(...)`
- publish messages with `testKit.getMessageBuilder()` when metadata is needed
- assert downstream entity state and/or outgoing topic messages

### 3. Mocked incoming workflow or entity updates
Use when isolating the consumer from workflow or entity change streams. If the upstream workflow/entity exists in the same generated service and the named feature depends on that reaction, also include an end-to-end local/TestKit path that triggers the real upstream component and observes the downstream result before calling the feature complete.

Repository example:
- a domain-specific workflow topic consumer integration test

Pattern:
- configure `withWorkflowIncomingMessages(...)`, `withEventSourcedEntityIncomingMessages(...)`, or `withKeyValueEntityIncomingMessages(...)`
- publish the state/event with a subject id
- assert the produced side effect

## Rules

1. Use `TestKitSupport`.
2. Use the matching TestKit source hook for the consumer source.
3. Use outgoing topic assertions when the consumer produces.
4. Use `Awaitility` or polling when the flow is eventually consistent.
5. Clear reused outgoing topics between tests when one suite contains multiple cases.
6. Include tenant/customer, `ce-subject`, correlation, producer provenance, and authorization/approval metadata in test messages when the capability depends on them.
7. Test duplicate delivery by publishing the same event/message twice and asserting idempotent downstream state, no duplicate unsafe side effects, or stable dedupe behavior.
8. Test invalid, unauthorized, stale, and cross-tenant messages as terminal audited denials/no-ops when that is the intended semantics.
9. Test transient dependency failures only when retry semantics are part of the capability contract.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- mocked incoming messages are treated as test isolation only, not proof of user-facing/runtime completion when a real upstream path exists
- TestKit settings include every mocked topic or incoming component source required by the test
- messages include `ce-subject` metadata when the consumer logic depends on it
- tests assert observable behavior, not implementation details
- eventual consistency is handled explicitly
- protected reactive capabilities cover success, forbidden/cross-tenant, idempotent duplicate, audit/trace, and retry/no-op behavior
