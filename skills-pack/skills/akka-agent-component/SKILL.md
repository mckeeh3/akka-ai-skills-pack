---
name: akka-agent-component
description: Implement Akka Java SDK Agent classes with one command handler, clear prompts, bounded memory, and fallback handling. Use when writing the agent component itself.
---

# Akka Agent Component

Use this skill when the task is mainly about a request-based Akka `Agent` class itself.

Before writing the class for a generated SaaS app, confirm its governed managed `AgentDefinition` and whether it implements a user-facing functional/context-area agent or a bounded internal agent. Functional agents must align with a workstream, structured surfaces, capabilities, authority indicators, and UI tests; internal agents are invoked behind workflows, tools, timers, consumers, endpoints, or services and should not become primary navigation units by accident.

If the internal/background work needs a durable typed task, task id, lifecycle, dependency, failure/cancellation, snapshot, notification stream, or model-driven delegation/handoff/team/moderation, switch to `akka-autonomous-agents` instead of forcing it into a request-based `Agent`.


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
- `../docs/agent-workstream-application-architecture.md`
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../akka-agent-model-governance/SKILL.md` when model aliases, `ModelConfigRef`, model policy, fallback model policy, or provider secret boundaries are in scope

## Core pattern

1. Annotate the class with `@Component(id = "...")`.
2. Extend `Agent` (not `AutonomousAgent`; load `akka-autonomous-agents` for that component type).
3. Keep exactly one public command handler.
4. Keep any in-code system message to a minimal static bootstrap or fallback-only shell; generated-app runtime prompt text must come from the active governed prompt resolved through `AgentRuntimeResolver`.
5. Register request-scoped runtime tools resolved by the governed profile, including `readSkill(skillId)` for every managed agent; store tool availability as governed logical tool/capability ids and/or approved Java binding ids/classes, never arbitrary model-supplied class names.
6. Add `.memory(...)` only when the memory behavior is intentional.
7. Add `.onFailure(...)` only for explicit graceful error/degraded-status DTOs or bounded fallback behavior that does not pretend model-backed work succeeded.
8. Keep the agent class stateless.
9. Keep tool calls aligned to named capability contracts; tool availability does not define the agent's responsibility or authorization boundary.
10. For generated app runtime, missing provider/model config, disabled/unknown managed agent state, missing `AuthContext`, denied tool boundary, missing prompt/skill manifest, or policy denial must fail closed with audit/work trace and actionable error; do not return canned, deterministic, or simulated successful work as the normal user-facing response.

## Pattern reference

- `WorkstreamRuntimeAgent`
  - single command handler
  - structured reply type
  - bounded session memory
  - fallback reply on failure
- `WorkstreamRuntimeAgent` with governed prompt/runtime loading
  - system prompt loaded from the active governed prompt version through `AgentRuntimeResolver`, with compact assigned skill names/descriptions/hints appended and `readSkill(skillId)` registered as a runtime tool
- configured model alias pattern
  - when a static Java agent uses `ModelProvider.fromConfig("openai-low-temperature")`, treat the alias as a safe deployment-configured reference only; for managed runtime agents, pair this pattern with `akka-agent-model-governance` so `AgentDefinition.modelConfigRef`, model policy, fallback policy, provider secret boundaries, and model-use traces are resolved before invocation
- a governed prompt/runtime-state endpoint
  - HTTP management of prompt-template values

## Review checklist

Before finishing, verify:
- functional/context-area versus internal-agent placement is explicit
- there is only one public command handler
- the handler returns `Effect<T>` or `StreamEffect`
- prompt text is resolved from governed runtime state and matches the task exactly after assembly
- compact assigned skill context is appended and `readSkill(skillId)` is registered for every managed agent
- fallback handling is explicit, fail-closed for provider/security/policy/governance gaps, and never masks missing model-backed runtime behavior as success
- no mutable request-specific state is stored on the agent instance
- configured model aliases are safe references only; provider secrets are not embedded in prompts, code examples, frontend responses, traces, or agent-visible context
