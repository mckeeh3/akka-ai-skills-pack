---
name: akka-agent-streaming
description: Implement Akka Java SDK streaming agents and token-stream endpoints using StreamEffect, tokenStream, and grouped token delivery. Use when streaming responses are the main concern.
---

# Akka Agent Streaming

Use this skill when an agent should stream response tokens.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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
