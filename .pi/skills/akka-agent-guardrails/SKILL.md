---
name: akka-agent-guardrails
description: Configure Akka Java SDK runtime guardrails for model requests and responses using TextGuardrail implementations and deployment-time config. Use when agent safety controls are the main concern.
---

# Akka Agent Guardrails

Use this skill when runtime validation of model input or output is the main concern.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/guardrails.html.md`
- `akka-context/sdk/sanitization.html.md`
- `../../../src/main/java/com/example/application/CompetitorMentionGuard.java`
- `../../../src/main/resources/application.conf`

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

## Repository examples

- `CompetitorMentionGuard`
  - custom `TextGuardrail`
  - config-driven blocked phrase
- `src/main/resources/application.conf`
  - report-only guardrail bound to `activity-agent`

## Review checklist

Before finishing, verify:
- the configured class implements `TextGuardrail`
- agent ids or roles match the intended targets
- `use-for` is set to the correct request/response stages
- report-only vs blocking behavior is deliberate
