---
name: akka-consumer-testing
description: Write Akka Java SDK Consumer integration tests using TestKitSupport, mocked incoming entity/workflow/topic messages, and outgoing topic assertions.
---

# Akka Consumer Testing

Use this skill when testing Consumer behavior.

## Capability-first test role

Consumer tests should verify reactive capability behavior, not only that a handler runs. Cover authority/provenance, tenant/customer scope, idempotent duplicate delivery, retry versus terminal denial/no-op semantics, scoped publication, and audit/work-trace effects for protected or consequential reactions.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`

## Test modes

### 1. End-to-end through endpoint or component flow
Use when the consumer is triggered by the real upstream component in the same service.

Repository examples:
- `WorkstreamEventAttentionConsumerIntegrationTest`
- `WorkstreamEventAttentionConsumerIntegrationTest`

Pattern:
- trigger the upstream entity through `httpClient` or `componentClient`
- poll or await the downstream result
- assert final state

### 2. Mocked incoming topic messages
Use when the consumer source is a broker topic.

Repository example:
- `WorkstreamEventAttentionConsumerIntegrationTest`

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

## Generated SaaS consumer contract

For generated SaaS reactive capabilities, require:
- reactive capability id, event provenance, correlation id, and tenant/customer scope;
- system-principal or service-authority basis for downstream calls;
- idempotency key/dedupe strategy for at-least-once delivery;
- retry, poison, obsolete, forbidden, and no-op behavior;
- scoped/redacted publication when producing topics or service streams;
- audit/work-trace records for side effects, denials, retries, and emitted public events;
- tests for duplicate delivery, cross-tenant/forbidden input, idempotent downstream effects, audit/trace, and surface/realtime/API outcomes where exposed.


## Review checklist

Before finishing, verify:
- mocked incoming messages are treated as test isolation only, not proof of user-facing/runtime completion when a real upstream path exists
- TestKit settings include every mocked topic or incoming component source required by the test
- messages include `ce-subject` metadata when the consumer logic depends on it
- tests assert observable behavior, not implementation details
- eventual consistency is handled explicitly
- protected reactive capabilities cover success, forbidden/cross-tenant, idempotent duplicate, audit/trace, and retry/no-op behavior
