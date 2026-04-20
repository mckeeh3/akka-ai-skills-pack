---
name: akka-consumers
description: Orchestrate Akka Java SDK Consumer work across entity/workflow/topic sources, producing patterns, and integration testing. Use when the task spans more than one consumer concern.
---

# Akka Consumers

Use this as the top-level skill for Akka Java SDK Consumer work.

## Goal

Generate or review Consumer code that is:
- correct for Akka SDK 3.4+
- explicit about the source being consumed
- safe for at-least-once delivery and duplicate redelivery
- clear about whether the consumer ignores, completes, or produces
- backed by executable examples and tests

## Required reading before coding

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `akka-context/sdk/dev-best-practices.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/running-locally.html.md`
- existing project consumers under `src/main/java/**/application/*Consumer.java`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../../src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../../../docs/consumer-reference.md`
- `../../../src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-consumer-from-event-sourced-entity`
  - consuming journal events from an event sourced entity
  - optional `@SnapshotHandler`
- `akka-consumer-from-key-value-entity`
  - consuming current-state updates and optional deletes from a key value entity
- `akka-consumer-from-workflow`
  - consuming workflow state transitions and optional deletes
- `akka-consumer-from-topic`
  - consuming CloudEvents or raw `byte[]` messages from a broker topic
- `akka-consumer-from-service-stream`
  - consuming public streams from another Akka service
- `akka-consumer-producing`
  - producing to a topic or service stream, including metadata and ACLs
- `akka-consumer-testing`
  - TestKit topic/workflow/entity incoming messages and outgoing topic assertions

## Core rules

1. A consumer extends `akka.javasdk.consumer.Consumer` and has `@Component(id = "...")`.
2. Choose exactly one source annotation that matches the upstream:
   - `@Consume.FromEventSourcedEntity`
   - `@Consume.FromKeyValueEntity`
   - `@Consume.FromWorkflow`
   - `@Consume.FromTopic`
   - `@Consume.FromServiceStream` when subscribing to another service
3. Handlers return one of:
   - `effects().done()`
   - `effects().ignore()`
   - `effects().produce(...)`
4. Consumers must tolerate duplicate delivery. Make downstream calls idempotent.
5. Use `messageContext().eventSubject()` when the entity or message subject is needed.
6. Prefer explicit filtering with `ignore()` over hidden conditionals.
7. Put business state changes in entities or workflows, not in mutable consumer fields.
8. For per-entity broker ordering, include `ce-subject` metadata when producing.
9. For service streams, add `@Acl` so other Akka services can subscribe.
10. Test topic flows with `TestKitSupport` and mocked incoming/outgoing messages.

## Decision guide

### 1. Downstream reaction to event sourced facts
Use `akka-consumer-from-event-sourced-entity`.

Repository examples:
- `ShoppingCartCheckoutConsumer`
- `ShoppingCartEventsToTopicConsumer`
- `ShoppingCartPublicEventsConsumer`

### 2. Downstream reaction to key value state snapshots
Use `akka-consumer-from-key-value-entity`.

Repository example:
- `DraftCartCheckoutConsumer`

### 3. Downstream reaction to workflow progress
Use `akka-consumer-from-workflow`.

Repository example:
- `ReviewWorkflowTopicConsumer`

### 4. Command ingestion from a broker topic
Use `akka-consumer-from-topic`.

Repository example:
- `ShoppingCartCommandsTopicConsumer`

### 5. Service-to-service subscription from another Akka service
Use `akka-consumer-from-service-stream`.

Repository references:
- `ShoppingCartPublicEventsConsumer` for the producer side
- `docs/consumer-reference.md` for the subscriber-side snippet

### 6. Event publication to topics or service streams
Use `akka-consumer-producing`.

Repository examples:
- `ShoppingCartEventsToTopicConsumer`
- `ShoppingCartPublicEventsConsumer`
- `ReviewWorkflowTopicConsumer`

### 7. Integration and eventing tests
Use `akka-consumer-testing`.

Repository examples:
- `ShoppingCartCommandsTopicConsumerIntegrationTest`
- `ReviewWorkflowTopicConsumerIntegrationTest`
- `ShoppingCartCheckoutConsumerIntegrationTest`
- `DraftCartCheckoutConsumerIntegrationTest`

## Final review checklist

Before finishing, verify:
- the correct `@Consume` source annotation is used
- the handler input type matches the source semantics
- `effects().ignore()` is explicit for unhandled events or states
- downstream side effects are idempotent under redelivery
- `ce-subject` metadata is added when producing ordered per-entity topic messages
- `@Acl` is present for `@Produce.ServiceStream`
- tests use the right TestKit incoming/outgoing message hooks

## Response style

When answering coding tasks:
- name the exact consumer source
- state whether the consumer is pure side-effecting or a producer
- call out the idempotency strategy
- list the concrete example files used as references
