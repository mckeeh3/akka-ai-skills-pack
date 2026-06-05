---
name: akka-agent-evaluation
description: Implement Akka Java SDK evaluator agents that return EvaluationResult for LLM-as-judge quality checks. Use when agent quality evaluation is the main concern.
---

# Akka Agent Evaluation

Use this skill when the main task is evaluating AI output quality with another agent.

Use `akka-agent-closed-loop-improvement` when evaluation results should become governed findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.


## Generated SaaS input contract

For generated full-stack AI-first SaaS agent work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- placement as a user-facing functional agent or a bounded internal agent, including owning workstream and structured surface placement when user-facing;
- capability id/class for each model request, tool call, output, workflow step, endpoint, or evaluation result;
- caller `AuthContext`, tenant/customer scope, roles/capabilities, allowed data/tools, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work trace fields, correlation ids, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing from prompt, memory, streaming, guardrail, or test mechanics.

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
