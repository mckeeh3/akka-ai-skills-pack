---
name: akka-consumer-from-topic
description: Implement Akka Java SDK Consumers that subscribe to broker topics, handle CloudEvent metadata, and forward messages into Akka components.
---

# Akka Consumer from Topic

Use this skill when a Consumer ingests messages from Kafka or Google Cloud Pub/Sub topics.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`

## Use this pattern when

- external systems push commands or facts through a broker topic
- the consumer should route topic messages into entities or workflows
- CloudEvent subject metadata identifies the entity key
- raw bytes handling is needed for non-JSON messages

## Core pattern

1. Annotate the class with `@Consume.FromTopic("topic-name")`.
2. Add one handler per message type when consuming CloudEvents with typed payloads.
3. Use `messageContext().eventSubject()` when the `ce-subject` identifies the target entity.
4. Return `effects().done()` after a successful downstream call.
5. For raw binary input, accept `byte[]`.

## CloudEvent rules

For typed JSON messages, upstream publishers should provide:
- `Content-Type = application/json`
- `ce-specversion = 1.0`
- `ce-type = fully qualified class name`
- `ce-subject` when routing or per-entity ordering is needed

## Repository example

- `ShoppingCartCommandsTopicConsumer`
  - consumes typed command messages from `shopping-cart-commands`
  - uses `ce-subject` as the shopping-cart id
  - forwards commands to `ShoppingCartEntity`

## Testing rules

- configure `withTopicIncomingMessages("topic-name")`
- publish typed messages through `testKit.getMessageBuilder()` when metadata is needed
- assert the downstream state or outgoing produced messages

## Review checklist

Before finishing, verify:
- `@Consume.FromTopic` uses the intended topic name
- handler parameter types match topic payload types
- `ce-subject` usage is explicit when routing depends on it
- raw `byte[]` is used only for binary payloads
- downstream calls are idempotent under redelivery
