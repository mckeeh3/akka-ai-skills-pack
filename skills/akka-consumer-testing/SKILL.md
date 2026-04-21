---
name: akka-consumer-testing
description: Write Akka Java SDK Consumer integration tests using TestKitSupport, mocked incoming entity/workflow/topic messages, and outgoing topic assertions.
---

# Akka Consumer Testing

Use this skill when testing Consumer behavior.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `../../../src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

## Test modes

### 1. End-to-end through endpoint or component flow
Use when the consumer is triggered by the real upstream component in the same service.

Repository examples:
- `ShoppingCartCheckoutConsumerIntegrationTest`
- `DraftCartCheckoutConsumerIntegrationTest`

Pattern:
- trigger the upstream entity through `httpClient` or `componentClient`
- poll or await the downstream result
- assert final state

### 2. Mocked incoming topic messages
Use when the consumer source is a broker topic.

Repository example:
- `ShoppingCartCommandsTopicConsumerIntegrationTest`

Pattern:
- configure `withTopicIncomingMessages(...)`
- optionally configure `withTopicOutgoingMessages(...)`
- publish messages with `testKit.getMessageBuilder()` when metadata is needed
- assert downstream entity state and/or outgoing topic messages

### 3. Mocked incoming workflow or entity updates
Use when the consumer source is workflow or entity change streams.

Repository example:
- `ReviewWorkflowTopicConsumerIntegrationTest`

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

## Review checklist

Before finishing, verify:
- TestKit settings include every mocked topic or incoming component source required by the test
- messages include `ce-subject` metadata when the consumer logic depends on it
- tests assert observable behavior, not implementation details
- eventual consistency is handled explicitly
