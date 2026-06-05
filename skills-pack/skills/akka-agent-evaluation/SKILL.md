---
name: akka-agent-evaluation
description: Implement Akka Java SDK evaluator agents that return EvaluationResult for LLM-as-judge quality checks. Use when agent quality evaluation is the main concern.
---

# Akka Agent Evaluation

Use this skill when the main task is evaluating AI output quality with another agent.

Use `akka-agent-closed-loop-improvement` when evaluation results should become governed findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

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
