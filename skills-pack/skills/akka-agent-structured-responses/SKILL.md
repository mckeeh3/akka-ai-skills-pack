---
name: akka-agent-structured-responses
description: Implement Akka Java SDK structured agent replies using responseConformsTo, responseAs, field descriptions, and fallback mapping. Use when the main concern is typed model output.
---

# Akka Agent Structured Responses

Use this skill when the main task is generating typed agent output.


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
- `akka-context/sdk/agents/structured.html.md`
- `akka-context/sdk/agents/failures.html.md`

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
7. Treat the typed reply as model output, not authorization, policy approval, or durable product state until backend validation and governed-tool/capability checks accept it.
8. Map malformed, missing, or policy-incompatible fields to safe fallback/denial surfaces and traces before any side effect.

## Repository examples

- `WorkstreamRuntimeAgent`
  - structured recommendation reply
  - `responseConformsTo(...)`
  - fallback value
- a domain-specific evaluator agent
  - structured model reply mapped into an `EvaluationResult`

## Review checklist

Before finishing, verify:
- field names and descriptions match the intended JSON shape
- the system prompt does not conflict with the structured schema
- endpoint APIs do not expose raw model JSON strings
- fallback behavior is explicit if invalid JSON is possible
- structured outputs are validated against `AuthContext`, tenant/customer scope, approval/tool-boundary policy, and trace requirements before driving consequential behavior
