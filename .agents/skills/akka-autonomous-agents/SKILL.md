---
name: akka-autonomous-agents
description: Implement Akka Java SDK AutonomousAgent components for durable internal/background typed task work. Use after capability contracts are clear and when request-based Agent or Workflow alone is not the right substrate.
---

# Akka Autonomous Agents

Use this skill when implementing or reviewing an Akka Java SDK `AutonomousAgent`: a durable model-driven component that owns a task loop, typed tasks, snapshots/state, cancellation/failure, notifications, and optional model-driven coordination.

Do **not** use Autonomous Agents as the default for user-facing workstream request/response turns. Use request-based `Agent` for bounded conversational turns, streaming responses, one-shot workflow steps, and immediate replies. Use `Workflow` when product correctness depends on deterministic steps, retries, pauses, approvals, compensation, or explicit orchestration. Use `Workflow + AutonomousAgent` when a deterministic process launches or waits on a durable model-driven investigation.

## Required reading

Read before implementing generated-app AutonomousAgent worker tasks:
- `../../docs/autonomous-agent-worker-runtime-pattern.md`

Read when API details are needed:
- `../../docs/agent-component-selection-guide.md`
- `../../specs/autonomous-agents-integration/research-notes.md`
- `akka-context/sdk/autonomous-agents.html.md`
- `akka-context/sdk/autonomous-agents/defining.html.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## Use when

- work is durable, asynchronous, task-oriented, and may outlive the initiating request;
- the model should iterate and decide which tools or coordination actions to use next;
- callers need typed task ids, snapshots, terminal results, failure/cancellation, dependencies, or notification streams;
- internal/background work includes investigation, monitoring, remediation, batch review, escalation processing, evaluator loops, or long-running analysis;
- model-driven delegation, handoff, team leadership, or moderation is actually needed.

## Generated SaaS contract

Before implementation, the task/spec must supply the reusable worker contract from `../../docs/autonomous-agent-worker-runtime-pattern.md` when this is generated-app worker work:
- internal/background or explicit functional placement; do not promote it to a left-rail workstream unless product intent says so;
- task capability ids and governed-tool ids for starting, assigning, querying, completing/failing, suspending/resuming, terminating, and streaming notifications;
- caller `AuthContext`, tenant/customer scope, roles/capabilities, model policy, tool boundary, approval gates, trace requirements, and provider-secret boundary;
- internal workstream agent graph placement, delegation source, result/escalation surface, and whether exposed operations are agent-tools, internal-tools, workflow-tools, timer-tools, or consumer-tools;
- task contract with typed task input/result DTOs, instructions, attachments, dependency behavior, idempotency, cancellation/failure behavior, and tests;
- v3 `worker.task.*` events, attention upsert/resolve rules, structured progress/result surfaces, provider fail-closed behavior, and no fake success/runtime-substitute guardrails.

Block or repair the task if these are missing for generated-app work.

## Core component pattern

1. Annotate with `@Component(id = "...", description = "...")`.
2. Extend `AutonomousAgent`.
3. Implement one `definition()` method returning Akka autonomous `AgentDefinition` from `define()`.
4. Add accepted task capabilities with `TaskAcceptance.of(...)`.
5. Add `.instructions(...)`, `.tools(...)`, `.mcpTools(...)`, `.modelProvider(...)`, guardrails, or content loaders only when required.
6. Start through `componentClient.forAutonomousAgent(AgentClass.class, instanceId).runSingleTask(task)` for auto-stopping single-task work.
7. Use `assignTasks(...)` only when application code owns lifecycle management (`suspend`, `resume`, `terminate`).
8. Query results through `componentClient.forTask(taskId).get(taskDefinition)` or `.result(taskDefinition)`.

## Terminology guardrail

Akka autonomous `AgentDefinition` is the SDK builder result returned by `AutonomousAgent.definition()` or supplied via `AgentSetup`. It is **not** the skills pack's governed managed-agent `AgentDefinition` domain concept. When both appear, qualify them.

## Review checklist

- request-based Agent and Workflow were rejected for explicit reasons;
- task definitions are typed and small;
- task start/query/result/notification surfaces enforce backend authorization;
- model config, provider secrets, and dynamic setup fail closed when invalid;
- protected governed-tools are registered only after `ToolPermissionBoundary` enforcement and with explicit agent-tool/internal-tool exposure labels;
- task start/query/result/notification paths preserve the governed-tool contract across workflows, timers, consumers, and browser APIs;
- normal runtime does not use deterministic/demo/model-less substitutes, fake success, or direct provider/service calls that bypass Akka `AutonomousAgent`;
- provider/model/governed runtime/tool/evidence gaps fail closed with actionable state instead of canned success;
- worker progress/results emit v3 `worker.task.*` events, update attention, and render backend-projected surfaces when visible;
- tests use `TestModelProvider.AutonomousAgentTools` only as test infrastructure.
