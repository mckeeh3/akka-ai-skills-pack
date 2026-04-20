---
name: akka-agent-structured-responses
description: Implement Akka Java SDK structured agent replies using responseConformsTo, responseAs, field descriptions, and fallback mapping. Use when the main concern is typed model output.
---

# Akka Agent Structured Responses

Use this skill when the main task is generating typed agent output.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/structured.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`
- `../../../src/test/java/com/example/application/ActivityAgentTest.java`

## Use this pattern when

- callers should receive a typed Java record instead of free-form text
- the model output should follow a JSON schema
- workflow or endpoint code depends on stable fields
- fallback mapping is needed when model output is malformed

## Core pattern

1. Define a small Java record for the reply.
2. Add `@Description` on fields when schema hints help the model.
3. Prefer `responseConformsTo(ReplyType.class)`.
4. Use `responseAs(...)` only when you must manually instruct JSON format.
5. Add `.onFailure(...)` if malformed JSON should become a fallback value.
6. Keep reply types narrow and purpose-built.

## Repository examples

- `ActivityAgent`
  - structured recommendation reply
  - `responseConformsTo(...)`
  - fallback value
- `ActivityAnswerEvaluatorAgent`
  - structured model reply mapped into an `EvaluationResult`

## Review checklist

Before finishing, verify:
- field names and descriptions match the intended JSON shape
- the system prompt does not conflict with the structured schema
- endpoint APIs do not expose raw model JSON strings
- fallback behavior is explicit if invalid JSON is possible
