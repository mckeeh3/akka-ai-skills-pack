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
- `../../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../../src/main/java/com/example/application/SelectorAgent.java`
- `../../src/main/java/com/example/application/PlannerAgent.java`
- `../../src/main/java/com/example/application/SummarizerAgent.java`
- `../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`

## Use this pattern when

For generated SaaS apps, workflow-supervised agent orchestration must carry AuthContext, tenant/customer scope, policy/approval references, and trace identifiers through the workflow state or command payload so retries and resumes cannot lose authorization boundaries.

Before choosing workflow-supervised orchestration, apply the one-agent vs agent-team decision guide in `ai-first-saas-agent-team-design`: keep one governed skilled agent when responsibilities share authority, tool boundary, model config, lifecycle, steward, memory, audit, and approval needs; split into specialized agents or evaluator agents when those boundaries differ; add a workflow supervisor when durable retries, pauses, handoffs, approval gates, or progress visibility are required.

- agent calls need retries and durable recovery
- several agents collaborate on the same request because they have different authority, tool boundary, model config, lifecycle, steward, memory, risk, audit, or approval needs
- a workflow should own the shared session id
- a caller needs a final result separate from intermediate agent outputs

## Core pattern

1. Prefer workflows to directly orchestrate agents.
2. Use long step timeouts for AI calls.
3. Configure bounded retries with `RecoverStrategy.maxRetries(...)`.
4. Reuse the workflow id as the shared session id.
5. Persist intermediate outputs in workflow state when later steps need them.
6. Do not model agent-to-agent coordination as tools.
7. Persist actor, tenant/customer, membership/capability snapshot or lookup reference, policy version, approval gate, and audit/work-trace ids needed by later agent steps.
8. Reauthorize before consequential tool calls or final commits, especially after long pauses or retries.
9. Route high-risk, low-confidence, cross-scope, or policy-bound outputs to human approval workflows instead of autonomous commits.

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
- workflow state preserves actor, tenant/customer scope, policy/approval/audit references, and forbidden/exception paths
- consequential agent outputs are reauthorized and audited before final side effects
