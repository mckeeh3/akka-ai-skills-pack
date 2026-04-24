---
name: app-descriptions
description: Orchestrate description-first application work across intake, behavior, tests, auth/security, observability, readiness, generation, and review summaries. Use when the task spans more than one app-description concern.
---

# App Descriptions

Use this as the top-level skill for description-first application work.

This skill is for a harness operating model where the **application description is the source of truth** and generated code is a downstream projection.

## Goal

Maintain or realize an application from its internal app-description system in a way that is:
- description-first
- harness-optimized
- explicit about behavior, tests, security, and observability
- safe about readiness before generation
- suitable for prompt/response review instead of direct file editing

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/app-description-end-to-end-workflow-example.md`

Prefer these local examples and references:
- `../../docs/examples/purchase-request-app-description/README.md`
- `../../docs/examples/purchase-request-app-description/normalized-input-example.md`
- `../../docs/examples/purchase-request-app-description/app-description/00-system/app-manifest.md`
- `../../docs/examples/purchase-request-app-description/app-description/20-behavior/flows/01-submission-and-approval-flow.md`
- `../../docs/examples/purchase-request-app-description/app-description/30-tests/acceptance/01-purchase-request-acceptance.md`
- `../../docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md`
- `../../docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md`
- `../../docs/examples/purchase-request-app-description/app-description/70-traceability/capability-to-behavior-map.md`

## Use this skill when

Use this top-level skill when the task spans more than one description concern, such as:
- a new app idea that needs description-first maintenance
- a feature request that affects behavior, tests, and security together
- a bug fix that requires behavior correction plus regression coverage
- a request to assess readiness and possibly generate the app
- a review question asking what changed or whether the description is ready

If the task is already clearly narrowed to one description concern, load the focused companion skill directly.

## Companion skills

Load the companion skill that matches the current task:

- `app-description-bootstrap`
  - create the initial internal app-description tree for a new app or sparse early app idea
- `app-description-input-normalization`
  - convert flexible user input into a structured app-description delta envelope before routing or maintenance
- `app-description-intake-router`
  - classify flexible input into description-change, generation, mixed, or review intent
- `app-description-capability-modeling`
  - define or revise business capabilities, scope boundaries, actors, outcomes, and links to downstream layers
- `app-description-behavior-specification`
  - update behavior rules, transitions, invariants, forbidden behavior, and no-op semantics
- `app-description-test-specification`
  - update acceptance, regression, negative, idempotency, security, and operational verification expectations
- `app-description-change-impact`
  - determine which description layers, traceability artifacts, readiness state, and generated outputs are affected by a change
- `app-description-auth-security`
  - update identity, authorization, trust-boundary, sensitive-data, and denial-behavior rules
- `app-description-observability`
  - update logs, metrics, traces, audit events, health signals, alerts, and diagnosability expectations
- `app-description-readiness-assessment`
  - decide whether the current description is `not-ready`, `ready-with-assumptions`, or `ready`
- `app-generate-app`
  - realize the current app description as generated outputs
- `app-description-change-summary`
  - summarize what changed after a revision request
- `app-description-readiness-summary`
  - summarize why the description is or is not ready for generation

## Default flow

Prefer this sequence unless the task is already narrowly scoped:

1. bootstrap with `app-description-bootstrap` when no usable app-description tree exists yet
2. normalize input with `app-description-input-normalization` when the request is broad, mixed, or ambiguous
3. route input with `app-description-intake-router`
4. model capabilities with `app-description-capability-modeling` when scope or business outcomes changed
5. update behavior with `app-description-behavior-specification`
6. update tests with `app-description-test-specification`
7. run `app-description-change-impact` to identify cross-layer and realization implications
8. update security with `app-description-auth-security` when needed
9. update observability with `app-description-observability` when needed
10. assess readiness with `app-description-readiness-assessment`
11. realize outputs with `app-generate-app` only when generation is requested or accepted
12. answer review questions with `app-description-change-summary` and `app-description-readiness-summary`

## Layer model

The default internal app-description structure is:
- `00-system/`
- `10-capabilities/`
- `20-behavior/`
- `30-tests/`
- `40-auth-security/`
- `50-observability/`
- `60-generation/`
- `70-traceability/`
- `80-review/`

Use the architecture and maintenance-flow docs as the canonical reference for layer responsibilities and update order.

## Core rules

1. The app description is the source of truth.
2. Generated code is a projection, not the definition of the app.
3. Humans do not directly edit generated code or internal app-description artifacts.
4. Tests are part of the app description, not only post-hoc verification.
5. Auth/security and observability are first-class description concerns.
6. Readiness must be assessed before generation.
7. Localized regeneration is an optimization, not a conceptual requirement.
8. Review should focus on semantic change, not only file churn.

## Decision guide

### 1. The user is starting a new app description
Start with:
- `app-description-bootstrap`
- then `app-description-intake-router`

### 2. The user is revising app meaning
Start with:
- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then `app-description-capability-modeling` when capability scope is changing
- then `app-description-behavior-specification`
- then `app-description-test-specification`

### 3. The user is tightening production concerns
Start with:
- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then `app-description-auth-security` and/or `app-description-observability`

### 4. The user asks whether the app is ready
Start with:
- `app-description-readiness-assessment`
- then `app-description-readiness-summary`

### 5. The user asks to generate the app
Start with:
- `app-description-readiness-assessment`
- then `app-generate-app` if appropriate

### 6. The user asks what changed
Start with:
- `app-description-change-summary`

## Final review checklist

Before finishing, verify:
- the current task is routed to the smallest suitable companion skill
- behavior, tests, security, and observability are kept separate when they change separately
- readiness is not skipped before generation
- generation summaries are clearly distinguished from description changes
- review answers focus on app meaning and readiness rather than internal editing mechanics

## Response style

When answering:
- identify whether the task is description maintenance, readiness, generation, or review
- load only the smallest relevant companion skills
- keep the interaction natural and prompt/response oriented
- preserve description primacy throughout
