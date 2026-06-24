---
name: akka-agent-streaming
description: Implement Akka Java SDK streaming agents and token-stream endpoints using StreamEffect, tokenStream, and grouped token delivery. Use when streaming responses are the main concern.
---

# Akka Agent Streaming

Use this skill when an agent should stream response tokens.


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
- `akka-context/sdk/agents/streaming.html.md`
- `akka-context/sdk/http-endpoints.html.md`

## Use this pattern when

- users should see incremental model output
- full replies may take several seconds
- an endpoint should bridge a token stream to HTTP
- workflow notifications should publish token chunks

## Core pattern

1. The agent method returns `StreamEffect`.
2. Use `streamEffects()` instead of `effects()`.
3. Call the agent with `componentClient.forAgent().inSession(...).tokenStream(...)`.
4. Convert the returned `Source<String, NotUsed>` into an HTTP response or notifications.
5. Group tokens when you want fewer downstream events.

## Pattern references

- `StreamingWorkstreamRuntimeAgent`
  - minimal token-streaming agent
- `WorkstreamRuntimeAgentEndpoint#stream`
  - endpoint that bridges the token stream to `HttpResponses.streamText(...)`

## Review checklist

Before finishing, verify:
- the method really returns `StreamEffect`
- callers use `tokenStream(...)`, not `method(...)`
- the streaming API path and request shape are explicit
- tests assert the streamed body or emitted chunks
