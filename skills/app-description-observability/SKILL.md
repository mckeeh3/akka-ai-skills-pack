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
- link observability expectations back to behavior, security, and tests
- improve readiness for generation and manual evaluation

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
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

Use it for:
- logging expectations
- metrics expectations
- traceability requirements
- audit-event requirements
- health-signal requirements
- alert-worthy condition definition
- failure diagnosis requirements
- visibility expectations for long-running or async flows

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
- trace continuity expectations across requests, workflows, and async steps
- health indicators or status signals
- alert-worthy thresholds or conditions
- diagnosability expectations for failures or stuck work
- actor, tenant, or correlation context needed for diagnosis
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
When flows cross boundaries, define what correlation, request, tenant, workflow, or causation context must remain visible.

### 5. Respect security boundaries
Observability must not leak secrets or protected data.
Call out masking, redaction, or restricted-access requirements where needed.

### 6. Define diagnosability, not just raw telemetry
Specify what operators should be able to answer, such as:
- what failed?
- for whom?
- where in the flow?
- is it retrying, stuck, or terminal?

### 7. Make alerts selective and meaningful
Capture conditions that should trigger attention, not a vague desire for monitoring.

## Clarification policy

Ask only the smallest questions needed to make operational expectations useful.

Good questions include:
- "Which failures must be immediately visible versus only diagnosable on inspection?"
- "Which business actions require audit records rather than ordinary logs?"
- "What correlation key should connect this flow across steps or services?"
- "Which conditions should produce alerts rather than dashboards only?"
- "Are there data fields that must be masked or omitted from logs and audit events?"

## Handoff to other skills

When the observability update is established, route onward as needed:
- to `app-description-behavior-specification` if the observability request reveals missing process semantics
- to `app-description-test-specification` to define evidence-verification cases
- to `app-description-auth-security` if audit, masking, or restricted visibility raises access-control concerns
- to `app-description-readiness-assessment` when the user is asking whether the description is sufficiently complete to realize

## Anti-patterns

Avoid:
- reducing observability to "add logs"
- omitting correlation requirements for async or multi-step flows
- defining alerts without saying what condition triggers them
- specifying diagnostics without respecting sensitive-data restrictions
- assuming audit is the same thing as debug logging
- leaving failure visibility implicit for important workflows or integrations

## Final review checklist

Before finishing, verify:
- important logs or audit events are explicit where relevant
- metrics are explicit where relevant
- trace/correlation expectations are explicit where relevant
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
