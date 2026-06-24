---
name: akka-consumer-producing
description: Implement Akka Java SDK Consumers that produce to broker topics or service streams, including metadata, filtering, and public event mapping.
---

# Akka Consumer Producing Pattern

Use this skill when a Consumer republishes, transforms, or filters messages into a topic or service stream.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`

## Use this pattern when

- internal events should be republished to a broker topic
- internal events should be transformed into a public service-stream contract
- only selected message types should be published
- downstream ordering depends on the original entity or workflow id

## Capability publication rules

Treat publication as a selected exposure surface for a named reactive/read-evidence capability. Publish only curated contracts that preserve required tenant/customer scope, subject, correlation id, provenance, redaction, and audit/trace references.

For service streams and topics, do not leak internal state or policy-only events by default. Use explicit filters, redacted public DTOs, service ACLs, and `ce-subject` metadata. Publishing denials, data-access summaries, or consequential side-effect notifications should follow the capability's audit and privacy rules.

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

These are Akka substrate mechanics examples, not generated-product architecture templates.

- `WorkstreamEventAttentionConsumer`
  - republishes internal workstream-event events to a topic
  - preserves the workstream id through `ce-subject`
- `WorkstreamEventAttentionConsumer`
  - maps internal workstream-event events to a public service-stream contract
  - ignores deletion events from the public stream
- a domain-specific workflow topic consumer
  - publishes only completed workflow updates to a topic

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- the correct produce annotation is used
- produced payloads are the intended public contract
- `ce-subject` metadata is included when needed
- ignored messages are intentional and explicit
- `@Acl` is present for service streams
- produced contracts are scoped/redacted and do not leak raw internal state, secrets, PII, or cross-tenant/customer data
- duplicate source events cannot produce unsafe duplicate downstream side effects, or downstream consumers receive a stable dedupe/correlation key
