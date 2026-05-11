---
name: app-description-observability
description: Update the authoritative observability layer of the app description by capturing logging, metrics, traces, audit events, health signals, alerts, and diagnosability expectations without generating code.
---

# App Description Observability

Use this skill when the user introduces or revises observability requirements in the application description.

This skill maintains the **observability layer** of the internal app description.
It does not generate code.
It defines what operational evidence and visibility the realized app must provide.

## Goal

Create or update observability-oriented app-description artifacts that:
- define what operational evidence must exist
- capture required logs, metrics, traces, and audit events
- define health and alert-worthy conditions
- define diagnosability expectations for important failures and workflows
- preserve AI-first work traces, decision traces, policy/tool/data access evidence, evaluations, digests, and outcome visibility when in scope
- link observability expectations back to behavior, security, and tests
- improve readiness for generation and manual evaluation

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../app-description-auth-security/SKILL.md`

## Use this skill when

The input sounds like:
- "we need to know when this fails"
- "add audit events for..."
- "track latency and throughput for..."
- "this flow must be diagnosable in production"
- "alert if retries exceed..."
- "capture traces across this workflow"
- "record who changed what and when"
- "trace agent tool calls, policy invocations, approvals, and outcomes"
- "show supervisors why an agent paused, escalated, or acted"

Use it for:
- logging expectations
- metrics expectations
- traceability requirements
- audit-event requirements
- health-signal requirements
- alert-worthy condition definition
- failure diagnosis requirements
- visibility expectations for long-running or async flows
- work-trace, decision-trace, policy-invocation, tool-call, data-access, evaluation, digest, and outcome-link evidence for AI-first flows

## Core operating rule

Treat observability as part of the app's meaning, not as a later operational add-on.

This skill should define what evidence must exist so that humans and systems can:
- detect important failures
- explain important state changes
- inspect important flows
- audit important actions
- understand runtime health

## What this skill must capture

For each requested change, identify and describe as applicable:
- important events that must be logged or audited
- metrics that must be recorded
- trace continuity expectations across requests, workflows, async steps, agent actions, tool calls, data access, approvals, policy use, and outcomes
- health indicators or status signals
- alert-worthy thresholds or conditions
- diagnosability expectations for failures or stuck work
- actor, tenant, agent, goal, plan, decision, policy version, tool, data resource, or correlation context needed for diagnosis
- sensitive-data constraints on observability output
- dependencies on behavior, security, and test layers

## Standard observability output shape

Use this response shape when updating or summarizing observability changes:

```md
# Observability Specification Update

## Requested change
- ...

## Logs and audit events
- ...

## Metrics
- ...

## Traces and correlation
- ...

## AI-first audit, evaluation, digest, and outcome evidence, if in scope
- ...

## Health signals
- ...

## Alert-worthy conditions
- ...

## Diagnosability expectations
- ...

## Open questions and assumptions
- ...

## Affected linked layers
- behavior:
- tests:
- auth/security:
```

## Observability modeling rules

### 1. Observe business-critical behavior, not everything
Focus on events and evidence that matter for correctness, operations, support, audit, and failure diagnosis.

### 2. Distinguish logs, metrics, traces, and audit events
Do not treat all observability as generic logging.
Be explicit about the evidence form.

### 3. Connect observability to behavior
Capture observability around meaningful domain transitions, failures, retries, approvals, expiries, and integrations.

### 4. Include correlation context
When flows cross boundaries, define what correlation, request, tenant, workflow, goal, plan, task, decision, policy, agent, tool, or causation context must remain visible.

### 5. Separate operational telemetry from AI-first audit facts
For delegated work, define durable work-trace and decision-trace evidence in addition to logs, metrics, and technical traces.
Capture policy invocations, approval gates, evidence/rationale links, tool/data access, evaluator results, overrides, and outcome links when they affect explainability or governance.

### 6. Support supervision and digest surfaces
When command centers, decision queues, async digests, governance centers, or audit investigations are in scope, define the read-model evidence those surfaces need rather than only raw telemetry.

### 7. Respect security boundaries
Observability must not leak secrets or protected data.
Call out masking, redaction, or restricted-access requirements where needed.

### 8. Define diagnosability, not just raw telemetry
Specify what operators should be able to answer, such as:
- what failed?
- for whom?
- where in the flow?
- is it retrying, stuck, or terminal?

### 9. Make alerts selective and meaningful
Capture conditions that should trigger attention, not a vague desire for monitoring.

## Clarification policy

Ask only the smallest questions needed to make operational expectations useful.

Good questions include:
- "Which failures must be immediately visible versus only diagnosable on inspection?"
- "Which business actions require audit records rather than ordinary logs?"
- "What correlation key should connect this flow across steps or services?"
- "Which conditions should produce alerts rather than dashboards only?"
- "Are there data fields that must be masked or omitted from logs and audit events?"
- "Which agent actions, tool calls, policy invocations, approvals, overrides, or outcome links must be durable audit facts?"
- "Which evidence must command centers, decision queues, digests, or audit investigations expose?"

## Handoff to other skills

When the observability update is established, route onward as needed:
- to `app-description-behavior-specification` if the observability request reveals missing process semantics
- to `app-description-test-specification` to define evidence-verification cases
- to `app-description-auth-security` if audit, masking, agent/tool/data access, or restricted visibility raises access-control concerns
- to `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, or `ai-first-saas-outcomes-metrics` when work traces, supervision surfaces, digests, evaluations, or outcome loops need focused modeling
- to `app-description-readiness-assessment` when the user is asking whether the description is sufficiently complete to realize

## Anti-patterns

Avoid:
- reducing observability to "add logs"
- omitting correlation requirements for async or multi-step flows
- defining alerts without saying what condition triggers them
- specifying diagnostics without respecting sensitive-data restrictions
- assuming audit is the same thing as debug logging
- omitting agent/tool/data/policy/decision/outcome evidence from AI-first flows that require explainability
- leaving failure visibility implicit for important workflows or integrations

## Final review checklist

Before finishing, verify:
- important logs or audit events are explicit where relevant
- metrics are explicit where relevant
- trace/correlation expectations are explicit where relevant
- AI-first work trace, decision trace, policy invocation, evaluation, digest, and outcome evidence is explicit where relevant
- health or alert conditions are explicit where relevant
- diagnosability expectations are explicit for important failures
- security or masking implications are called out
- linked impacts on behavior, tests, and auth/security are called out
- no code-generation step was assumed

## Response style

When answering:
- summarize the operational visibility change first
- separate logs, metrics, traces, audit, and alerts clearly
- describe the evidence the app must provide, not implementation libraries
- keep rules precise and operationally meaningful
- call out linked test and security implications explicitly
