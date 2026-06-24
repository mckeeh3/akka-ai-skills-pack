---
name: akka-agent-evaluation
description: Implement Akka Java SDK evaluator agents that return EvaluationResult for LLM-as-judge quality checks. Use when agent quality evaluation is the main concern.
---

# Akka Agent Evaluation

Use this skill when the main task is evaluating AI output quality with another agent.

Use `akka-agent-closed-loop-improvement` when evaluation results should become governed findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.


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
- `akka-context/sdk/agents/llm_eval.html.md`
- `akka-context/sdk/agents/memory.html.md`

## Use this pattern when

- a model response needs asynchronous quality checks
- workflow or consumer flows should emit evaluation signals
- prompt changes need regression coverage with realistic data
- results should appear in traces and metrics as evaluation outcomes
- the task is focused on evaluator-agent mechanics rather than the full proposal/approval/activation loop

## Core pattern

1. Implement the evaluator as an ordinary `Agent`.
2. Return a type that implements `EvaluationResult`.
3. Use `MemoryProvider.none()` unless historical context is part of the judgment.
4. Keep the model reply structured and map it into the final evaluation result.
5. Validate labels before translating them into pass/fail.

## Pattern to implement

Create or select a domain-specific evaluator `Agent` that demonstrates:
- structured model result
- label validation
- mapping into `EvaluationResult`

## Closed-loop routing

If evaluator output will alter prompts, skills, agent definitions, policies, rubrics, or authority boundaries, route to `akka-agent-closed-loop-improvement` after implementing or selecting the evaluator agent. Evaluators may draft findings and recommendations, but approval and activation must be handled by governed workflows and authorization checks outside the model.

## Review checklist

Before finishing, verify:
- the public result type implements `EvaluationResult`
- evaluator memory is disabled unless truly needed
- labels are validated before being converted to boolean pass/fail
- the evaluator is placed in workflow, consumer, or test flows intentionally
- proposal, activation, rollback, or self-improvement scope has been routed to `akka-agent-closed-loop-improvement`
