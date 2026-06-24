---
name: akka-consumer-from-service-stream
description: Implement Akka Java SDK Consumers that subscribe to service-to-service streams from another Akka service using @Consume.FromServiceStream.
---

# Akka Consumer from Service Stream

Use this skill when a Consumer subscribes to a stream published by another Akka service in the same Akka project.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

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

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- the source annotation is `@Consume.FromServiceStream`
- `service` matches the publishing Akka service name
- `id` matches the producer's `@Produce.ServiceStream(id = "...")`
- subscriber handlers accept only public event types
- producer ACLs allow the intended subscriber services
- downstream actions are idempotent
