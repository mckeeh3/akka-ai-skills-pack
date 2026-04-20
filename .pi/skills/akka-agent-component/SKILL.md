---
name: akka-agent-component
description: Implement Akka Java SDK Agent classes with one command handler, clear prompts, bounded memory, and fallback handling. Use when writing the agent component itself.
---

# Akka Agent Component

Use this skill when the task is mainly about the agent class itself.

## Required reading

Read these first if present:
- `akka-context/sdk/agents.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../../../src/main/java/com/example/api/ActivityPromptEndpoint.java`

## Core pattern

1. Annotate the class with `@Component(id = "...")`.
2. Extend `Agent`.
3. Keep exactly one public command handler.
4. Use a stable system message constant or a small request-to-system-message builder.
5. Add `.memory(...)` only when the memory behavior is intentional.
6. Add `.onFailure(...)` when a fallback value or graceful error is needed.
7. Keep the agent class stateless.

## Repository example

- `ActivityAgent`
  - single command handler
  - structured reply type
  - bounded session memory
  - fallback reply on failure
- `TemplateBackedActivityAgent`
  - system prompt loaded from the built-in `PromptTemplate` entity
- `ActivityPromptEndpoint`
  - HTTP management of prompt-template values

## Review checklist

Before finishing, verify:
- there is only one public command handler
- the handler returns `Effect<T>` or `StreamEffect`
- prompt text matches the task exactly
- fallback handling is explicit when the reply must stay well-formed
- no mutable request-specific state is stored on the agent instance
