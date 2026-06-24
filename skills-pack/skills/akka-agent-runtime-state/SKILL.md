---
name: akka-agent-runtime-state
description: Work with built-in Akka Java SDK agent runtime state such as PromptTemplate and SessionMemoryEntity through views, endpoints, consumers, and compaction flows. Use when the task involves prompt-template management, session-memory analytics, or session-memory compaction.
---

# Akka Agent Runtime State

Use this skill when the main task is built-in agent runtime state rather than the agent prompt/response method itself.

Use `akka-agent-prompt-governance` instead when prompts require tenant-scoped review, approval, activation, version history, diff/history UI, effective prompt assembly, prompt assembly traces, or governed admin surfaces. Use this skill's built-in `PromptTemplate` pattern for simple runtime-editable prompt text without a governance workflow.

## Worker/tool/capability alignment

For generated AI-first SaaS app work, treat the agent runtime, autonomous task loop, or governed artifact in scope as a software-worker harness concern, not as the product operation or authorization boundary. Keep the chain explicit:

```text
software worker
→ Akka Agent/AutonomousAgent harness or focused governance artifact
→ actor adapter (`agent_tool_call`, `human_chat_tool_plan`, workflow/timer/consumer/API/MCP/internal adapter as applicable)
→ governed tool
→ backend capability
→ Akka/frontend implementation
```

Human surface availability, prompt/skill/reference text, model output, task instructions, and Akka tool registration do not grant tool authority. A model-facing tool, loader, or autonomous task action may be exposed only when the active workstream tool catalog, governed tool contract, backend `AuthContext`, and `ToolPermissionBoundary` explicitly allow that actor adapter; denials and approval-required paths must fail closed and be traced.


## Required reading

Read these first if present:
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`

## Use this pattern when

- prompts should be managed at runtime without redeploying and without a full governance workflow
- current prompt-template values should be queryable through a view or endpoint
- session-memory activity should drive analytics or alerts
- oversized session histories should be compacted into summaries
- compaction outcomes should be published for audit or observability

## Core pattern

1. Use the built-in `PromptTemplate` entity for simple runtime-managed prompts.
2. Use `systemMessageFromTemplate(...)` in the agent when prompt text should be mutable without review/approval/version activation semantics.
3. Build views from `PromptTemplate` or `SessionMemoryEntity` with `@Consume.FromEventSourcedEntity(...)`.
4. Expose prompt-template or session-memory views through HTTP endpoints using API-specific response records.
5. Treat `SessionMemoryEntity` as built-in event-sourced state that can trigger consumers.
6. Use a dedicated compaction agent with `MemoryProvider.none()` when summarizing history.
7. Keep audit publication separate from compaction logic when you need topic output.

## Pattern references

- `WorkstreamRuntimeAgent` with governed prompt/runtime loading
  - prompt loaded from `PromptTemplate`
- a governed prompt/runtime-state endpoint
  - update/read prompt-template state over HTTP
- agent behavior document/version views such as `PromptDocumentView`
  - query current prompt value and update count
- a governed prompt history endpoint
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
- governed prompt needs have been routed to `akka-agent-prompt-governance` instead of overloading simple `PromptTemplate` guidance
- views return wrapper records for multi-row queries
- endpoints expose API-facing types rather than view rows directly when appropriate
- compaction consumers avoid recursive compaction loops
- compaction agents do not accidentally write their own interaction history into session memory
- compaction audit views and SSE endpoints are routed when observability is required
- topic-producing audit consumers attach `ce-subject` metadata
