---
name: akka-agent-component
description: Implement Akka Java SDK Agent classes with one command handler, clear prompts, bounded memory, and fallback handling. Use when writing the agent component itself.
---

# Akka Agent Component

Use this skill when the task is mainly about the agent class itself.

Before writing the class for a generated SaaS app, confirm whether it implements a user-facing functional/context-area agent or a bounded internal agent. Functional agents must align with a workstream, structured surfaces, capabilities, authority indicators, and UI tests; internal agents are invoked behind workflows, tools, timers, consumers, endpoints, or services and should not become primary navigation units by accident.


## Generated SaaS input contract

For generated full-stack AI-first SaaS agent work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- placement as a user-facing functional agent or a bounded internal agent, including owning workstream and structured surface placement when user-facing;
- capability id/class for each model request, tool call, output, workflow step, endpoint, or evaluation result;
- caller `AuthContext`, tenant/customer scope, roles/capabilities, allowed data/tools, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work trace fields, correlation ids, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing from prompt, memory, streaming, guardrail, or test mechanics.

## Required reading

Read these first if present:
- `../../docs/agent-workstream-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../src/main/java/com/example/application/ActivityAgent.java`
- `../../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../../src/main/java/com/example/application/ConfiguredModelActivityAgent.java`
- `../../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../akka-agent-model-governance/SKILL.md` when model aliases, `ModelConfigRef`, model policy, fallback model policy, or provider secret boundaries are in scope

## Core pattern

1. Annotate the class with `@Component(id = "...")`.
2. Extend `Agent`.
3. Keep exactly one public command handler.
4. Use a stable system message constant or a small request-to-system-message builder.
5. Add `.memory(...)` only when the memory behavior is intentional.
6. Add `.onFailure(...)` when a fallback value or graceful error is needed.
7. Keep the agent class stateless.
8. Keep tool calls aligned to named capability contracts; tool availability does not define the agent's responsibility or authorization boundary.

## Repository example

- `ActivityAgent`
  - single command handler
  - structured reply type
  - bounded session memory
  - fallback reply on failure
- `TemplateBackedActivityAgent`
  - system prompt loaded from the built-in `PromptTemplate` entity
- `ConfiguredModelActivityAgent`
  - focused static example of `ModelProvider.fromConfig("openai-low-temperature")`; for managed runtime agents, pair this pattern with `akka-agent-model-governance` so `AgentDefinition.modelConfigRef`, model policy, fallback policy, provider secret boundaries, and model-use traces are resolved before invocation
- `ActivityPromptEndpoint`
  - HTTP management of prompt-template values

## Review checklist

Before finishing, verify:
- functional/context-area versus internal-agent placement is explicit
- there is only one public command handler
- the handler returns `Effect<T>` or `StreamEffect`
- prompt text matches the task exactly
- fallback handling is explicit when the reply must stay well-formed
- no mutable request-specific state is stored on the agent instance
- configured model aliases are safe references only; provider secrets are not embedded in prompts, code examples, frontend responses, traces, or agent-visible context
