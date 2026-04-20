---
name: akka-agents
description: Orchestrate Akka Java SDK Agent work across prompt design, structured responses, tools, memory, streaming, workflow orchestration, guardrails, evaluation, and testing. Use when the task spans more than one agent concern.
---

# Akka Agents

Use this as the top-level skill for Akka Java SDK agent work.

## Goal

Generate or review agent code that is:
- correct for Akka SDK 3.4+
- explicit about the agent's single responsibility
- safe about session ids, memory, and failure handling
- easy for AI coding agents to extend with focused companion skills
- backed by tests or workflow-driven examples when reliability matters

## Required reading before coding

Read these first if present:
- `akka-context/sdk/agents.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/structured.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/streaming.html.md`
- `akka-context/sdk/agents/orchestrating.html.md`
- `akka-context/sdk/agents/guardrails.html.md`
- `akka-context/sdk/agents/llm_eval.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`
- `../../../src/main/java/com/example/application/StreamingActivityAgent.java`
- `../../../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/SelectorAgent.java`
- `../../../src/main/java/com/example/application/PlannerAgent.java`
- `../../../src/main/java/com/example/application/SummarizerAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertView.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../../../src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `../../../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`
- `../../../src/main/java/com/example/application/CompetitorMentionGuard.java`
- `../../../src/main/java/com/example/api/ActivityAgentEndpoint.java`
- `../../../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../../../src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `../../../src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `../../../src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `../../../src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java`
- `../../../src/test/java/com/example/application/ActivityAgentTest.java`
- `../../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/DynamicAgentTeamWorkflowEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-agent-component`
  - core agent class, single command handler, prompt shape, session strategy, and failure fallback
- `akka-agent-structured-responses`
  - `responseConformsTo(...)`, `responseAs(...)`, field descriptions, and fallback mapping
- `akka-agent-tools`
  - `@FunctionTool`, external tool classes, Akka component tools, and MCP tool registration
- `akka-agent-memory`
  - session ids, `MemoryProvider`, limited windows, and filtered memory reads
- `akka-agent-streaming`
  - `StreamEffect`, `tokenStream(...)`, streaming endpoints, and grouped token delivery
- `akka-agent-orchestration`
  - calling agents from workflows, shared session ids, and multi-agent supervisor patterns
- `akka-agent-guardrails`
  - runtime-enforced input/output validation and configuration-driven guardrail selection
- `akka-agent-evaluation`
  - evaluator agents and `EvaluationResult` patterns for LLM-as-judge flows
- `akka-agent-runtime-state`
  - built-in `PromptTemplate` and `SessionMemoryEntity` patterns, views, endpoints, and compaction flows
- `akka-agent-testing`
  - `TestModelProvider`, deterministic tests, and workflow or endpoint integration tests

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

Rules:
- agent classes belong in `application`
- helper tool classes usually belong in `application`
- evaluator and guardrail classes may also live in `application`
- endpoints that call agents belong in `api`
- workflows orchestrating agents belong in `application`

## Core rules

1. An agent extends `Agent` and has `@Component(id = "...")`.
2. An agent has exactly one public command handler.
3. Command handlers accept 0 or 1 parameter and return `Effect<T>` or `StreamEffect`.
4. Prefer a stable system message constant or a small builder method.
5. Use explicit session ids with `componentClient.forAgent().inSession(...)`.
6. Prefer `responseConformsTo(...)` for structured replies.
7. Use `.onFailure(...)` for fallback handling instead of assuming model output is always valid.
8. Keep agents stateless; use memory or Akka components for context instead of mutable fields.
9. Use workflows to orchestrate multiple agents or to add retries, timeouts, and durable progress.
10. Use `TestModelProvider` for deterministic tests.

## Decision guide

### 1. Single-purpose request/reply agent
Use when one model interaction produces one reply.

Repository example:
- `ActivityAgent`

### 2. Tool-using agent
Use when the model must call functions to fetch data or trigger actions.

Repository examples:
- `WeatherAgent`
- `WeatherForecastTools`

### 3. Streaming agent
Use when tokens should be returned incrementally to an endpoint or notification flow.

Repository examples:
- `StreamingActivityAgent`
- `ActivityAgentEndpoint#stream`

### 4. Workflow-supervised agent team
Use when AI calls need durable retries, shared sessions, or multi-step orchestration.

Repository example:
- `AgentTeamWorkflow`

### 5. Evaluated or governed agent
Use when output quality or runtime safety checks are a first-class concern.

Repository examples:
- `ActivityAnswerEvaluatorAgent`
- `CompetitorMentionGuard`
- `../../../src/main/resources/application.conf`

## Final review checklist

Before finishing, verify:
- the agent has exactly one public command handler
- session id strategy is explicit
- prompt and response type match each other
- memory behavior is intentional
- tools have rich `@FunctionTool` descriptions when used
- structured response records are small and descriptive
- workflow orchestration is used instead of agent-to-agent tool chaining
- tests replace real models with `TestModelProvider`

## Response style

When answering coding tasks:
- name the agent class and its single responsibility explicitly
- state whether the reply is plain text, structured, or streamed
- mention memory, tools, or workflow orchestration only when actually used
- list the concrete example files used as references
