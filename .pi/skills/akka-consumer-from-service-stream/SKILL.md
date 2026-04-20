---
name: akka-consumer-from-service-stream
description: Implement Akka Java SDK Consumers that subscribe to service-to-service streams from another Akka service using @Consume.FromServiceStream.
---

# Akka Consumer from Service Stream

Use this skill when a Consumer subscribes to a stream published by another Akka service in the same Akka project.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../../docs/service-to-service-consumers.md`
- `../../../docs/consumer-reference.md`

## Use this pattern when

- another Akka service publishes a public event stream with `@Produce.ServiceStream`
- this service needs side effects, notifications, or downstream writes based on those public events
- a View is not the right target because the result is not just a query model

## Core pattern

1. On the publishing side, expose a public contract with `@Produce.ServiceStream(id = "...")`.
2. Add `@Acl` on the producer so subscriber services are allowed to connect.
3. On the subscriber side, annotate the consumer with:
   - `@Consume.FromServiceStream(service = "publisher-service", id = "stream_id")`
4. Handle only the published public event contract, not internal producer events.
5. Keep downstream writes idempotent because delivery is at least once.
6. Use `messageContext().eventSubject()` when the publisher included subject metadata.

## Design rules

- Prefer a dedicated public event type instead of reusing internal entity events.
- Filter internal-only events on the producer side.
- Treat the stream id as part of the producer service's public contract.
- If the subscriber only needs a query model, consider a View subscriber instead of a Consumer.

## Repository reference

- `ShoppingCartPublicEventsConsumer`
  - producer-side example that exposes a public service stream
- `docs/service-to-service-consumers.md`
  - dedicated publisher/subscriber reference
- `docs/consumer-reference.md`
  - short consumer overview linking to the dedicated reference

## Review checklist

Before finishing, verify:
- the source annotation is `@Consume.FromServiceStream`
- `service` matches the publishing Akka service name
- `id` matches the producer's `@Produce.ServiceStream(id = "...")`
- subscriber handlers accept only public event types
- producer ACLs allow the intended subscriber services
- downstream actions are idempotent
