---
name: akka-agent-guardrails
description: Configure Akka Java SDK runtime guardrails for model requests and responses using TextGuardrail implementations and deployment-time config. Use when agent safety controls are the main concern.
---

# Akka Agent Guardrails

Use this skill when runtime validation of model input or output is the main concern.


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
- `akka-context/sdk/agents/guardrails.html.md`
- `akka-context/sdk/sanitization.html.md`

## Use this pattern when

- prompts or model replies must be validated at runtime
- certain agents or roles require mandatory safety checks
- the deployment should enforce governance regardless of application code paths
- logs, metrics, and traces should capture guardrail decisions automatically

## Core pattern

1. Implement `TextGuardrail`.
2. Optionally accept `GuardrailContext` in the constructor.
3. Read guardrail-specific config from `GuardrailContext`.
4. Configure guardrails under `akka.javasdk.agent.guardrails`.
5. Target agents by component id or `@AgentRole`.
6. Choose `report-only = true` for observation-only rollout, or `false` to block.

## Pattern to implement

Create a domain-specific guardrail and config pair that demonstrates:
- custom `TextGuardrail`
- config-driven blocked phrase or policy signal
- `src/main/resources/application.conf` binding with report-only rollout before blocking rollout

## Review checklist

Before finishing, verify:
- the configured class implements `TextGuardrail`
- agent ids or roles match the intended targets
- `use-for` is set to the correct request/response stages
- report-only vs blocking behavior is deliberate
