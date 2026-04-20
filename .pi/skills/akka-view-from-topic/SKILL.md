---
name: akka-view-from-topic
description: Implement Akka Java SDK Views that consume topic messages using TableUpdater.onEvent(...), ce-subject metadata, and optional origin-aware filtering. Use when building query models from broker topics.
---

# Akka View from Topic

Use this skill when the source of the view is a topic.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../../../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`

## Source-specific rules

1. Subscribe with `@Consume.FromTopic("topic-name")`.
2. Ensure produced messages include `ce-subject` metadata so the view can select a row.
3. Use `updateContext().eventSubject()` to recover that row key.
4. Use `effects().ignore()` for message types or origin cases you do not want to project.
5. Use `updateContext().hasLocalOrigin()` or `originRegion()` when the topic flow needs region-aware filtering.

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

## Review checklist

Before finishing, verify:
- `@Consume.FromTopic` uses the intended topic name
- published messages include `ce-subject`
- ignored message types are explicit
- origin-aware logic is intentional when used
