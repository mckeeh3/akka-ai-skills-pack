---
name: akka-agent-evaluation
description: Implement Akka Java SDK evaluator agents that return EvaluationResult for LLM-as-judge quality checks. Use when agent quality evaluation is the main concern.
---

# Akka Agent Evaluation

Use this skill when the main task is evaluating AI output quality with another agent.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/llm_eval.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `../../../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`

## Use this pattern when

- a model response needs asynchronous quality checks
- workflow or consumer flows should emit evaluation signals
- prompt changes need regression coverage with realistic data
- results should appear in traces and metrics as evaluation outcomes

## Core pattern

1. Implement the evaluator as an ordinary `Agent`.
2. Return a type that implements `EvaluationResult`.
3. Use `MemoryProvider.none()` unless historical context is part of the judgment.
4. Keep the model reply structured and map it into the final evaluation result.
5. Validate labels before translating them into pass/fail.

## Repository example

- `ActivityAnswerEvaluatorAgent`
  - structured model result
  - label validation
  - mapping into `EvaluationResult`

## Review checklist

Before finishing, verify:
- the public result type implements `EvaluationResult`
- evaluator memory is disabled unless truly needed
- labels are validated before being converted to boolean pass/fail
- the evaluator is placed in workflow, consumer, or test flows intentionally
