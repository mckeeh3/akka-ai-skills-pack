---
name: akka-agents
description: Orchestrate Akka Java SDK Agent work across durable behavior profiles, governed behavior documents, prompt design, structured responses, tools, memory, streaming, workflow orchestration, guardrails, evaluation, and testing. Use when the task spans more than one agent concern.
---

# Akka Agents

Use this broad skill to route multi-concern Akka Agent work. For a single implementation concern, load the focused skill directly.

## Required reading

- `../docs/agent-component-selection-guide.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../docs/agent-workstream-application-architecture.md` when the agent is user-facing in a workstream
- `../docs/governed-agent-substrate.md` when managed/generative SaaS agent behavior is in scope
- `../references/generated-saas-runtime-completion.md`

## Routing

| Need | Focused skill |
|---|---|
| Write an Akka request-based Agent component | `akka-agent-component` |
| Structured/typed model output | `akka-agent-structured-responses` |
| Local function tools | `akka-agent-tools` |
| Akka component tools | `akka-agent-component-tools` |
| Remote MCP tools | `akka-agent-mcp-tools` |
| Session memory | `akka-agent-memory` |
| Streaming responses | `akka-agent-streaming` |
| Workflow calls to agents | `akka-agent-orchestration` |
| Guardrails | `akka-agent-guardrails` |
| Evaluation agents | `akka-agent-evaluation` |
| Agent tests | `akka-agent-testing` |
| Managed profiles/AgentDefinition | `akka-agent-behavior-profiles` |
| Prompt/skill/reference/model/tool/work-trace governance | corresponding `akka-agent-*governance`, `akka-agent-tool-boundaries`, `akka-agent-work-trace` skills |
| Durable background typed tasks | `akka-autonomous-agents` and autonomous-agent focused skills |

## Generated SaaS requirements

For model-backed workstream behavior, normal runtime must use a concrete Akka `Agent` path with:

- backend-selected `AuthContext` and authorization
- active `AgentDefinition`/behavior profile when managed agents are in scope
- governed prompt/skill/reference/model/tool-boundary resolution where applicable
- registered tools via the Akka Agent effect APIs
- provider/model configuration fail-closed behavior
- durable work/audit traces
- tests that prove allowed, denied, and failure paths

Do not replace this with direct provider calls, deterministic canned responses, prompt-only security, frontend-only decisions, or fixture/model-less normal runtime.

## Workstream tool interpretation

For user-facing workstream agents, distinguish the governed workstream tool from the Akka tool mechanism. A governed workstream tool is the capability-backed operation with AuthContext, tenant/customer scope, schemas, idempotency, policy/approval, audit/work trace, and tests. Akka `@FunctionTool`, component tools, MCP tools, and loader tools are only model-facing exposure adapters for the subset of governed tools assigned to the AI-backed actor through the active workstream tool catalog and `ToolPermissionBoundary`.

Human-requested chat execution is a separate human-backed adapter: the selected workstream agent may interpret a natural-language request, propose a detailed tool plan, bind explicit human confirmation to that exact plan, and then execute each governed-tool invocation through backend authorization and per-tool transaction/idempotency rules. That path does not grant the AI-backed agent extra authority, and prompt, skill, or reference text cannot expand the catalog. Trace records must distinguish `surface_action`, `human_chat_tool_plan`, and `agent_tool_call` sources and preserve `requestedBy`, `confirmedBy`, denials, partial failures, and result surfaces.

## Design checklist

Before implementation, decide:

- request-based Agent vs AutonomousAgent vs Workflow + Agent
- input/output contract and fallback/error shape
- prompt layers and governed document refs
- tool set and tool permission boundary
- memory/session behavior
- AuthContext, tenant/customer/data scope, approval gates, and side-effect authority
- trace facts and redaction rules
- endpoint/workflow/consumer caller and validation path

## Validation

Use `akka-agent-testing` plus component/endpoint/workflow tests appropriate to the caller. At minimum cover successful model response, provider/config missing fail-closed behavior, authorization denial, tool denial, malformed/unsafe output fallback, and trace emission.
