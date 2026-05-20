---
name: akka-view-from-topic
description: Implement Akka Java SDK Views that consume topic messages using TableUpdater.onEvent(...), ce-subject metadata, and optional origin-aware filtering. Use when building query models from broker topics.
---

# Akka View from Topic

Use this skill when the source of the view is a topic.

Capability-first framing: use topic-backed Views for read/evidence capabilities built from event streams, external signals, trace enrichment, integration reports, or cross-service summaries. The View should curate and scope evidence for callers; side effects from topic messages belong in Consumers, not Views.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `../../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`
- `../../docs/capability-first-backend-architecture.md`

## Source-specific rules

1. Subscribe with `@Consume.FromTopic("topic-name")`.
2. Ensure produced messages include `ce-subject` metadata so the view can select a row.
3. Use `updateContext().eventSubject()` to recover that row key.
4. Use `effects().ignore()` for message types or origin cases you do not want to project.
5. Use `updateContext().hasLocalOrigin()` or `originRegion()` when the topic flow needs region-aware filtering.
6. Preserve provenance, correlation id, tenant/customer scope, and redaction-ready fields when they are part of the read/evidence capability contract.

## Repository example

- `ShoppingCartTopicView`
  - consumes topic messages with `ce-subject`
  - demonstrates `effects().ignore()`
  - stores origin information from `updateContext()` in the row

## Testing rules

For topic-backed view tests:
- configure `withTopicIncomingMessages("topic-name")`
- publish messages with metadata containing `ce-subject`
- query the view through `componentClient.forView()`
- assert eventual consistency with `Awaitility`

## Generated SaaS view contract

For generated SaaS read/evidence capabilities, require:
- tenant/customer scoped row keys and query filters aligned with the selected `AuthContext`;
- redacted DTO rows for the chosen UI/API/MCP/agent-tool consumers, not raw state dumps;
- stable surface payload or evidence-bundle mapping when used by structured surfaces;
- data-access audit/work-trace requirements at the query or endpoint boundary;
- tests for authorized query, forbidden/cross-tenant query, redaction, projection update/delete behavior, and surface/API/tool consumers where exposed.


## Review checklist

Before finishing, verify:
- `@Consume.FromTopic` uses the intended topic name
- published messages include `ce-subject`
- ignored message types are explicit
- protected evidence queries include scope/provenance filters and caller-safe fields for UI/API/MCP/agent exposure
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
- origin-aware logic is intentional when used
