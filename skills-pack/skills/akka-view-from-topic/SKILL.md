---
name: akka-view-from-topic
description: Implement Akka Java SDK Views that consume topic messages using TableUpdater.onEvent(...), ce-subject metadata, and optional origin-aware filtering. Use when building query models from broker topics.
---

# Akka View from Topic

Use this skill when the source of the view is a topic.

Capability-first framing: use topic-backed Views for read/evidence capabilities built from event streams, external signals, trace enrichment, integration reports, or cross-service summaries. The View should curate and scope evidence for callers; side effects from topic messages belong in Consumers, not Views.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `../docs/capability-first-backend-architecture.md`

## Source-specific rules

1. Subscribe with `@Consume.FromTopic("topic-name")`.
2. Ensure produced messages include `ce-subject` metadata so the view can select a row.
3. Use `updateContext().eventSubject()` to recover that row key.
4. Use `effects().ignore()` for message types or origin cases you do not want to project.
5. Use `updateContext().hasLocalOrigin()` or `originRegion()` when the topic flow needs region-aware filtering.
6. Preserve provenance, correlation id, tenant/customer scope, and redaction-ready fields when they are part of the read/evidence capability contract.

## Repository example

- `AgentRuntimeTraceView`
  - consumes topic messages with `ce-subject`
  - demonstrates `effects().ignore()`
  - stores origin information from `updateContext()` in the row

## Testing rules

For topic-backed view tests:
- configure `withTopicIncomingMessages("topic-name")`
- publish messages with metadata containing `ce-subject`
- query the view through `componentClient.forView()`
- assert eventual consistency with `Awaitility`

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Review checklist

Before finishing, verify:
- `@Consume.FromTopic` uses the intended topic name
- published messages include `ce-subject`
- ignored message types are explicit
- protected evidence queries include scope/provenance filters and caller-safe fields for UI/API/MCP/agent exposure
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
- origin-aware logic is intentional when used
