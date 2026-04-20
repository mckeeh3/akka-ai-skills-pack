---
name: akka-agent-memory
description: Configure Akka Java SDK agent session memory using session ids, MemoryProvider, limited windows, and filters. Use when memory behavior is the main concern.
---

# Akka Agent Memory

Use this skill when the main task is session memory behavior.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

## Use this pattern when

- the agent should remember prior turns in the same session
- several agents share context across one workflow or conversation
- token cost must be controlled with a bounded window
- some messages should be hidden from specific agents

## Core pattern

1. Treat the session id as a first-class design choice.
2. Use `componentClient.forAgent().inSession(sessionId)` on every caller.
3. Use `MemoryProvider.limitedWindow()` for bounded context.
4. Use `MemoryProvider.none()` for evaluators, compaction, or stateless judging agents.
5. Use memory filters only when a multi-agent session needs visibility rules.
6. Prefer workflow id as session id when a workflow supervises agents.

## Repository examples

- `ActivityAgent`
  - bounded memory with `readLast(6)`
- `AgentTeamWorkflow`
  - shared session id derived from `workflowId()`
- `ActivityAnswerEvaluatorAgent`
  - memory disabled for evaluation
- `SessionMemoryAlertsConsumer`
  - reacts to built-in `SessionMemoryEntity` events
- `SessionMemoryByComponentView`
  - indexes sessions by the latest component that wrote to memory
- `SessionMemoryCompactionAgent`
  - summarizes full session history without using memory itself
- `SessionMemoryCompactionConsumer`
  - compacts oversized histories back into `SessionMemoryEntity`
- `SessionMemoryCompactionAuditConsumer`
  - publishes compaction audit events to a topic

## Review checklist

Before finishing, verify:
- session ids are created or supplied at the caller boundary
- memory reads and writes are intentional
- evaluators or compaction agents do not accidentally retain session history
- workflow-supervised agents reuse the same session id when collaboration is intended
