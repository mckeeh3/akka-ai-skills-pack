---
name: akka-agent-orchestration
description: Call Akka Java SDK agents from workflows using shared sessions, durable retries, and supervisor-style orchestration. Use when the task is about reliable agent calling or multi-agent coordination.
---

# Akka Agent Orchestration

Use this skill when agents are being called from other Akka components, especially workflows.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/orchestrating.html.md`
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/SelectorAgent.java`
- `../../../src/main/java/com/example/application/PlannerAgent.java`
- `../../../src/main/java/com/example/application/SummarizerAgent.java`
- `../../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`

## Use this pattern when

- agent calls need retries and durable recovery
- several agents collaborate on the same request
- a workflow should own the shared session id
- a caller needs a final result separate from intermediate agent outputs

## Core pattern

1. Prefer workflows to directly orchestrate agents.
2. Use long step timeouts for AI calls.
3. Configure bounded retries with `RecoverStrategy.maxRetries(...)`.
4. Reuse the workflow id as the shared session id.
5. Persist intermediate outputs in workflow state when later steps need them.
6. Do not model agent-to-agent coordination as tools.

## Repository example

- `AgentTeamWorkflow`
  - predefined two-agent workflow
  - workflow id is reused as the shared session id
  - workflow state stores intermediate weather context and final answer
- `DynamicAgentTeamWorkflow`
  - selector/planner/summarizer orchestration
  - uses `dynamicCall(agentId)` for worker-agent execution
  - summarizes accumulated worker responses into a final answer

## Review checklist

Before finishing, verify:
- the caller uses `componentClient.forAgent().inSession(...)`
- timeouts are long enough for model latency
- retries are bounded
- workflow state captures the data needed by later steps
- agent collaboration uses a workflow supervisor instead of direct chaining
