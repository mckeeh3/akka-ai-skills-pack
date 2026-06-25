---
name: akka-agent-memory
description: Configure Akka Java SDK agent session memory using session ids, MemoryProvider, limited windows, and filters. Use when memory behavior is the main concern.
---

# Akka Agent Memory

Use this skill when the main task is session memory behavior.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

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
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/memory.html.md`

## Use this pattern when

- the agent should remember prior turns in the same session
- several agents share context across one workflow or conversation
- token cost must be controlled with a bounded window
- some messages should be hidden from specific agents

## Core pattern

1. Treat the session id as a first-class design choice.
2. Bind session ids to tenant/customer, selected `AuthContext`, agent/workstream, and workflow or conversation scope; do not trust raw caller-supplied ids across tenants.
3. Use `componentClient.forAgent().inSession(sessionId)` on every caller.
4. Use `MemoryProvider.limitedWindow()` for bounded context.
5. Use `readOnly()` when the agent should consume history without writing to it.
6. Use `writeOnly()` when the agent should append interactions but not read prior context.
7. Use `MemoryProvider.none()` for evaluators, compaction, or stateless judging agents.
8. Use memory filters when a multi-agent session needs visibility rules.
9. Prefer workflow id as session id when a workflow supervises agents.
10. Treat memory as model context, not authorization or durable product evidence; redact secrets/sensitive fields and fail closed before memory read/write when scope cannot be verified.

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
- session ids are created or supplied at the caller boundary and validated against tenant/customer, `AuthContext`, agent, and workflow/conversation scope
- memory reads and writes are intentional and covered by provider/security fail-closed behavior
- session memory never grants data/tool/capability authority and does not leak secrets or cross-tenant/customer context
- evaluators or compaction agents do not accidentally retain session history
- workflow-supervised agents reuse the same session id when collaboration is intended
- filtered memory examples use `MemoryFilter` explicitly when visibility rules matter
