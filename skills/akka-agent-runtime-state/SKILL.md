---
name: akka-agent-runtime-state
description: Work with built-in Akka Java SDK agent runtime state such as PromptTemplate and SessionMemoryEntity through views, endpoints, consumers, and compaction flows. Use when the task involves prompt-template management, session-memory analytics, or session-memory compaction.
---

# Akka Agent Runtime State

Use this skill when the main task is built-in agent runtime state rather than the agent prompt/response method itself.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `../../../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../../../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../../../src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `../../../src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `../../../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../../../src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertView.java`
- `../../../src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAuditView.java`
- `../../../src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java`
- `../../../src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

## Use this pattern when

- prompts should be managed at runtime without redeploying
- current prompt-template values should be queryable through a view or endpoint
- session-memory activity should drive analytics or alerts
- oversized session histories should be compacted into summaries
- compaction outcomes should be published for audit or observability

## Core pattern

1. Use the built-in `PromptTemplate` entity for runtime-managed prompts.
2. Use `systemMessageFromTemplate(...)` in the agent when prompt text should be mutable.
3. Build views from `PromptTemplate` or `SessionMemoryEntity` with `@Consume.FromEventSourcedEntity(...)`.
4. Expose prompt-template or session-memory views through HTTP endpoints using API-specific response records.
5. Treat `SessionMemoryEntity` as built-in event-sourced state that can trigger consumers.
6. Use a dedicated compaction agent with `MemoryProvider.none()` when summarizing history.
7. Keep audit publication separate from compaction logic when you need topic output.

## Repository examples

- `TemplateBackedActivityAgent`
  - prompt loaded from `PromptTemplate`
- `ActivityPromptEndpoint`
  - update/read prompt-template state over HTTP
- `PromptTemplateHistoryView`
  - query current prompt value and update count
- `PromptTemplateHistoryEndpoint`
  - edge-facing API over the prompt-template history view
- `SessionMemoryByComponentView`
  - query sessions by the component that most recently wrote memory
- `SessionMemoryViewEndpoint`
  - edge-facing API over session-memory analytics rows
- `SessionMemoryAlertView`
  - materializes threshold alerts for query and streaming
- `SessionMemoryAlertStreamEndpoint`
  - SSE over session-memory threshold alerts
- `SessionMemoryAlertsConsumer`
  - publish alert events when memory exceeds a threshold
- `SessionMemoryCompactionAgent`
  - summarize full session history into one user and one AI message
- `SessionMemoryCompactionConsumer`
  - trigger compaction from `SessionMemoryEntity` events
- `SessionMemoryCompactionAuditConsumer`
  - publish topic audit events after compaction completes
- `SessionMemoryCompactionAuditView`
  - query and stream compaction audit rows by session id
- `SessionMemoryCompactionStreamEndpoint`
  - SSE over compaction audit updates for one session

## Review checklist

Before finishing, verify:
- prompt-template ids are stable and explicit
- views return wrapper records for multi-row queries
- endpoints expose API-facing types rather than view rows directly when appropriate
- compaction consumers avoid recursive compaction loops
- compaction agents do not accidentally write their own interaction history into session memory
- compaction audit views and SSE endpoints are routed when observability is required
- topic-producing audit consumers attach `ce-subject` metadata
