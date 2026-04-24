---
name: app-description-input-normalization
description: Normalize flexible user input into a structured app-description delta envelope covering intent, capability, behavior, tests, auth/security, observability, review requests, and realization requests before routing or maintenance.
---

# App Description Input Normalization

Use this skill when the harness needs to turn flexible user language into a consistent internal change envelope before routing, maintenance, impact analysis, or generation decisions.

This skill does not primarily decide the final next step.
Its job is to convert messy input into a stable normalized representation that later skills can use reliably.

## Goal

Produce a normalized input result that:
- preserves the user's intent in a structured form
- separates description-change intent from generation and review intent
- extracts candidate deltas across behavior, tests, auth/security, and observability
- distinguishes confirmed statements from inferred assumptions
- records ambiguity explicitly instead of hiding it
- gives downstream skills a stable basis for routing and maintenance

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../app-descriptions/SKILL.md`
- `../app-description-bootstrap/SKILL.md`
- `../app-description-intake-router/SKILL.md`

Prefer these example references when present:
- `../../docs/examples/purchase-request-app-description/normalized-input-example.md`
- `../../docs/examples/purchase-request-app-description/app-description/00-system/app-manifest.md`
- `../../docs/examples/purchase-request-app-description/app-description/10-capabilities/01-submit-and-approve-purchase-requests.md`
- `../../docs/examples/purchase-request-app-description/app-description/20-behavior/flows/01-submission-and-approval-flow.md`
- `../../docs/examples/purchase-request-app-description/app-description/30-tests/acceptance/01-purchase-request-acceptance.md`
- `../../docs/examples/purchase-request-app-description/app-description/40-auth-security/identity-and-authorization.md`
- `../../docs/examples/purchase-request-app-description/app-description/50-observability/logs-metrics-traces-and-alerts.md`

## Use this skill when

The task sounds like:
- "interpret this request first"
- "normalize this input before updating the description"
- "extract the real deltas from this user prompt"
- "separate behavior, test, security, and generation intent"
- "turn this revision request into a structured maintenance input"

Use it especially when the input is:
- broad
- mixed across multiple concerns
- partly ambiguous
- combining revision, review, and realization intent
- likely to affect more than one description layer

## Core operating rule

Normalize the user's meaning without overcommitting beyond what they actually said.

A good normalized result:
- captures explicit requests faithfully
- identifies probable implied deltas carefully
- separates confirmed facts from assumptions
- leaves open questions visible

## What this skill must extract

From user input, derive as applicable:
- primary intent
- secondary intents
- capability or scope delta
- behavior delta
- test delta
- auth/security delta
- observability delta
- change-impact or readiness interest
- generation request details
- review/explanation request details
- explicit user constraints or preferences
- open questions created by ambiguity

## Normalized envelope

Use this normalized shape internally:

```md
# Normalized App Description Input

## Raw input summary
- ...

## Primary intent
- bootstrap | description-change | generate-app | review | mixed

## Secondary intents
- ...

## Confirmed deltas
- capabilities:
- behavior:
- tests:
- auth/security:
- observability:

## Candidate inferred deltas
- ...

## Realization request
- none | generate | run-tests | run-app | mixed

## Review request
- none | what-changed | readiness | impact | other

## Constraints and preferences
- ...

## Open questions
- ...
```

## Normalization rules

### 1. Separate intent from content
A user may ask for generation while also changing the app description.
Record both instead of forcing one to erase the other.

### 2. Separate confirmed from inferred
Use `confirmed` for what the user clearly requested.
Use `candidate inferred deltas` for plausible implications that still need confirmation or later skill validation.

### 3. Preserve cross-layer separation
Do not collapse these into one bucket:
- behavior
- tests
- auth/security
- observability
- realization
- review

### 4. Normalize to app-description language, not code language
Prefer terms like:
- capability
- behavior rule
- invariant
- acceptance case
- authorization rule
- audit requirement
instead of jumping to classes, endpoints, or frameworks.

### 5. Surface ambiguity explicitly
If the input leaves unclear whether something is a behavior change, test clarification, or security rule, record the ambiguity instead of guessing silently.

### 6. Preserve user priority signals
If the user says things like "for now", "must", "optional", "later", or "just for evaluation", carry those into constraints and preferences.

## Intent classification guide

### `bootstrap`
Use when the user is starting a new app description or no usable internal root exists.

### `description-change`
Use when the user is primarily changing capabilities, behavior, tests, security, or observability.

### `generate-app`
Use when the user explicitly asks to realize outputs now.

### `review`
Use when the user is mainly asking for explanation, readiness, or change summary.

### `mixed`
Use when more than one of the above clearly applies in one prompt.

## Handoff rules

Route onward as needed:
- to `app-description-bootstrap` when the normalized intent is `bootstrap`
- to `app-description-intake-router` when routing is still needed after normalization
- to `app-description-capability-modeling` when capability scope, actors, or intended outcomes are the dominant delta
- to focused maintenance skills when normalization already isolates the dominant delta clearly
- to `app-description-change-impact` when the input is explicitly asking about affected areas or regeneration scope
- to `app-description-readiness-assessment` when the input explicitly asks whether generation is appropriate

## Clarification policy

Ask only the smallest questions needed to reduce material ambiguity in the normalized envelope.

Examples:
- "Is your main goal to revise the description only, or to revise it and then generate the app now?"
- "Is this primarily a new behavior rule, or are you mainly defining how it should be tested?"
- "Should I treat this as a security requirement, or as a general business rule that applies to all users?"

## Anti-patterns

Avoid:
- collapsing mixed intent into a single simplistic label
- converting uncertain implications into confirmed requirements
- normalizing directly into code-level tasks
- losing user preference signals like phased scope or evaluation-only intent
- treating review questions as hidden generation requests

## Final review checklist

Before finishing, verify:
- primary intent is explicit
- secondary intents are preserved when present
- confirmed vs inferred deltas are separated
- behavior, tests, security, and observability are separated
- realization and review requests are separated
- ambiguity is recorded as open questions instead of guessed away

## Response style

When answering:
- summarize the input briefly
- show the normalized intent and deltas clearly
- keep the structure compact but explicit
- make it usable as an immediate handoff to routing or maintenance skills
