# Agent Component Selection Guide

Use this guide after secure AI-first SaaS, the requirements-to-workstream process, agent workstream, and capability-first modeling are clear enough to choose an Akka substrate. The canonical process is `./requirements-to-workstream-development-process.md`: workstreams → attention/dashboard/surfaces/actions → capabilities/APIs → Akka substrate → request-based workstream Agents and AutonomousAgent task candidates → notifications/projections/traces.

## Default choices

| Need | Choose | Why |
|---|---|---|
| User-facing workstream turn, bounded request/response, streaming response, or one model round trip from a component/workflow | request-based Akka `Agent` | The caller owns the turn and expects an immediate or streamed result; the component can use per-call prompt/effects control and managed runtime tool registration. |
| Durable background/internal model-driven work with typed task lifecycle, dependencies, snapshots, cancellation/failure, notifications, or model-chosen iteration | Akka `AutonomousAgent` | The work is a task/process that outlives the initiating request and benefits from Akka's autonomous task loop. |
| Deterministic long-running business process with explicit order, retries, compensation, timeouts, or approval pause/resume | Akka `Workflow` | Product correctness depends on known state transitions rather than model-chosen iteration. |
| Deterministic process that needs one bounded model step | `Workflow` + request-based `Agent` | The workflow owns process state; the agent performs a single reasoning/classification/summarization step. |
| Deterministic process that launches or waits on a durable open-ended investigation/task | `Workflow` + `AutonomousAgent` | The workflow owns product orchestration; the autonomous agent owns the model-driven task loop. |

## Generated-app routing rules

- Keep workstream functional/context-area agents request-based by default. Normal user message submission should invoke a concrete Akka `Agent` through the governed runtime path when a model-backed workstream turn is in scope.
- Use `AutonomousAgent` by default for internal/background agents when they are task-oriented: long-running investigations, autonomous monitoring/remediation, batch review, escalation processing, evaluator loops, internal specialist research, or model-driven multi-agent coordination.
- Use request-based `Agent` for one-shot internal helpers when there is no durable task identity, dependency graph, external observation, or independent lifecycle.
- Use `Workflow` when approval, retry, compensation, timeout, or deterministic order is the core requirement, even if one step calls an agent.
- Model every task start/query/result, delegation, handoff, team/moderation action, notification exposure, tool call, and external completion/failure as governed backend capabilities before implementation.
- When an AutonomousAgent task is visible to users, map task progress snapshots, task result surfaces, failed/rejected/blocked states, and notification-to-attention behavior into the owning workstream dashboard, My Account aggregate counts when relevant, and governed surface actions.

## Governance requirements

Autonomous Agents do not bypass this pack's managed runtime doctrine. Before exposing them in generated apps, preserve:

- tenant/customer-scoped task ids, agent instance ids, instructions, attachments, tools, snapshots, and notification streams;
- backend authorization for `runSingleTask`, `assignTasks`, task reads/results, external complete/fail, suspend/resume/terminate, and notification subscriptions;
- model policy and provider-secret boundaries for static definitions and dynamic setup;
- `ToolPermissionBoundary` checks before registering domain/component/MCP tools or governed loader tools;
- approval gates for side-effecting tools, higher-authority handoffs, remediation, policy changes, and external task completion;
- audit/work traces for task lifecycle, model iterations, prompt/skill/reference loads, tool invocations, denials, handoff/delegation/team/moderation events, notifications, and human/external decisions;
- fail-closed behavior when security, model, provider, or tool-boundary configuration is missing.

## Terminology guardrail

Akka autonomous `AgentDefinition` is the SDK definition returned by `AutonomousAgent.definition()` or supplied via `AgentSetup`. The skills pack's governed managed-agent `AgentDefinition` is a tenant-scoped domain/runtime profile that controls lifecycle, authority, model refs, prompt/skill/reference manifests, and tool boundaries. Always qualify which meaning you intend when both are in scope.
