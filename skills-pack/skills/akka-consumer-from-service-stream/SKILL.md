---
name: akka-consumer-from-service-stream
description: Implement Akka Java SDK Consumers that subscribe to service-to-service streams from another Akka service using @Consume.FromServiceStream.
---

# Akka Consumer from Service Stream

Use this skill when a Consumer subscribes to a stream published by another Akka service in the same Akka project.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`
- `../docs/service-to-service-consumers.md`
- `../docs/consumer-reference.md`

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

- `WorkstreamEventAttentionConsumer`
  - producer-side example that exposes a public service stream
- `../docs/service-to-service-consumers.md`
  - dedicated publisher/subscriber reference
- `../docs/consumer-reference.md`
  - short consumer overview linking to the dedicated reference

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
- the source annotation is `@Consume.FromServiceStream`
- `service` matches the publishing Akka service name
- `id` matches the producer's `@Produce.ServiceStream(id = "...")`
- subscriber handlers accept only public event types
- producer ACLs allow the intended subscriber services
- downstream actions are idempotent
