---
name: akka-agent-component
description: Implement Akka Java SDK Agent classes with one command handler, clear prompts, bounded memory, and fallback handling. Use when writing the agent component itself.
---

# Akka Agent Component

Use this skill when the task is mainly about a request-based Akka `Agent` class itself.

Before writing the class for a generated SaaS app, confirm whether it implements a user-facing functional/context-area agent or a bounded internal agent. Functional agents must align with a workstream, structured surfaces, capabilities, authority indicators, and UI tests; internal agents are invoked behind workflows, tools, timers, consumers, endpoints, or services and should not become primary navigation units by accident.

If the internal/background work needs a durable typed task, task id, lifecycle, dependency, failure/cancellation, snapshot, notification stream, or model-driven delegation/handoff/team/moderation, switch to `akka-autonomous-agents` instead of forcing it into a request-based `Agent`.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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
4. Use a stable system message constant or a small request-to-system-message builder.
5. Add `.memory(...)` only when the memory behavior is intentional.
6. Add `.onFailure(...)` only for explicit graceful error/degraded-status DTOs or bounded fallback behavior that does not pretend model-backed work succeeded.
7. Keep the agent class stateless.
8. Keep tool calls aligned to named capability contracts; tool availability does not define the agent's responsibility or authorization boundary.
9. For generated app runtime, missing provider/model config, disabled/unknown managed agent state, missing `AuthContext`, denied tool boundary, or policy denial must fail closed with audit/work trace and actionable error; do not return canned, deterministic, or simulated successful work as the normal user-facing response.

## Pattern reference

- `WorkstreamRuntimeAgent`
  - single command handler
  - structured reply type
  - bounded session memory
  - fallback reply on failure
- `WorkstreamRuntimeAgent` with governed prompt/runtime loading
  - system prompt loaded from the built-in `PromptTemplate` entity
- `ConfiguredModelWorkstreamRuntimeAgent`
  - focused static example of `ModelProvider.fromConfig("openai-low-temperature")`; for managed runtime agents, pair this pattern with `akka-agent-model-governance` so `AgentDefinition.modelConfigRef`, model policy, fallback policy, provider secret boundaries, and model-use traces are resolved before invocation
- a governed prompt/runtime-state endpoint
  - HTTP management of prompt-template values

## Review checklist

Before finishing, verify:
- functional/context-area versus internal-agent placement is explicit
- there is only one public command handler
- the handler returns `Effect<T>` or `StreamEffect`
- prompt text matches the task exactly
- fallback handling is explicit, fail-closed for provider/security/policy gaps, and never masks missing model-backed runtime behavior as success
- no mutable request-specific state is stored on the agent instance
- configured model aliases are safe references only; provider secrets are not embedded in prompts, code examples, frontend responses, traces, or agent-visible context
