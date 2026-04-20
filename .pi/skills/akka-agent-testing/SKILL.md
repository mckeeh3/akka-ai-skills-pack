---
name: akka-agent-testing
description: Test Akka Java SDK agents with TestModelProvider, TestKitSupport, workflow orchestration, and endpoint integration calls. Use when deterministic agent testing is the main concern.
---

# Akka Agent Testing

Use this skill when testing agent code or agent-driven flows.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/agents/llm_eval.html.md`
- `../../../src/test/java/com/example/application/ActivityAgentTest.java`
- `../../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`

## Use this pattern when

- agent behavior must be deterministic in tests
- workflow steps call one or more agents
- endpoints expose agents directly
- structured replies must be asserted without real model calls

## Core pattern

1. Extend `TestKitSupport`.
2. Create one `TestModelProvider` per agent when different mocked behavior is needed.
3. Register them in `testKitSettings()` with `.withModelProvider(...)`.
4. Use `.fixedResponse(...)` for simple deterministic replies.
5. Use `.whenMessage(...).reply(...)` for scenario-specific behavior.
6. Call agents through `componentClient.forAgent().inSession(...)`.
7. Call endpoints through `httpClient` and workflows through `componentClient.forWorkflow(...)`.

## Repository examples

- `ActivityAgentTest`
  - single agent, structured reply mapping
- `AgentTeamWorkflowIntegrationTest`
  - workflow orchestration with two mocked agents
- `ActivityAgentEndpointIntegrationTest`
  - HTTP endpoint over direct and streaming agent calls

## Review checklist

Before finishing, verify:
- each tested agent has a registered `TestModelProvider`
- session ids are supplied in agent calls
- structured responses are serialized with `JsonSupport.encodeToString(...)`
- workflow tests use `Awaitility` when completion is asynchronous
- endpoint tests use `httpClient`, not `componentClient`
