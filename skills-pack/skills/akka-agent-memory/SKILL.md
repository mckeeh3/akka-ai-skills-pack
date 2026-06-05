---
name: akka-agent-memory
description: Configure Akka Java SDK agent session memory using session ids, MemoryProvider, limited windows, and filters. Use when memory behavior is the main concern.
---

# Akka Agent Memory

Use this skill when the main task is session memory behavior.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/memory.html.md`

## Use this pattern when

- the agent should remember prior turns in the same session
- several agents share context across one workflow or conversation
- token cost must be controlled with a bounded window
- some messages should be hidden from specific agents

## Core pattern

1. Treat the session id as a first-class design choice.
2. Use `componentClient.forAgent().inSession(sessionId)` on every caller.
3. Use `MemoryProvider.limitedWindow()` for bounded context.
4. Use `readOnly()` when the agent should consume history without writing to it.
5. Use `writeOnly()` when the agent should append interactions but not read prior context.
6. Use `MemoryProvider.none()` for evaluators, compaction, or stateless judging agents.
7. Use memory filters when a multi-agent session needs visibility rules.
8. Prefer workflow id as session id when a workflow supervises agents.

## Pattern references

- `WorkstreamRuntimeAgent`
  - bounded memory with `readLast(6)`
- a domain-specific agent-team workflow
  - shared session id derived from `workflowId()`
- a domain-specific memory summary agent
  - read-only filtered memory that includes only worker-role messages and excludes `debug-agent`
- a domain-specific evaluator agent
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
- filtered memory examples use `MemoryFilter` explicitly when visibility rules matter
