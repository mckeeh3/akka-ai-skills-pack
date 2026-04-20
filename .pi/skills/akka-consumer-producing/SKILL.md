---
name: akka-consumer-producing
description: Implement Akka Java SDK Consumers that produce to broker topics or service streams, including metadata, filtering, and public event mapping.
---

# Akka Consumer Producing Pattern

Use this skill when a Consumer republishes, transforms, or filters messages into a topic or service stream.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../../src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`

## Use this pattern when

- internal events should be republished to a broker topic
- internal events should be transformed into a public service-stream contract
- only selected message types should be published
- downstream ordering depends on the original entity or workflow id

## Topic production rules

1. Add `@Produce.ToTopic("topic-name")`.
2. Return `effects().produce(payload)` or `effects().produce(payload, metadata)`.
3. Add `Metadata.EMPTY.add("ce-subject", id)` when per-entity ordering or downstream routing depends on the source id.
4. Use `effects().ignore()` for event types that should not be published.

## Service-stream rules

1. Add `@Produce.ServiceStream(id = "public_stream_id")`.
2. Add `@Acl(allow = @Acl.Matcher(service = "*"))` or a narrower service ACL.
3. Prefer a public message contract instead of leaking internal event types unchanged.
4. Filter internal-only events explicitly.

## Repository examples

- `ShoppingCartEventsToTopicConsumer`
  - republishes internal shopping-cart events to a topic
  - preserves the cart id through `ce-subject`
- `ShoppingCartPublicEventsConsumer`
  - maps internal shopping-cart events to a public service-stream contract
  - ignores deletion events from the public stream
- `ReviewWorkflowTopicConsumer`
  - publishes only completed workflow updates to a topic

## Review checklist

Before finishing, verify:
- the correct produce annotation is used
- produced payloads are the intended public contract
- `ce-subject` metadata is included when needed
- ignored messages are intentional and explicit
- `@Acl` is present for service streams
