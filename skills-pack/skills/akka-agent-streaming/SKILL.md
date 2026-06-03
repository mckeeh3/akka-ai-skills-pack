---
name: akka-agent-streaming
description: Implement Akka Java SDK streaming agents and token-stream endpoints using StreamEffect, tokenStream, and grouped token delivery. Use when streaming responses are the main concern.
---

# Akka Agent Streaming

Use this skill when an agent should stream response tokens.


## Generated SaaS input contract

For generated full-stack AI-first SaaS agent work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- placement as a user-facing functional agent or a bounded internal agent, including owning workstream and structured surface placement when user-facing;
- capability id/class for each model request, tool call, output, workflow step, endpoint, or evaluation result;
- caller `AuthContext`, tenant/customer scope, roles/capabilities, allowed data/tools, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work trace fields, correlation ids, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing from prompt, memory, streaming, guardrail, or test mechanics.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/streaming.html.md`
- `akka-context/sdk/http-endpoints.html.md`
- `../../examples/akka-components/src/main/java/com/example/application/StreamingActivityAgent.java`
- `../../examples/akka-components/src/main/java/com/example/api/ActivityAgentEndpoint.java`
- `../../examples/akka-components/src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`

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

## Repository examples

- `StreamingActivityAgent`
  - minimal token-streaming agent
- `ActivityAgentEndpoint#stream`
  - endpoint that bridges the token stream to `HttpResponses.streamText(...)`

## Review checklist

Before finishing, verify:
- the method really returns `StreamEffect`
- callers use `tokenStream(...)`, not `method(...)`
- the streaming API path and request shape are explicit
- tests assert the streamed body or emitted chunks
