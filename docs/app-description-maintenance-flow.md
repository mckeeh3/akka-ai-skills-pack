# App Description Maintenance Flow

## Purpose

This document defines the default harness operating flow for maintaining the internal app-description artifact system.

It complements `docs/internal-app-description-architecture.md` by defining **how** the layers should be updated over time.

Reference example:
- `docs/examples/purchase-request-app-description/app-description/`

## Core interaction model

The user interacts through flexible prompt/response.
The harness interprets intent and updates internal description artifacts.
The user does not edit:
- generated code
- generated tests
- internal app-description artifacts

All app evolution enters as user input to the harness.

## Primary user intents

The harness should initially support two primary intents.

### 1. Change only the app description
This is the default.

Use when the user is:
- adding or changing capabilities
- revising workflows or rules
- clarifying tests
- tightening auth/security
- refining observability
- reporting bugs in intended behavior

### 2. Generate the app
Use only when the user explicitly asks to realize outputs or accepts a harness recommendation to do so.

## Default harness flow

Prefer this default sequence:

1. bootstrap the internal app-description tree if no usable root exists yet
2. normalize the user input when it is broad, mixed, or ambiguous
3. intake and route the user input
4. identify impacted description layers
5. update behavior-level meaning
6. update verification expectations
7. update auth/security if needed
8. update observability if needed
9. update traceability and impact understanding
10. assess readiness
11. when realization planning artifacts already exist, reconcile affected specs/backlogs/pending tasks before more coding
12. either stop at description maintenance or proceed to generation
13. answer review questions through summaries

## Change-only flow

When the user is not explicitly asking for realization, use this flow:

### Step 0. Bootstrap if needed
Use `app-description-bootstrap` when no usable `app-description/` root exists yet.

### Step 1. Normalize when needed
Use `app-description-input-normalization` when the input is broad, mixed, or ambiguous.

### Step 2. Route
Use `app-description-intake-router` to determine:
- description-only vs generation intent
- candidate affected layers
- whether clarification is needed

### Step 3. Update capabilities when scope changed
Use `app-description-capability-modeling` when the request changes business scope, actors, or intended outcomes.

### Step 4. Update behavior first
Use `app-description-behavior-specification` to update the app's meaning.

Behavior comes first because:
- tests need something to verify
- security needs something to protect
- observability needs something meaningful to expose

### Step 5. Update tests
Use `app-description-test-specification` to make the behavior explicit and verifiable.

### Step 6. Analyze change impact
Use `app-description-change-impact` to determine:
- which additional layers must move
- which traceability artifacts must update
- whether readiness should be reassessed
- whether later regeneration can stay localized

### Step 7. Update production-readiness layers
Update as needed:
- `app-description-auth-security`
- `app-description-observability`

These are not optional polish layers.
They are part of the app definition.

### Step 8. Update readiness
Use `app-description-readiness-assessment` to reflect the new current state.

### Step 9. Respond with review summaries when useful
Use:
- `app-description-change-summary`
- `app-description-readiness-summary`

## Generate-app flow

When the user explicitly asks to realize the app, use this flow:

### Step 1. Confirm current description state
Read the current authoritative layers.

### Step 2. Assess readiness
Use `app-description-readiness-assessment`.
Do not skip this step just because generation was requested.

### Step 3. Decide outcome
- if `not-ready`, explain blockers and continue description work
- if `ready-with-assumptions`, surface assumptions explicitly and proceed only if acceptable
- if `ready`, proceed

### Step 4. Generate
Use `app-generate-app`.
Generation must remain subordinate to the current description.

### Step 5. Run and evaluate if requested
Run tests, run the app, or prepare manual evaluation artifacts as requested and supported.

### Step 6. Summarize results
Use generation and readiness summaries to explain what happened.

## Layer update order

When multiple layers are affected, prefer this order:

1. `10-capabilities/`
2. `20-behavior/`
3. `30-tests/`
4. `40-auth-security/`
5. `50-observability/`
6. `70-traceability/`
7. `00-system/readiness-status.md`
8. `60-generation/` derived generation notes
9. `80-review/` optional summaries

This keeps the semantic source layers ahead of the derived layers.

## Change-impact rule

Every description change should trigger a mental or explicit change-impact pass.

The harness should ask:
- which capabilities changed?
- which behavior artifacts changed?
- which tests now need updates?
- are there new or changed security implications?
- are there new or changed observability implications?
- does readiness status change?
- would generation scope be localized or broad?
- do any existing specs, backlogs, task briefs, or pending tasks need to be updated, blocked, deferred, or superseded?

## Readiness rule

Readiness is a maintained state, not a one-time milestone.

The harness should reassess readiness after material description changes, especially when they affect:
- state transitions
- failure semantics
- auth/security
- observability
- operational tests

## Regeneration rule

Generation is allowed only as a realization step.

The harness must not let generation become the place where missing semantics are silently invented.
If generation exposes missing semantics, the harness should:
1. identify the semantic gap
2. update the description or ask for clarification
3. regenerate as needed

## Review rule

Human review is prompt/response.

The harness should prefer answering questions such as:
- what changed?
- what behavior changed?
- what is still missing?
- is the description ready?
- what was generated?

The review answer should be based on authoritative description layers, not on generated code diffs alone.

## Summary artifact rule

Review summaries are useful, but they are not the source of truth.

If stored at all, they should be treated as derived artifacts under `80-review/`.
The real app definition remains in the earlier authoritative layers.

## Minimal clarification rule

The harness should ask only the smallest clarifying question needed to avoid one of these mistakes:
- changing the wrong semantic layer
- inventing a security model implicitly
- inventing observability requirements implicitly
- generating too early
- treating a user review question as a generation request

## Full vs localized regeneration rule

When generation is requested, the harness should decide between:
- full regeneration
- localized regeneration

Use localized regeneration only when the changed description area and affected outputs are sufficiently clear.
Otherwise prefer full regeneration.

Localized regeneration is an optimization.
It must never preserve stale behavior that conflicts with the current description.

## Completion rule for a change request

A description change request is complete when:
- the relevant authoritative layers are updated
- linked verification expectations are updated
- linked security and observability expectations are updated when needed
- readiness has been reassessed or intentionally deferred
- affected realization specs/backlogs/task briefs/pending tasks are reconciled when they already exist
- the user can receive a coherent summary of what changed

A generation request is complete when:
- readiness was assessed
- generation scope was chosen deliberately
- outputs were generated
- execution steps and results were summarized clearly
- any remaining semantic gaps were surfaced back to the description layer

## Anti-patterns

Avoid:
- defaulting to code generation for every prompt
- updating tests without first clarifying behavior
- treating auth/security or observability as optional afterthoughts
- using generated code as the primary explanation of what changed
- leaving readiness stale after major description changes
- silently burying semantic uncertainty inside generation results

## Recommended next-doc usage

Use this document together with:
- `docs/description-first-application-doctrine.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-skills-plan-backlog.md`

The doctrine defines the why.
The architecture doc defines the artifact structure.
This document defines the maintenance flow.