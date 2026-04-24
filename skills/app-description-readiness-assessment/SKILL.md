---
name: app-description-readiness-assessment
description: Assess whether the current app description is sufficiently complete and unambiguous for reliable generation, testing, and manual evaluation, and recommend whether to continue description work or proceed to app realization.
---

# App Description Readiness Assessment

Use this skill when the harness needs to decide whether the current app description is complete enough to realize the app.

This skill does not primarily change the app description.
It evaluates the current description state and determines whether generation would be responsible, premature, or acceptable with assumptions.

## Goal

Assess whether the current application description is sufficiently complete to support reliable:
- code generation
- test generation
- app execution
- manual testing and evaluation

The result should tell the harness and the user whether to:
- continue description work
- proceed with assumptions called out explicitly
- proceed to app generation now

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
- `../app-description-observability/SKILL.md`

## Use this skill when

The input sounds like:
- "is the description ready?"
- "can we generate the app now?"
- "what is still missing before generation?"
- "assess whether this is sufficient for implementation"
- "ok, now generate the code and run the app"

Use it before generation when the harness must decide whether the current description is:
- incomplete
- sufficient with assumptions
- sufficiently complete

## Core operating rule

Readiness is about **semantic completeness**, not about whether code could be guessed.

A description is ready only when it is clear enough that generation is likely to preserve intended behavior without hiding major unresolved decisions.

This skill should resist premature generation when important behavior, test, security, or observability semantics are still undefined.

## Readiness dimensions

Assess the current description across these dimensions:

### 1. Behavior completeness
Check whether the app meaning is sufficiently defined:
- core capabilities
- major flows and state changes
- invariants
- failure behavior
- forbidden behavior
- no-op or idempotency rules where relevant

### 2. Test completeness
Check whether important behavior is backed by explicit verification expectations:
- acceptance cases
- regression cases
- negative cases
- repeated-request behavior
- failure-path expectations

### 3. Auth/security completeness
Check whether required production security semantics are defined:
- identity and trust model
- authorization rules
- trust boundaries
- sensitive-data rules
- denial behavior

### 4. Observability completeness
Check whether required operational evidence is defined:
- logs and audit events
- metrics
- traces and correlation
- health signals
- alert-worthy conditions
- diagnosability expectations

### 5. Generation stability
Check whether remaining ambiguity would likely cause incorrect or unstable generated outputs.

## Allowed outcomes

Return exactly one of these states:

### `not-ready`
Use when important semantics are still too incomplete or ambiguous for responsible generation.

### `ready-with-assumptions`
Use when the description is mostly sufficient, but some limited assumptions remain.
These assumptions must be listed explicitly and judged acceptable for a useful realization step.

### `ready`
Use when the description is sufficiently complete for reliable generation and downstream evaluation.

## Standard readiness output shape

Use this response shape:

```md
# App Description Readiness Assessment

## Overall state
- not-ready | ready-with-assumptions | ready

## Behavior completeness
- status:
- notes:

## Test completeness
- status:
- notes:

## Auth/security completeness
- status:
- notes:

## Observability completeness
- status:
- notes:

## Remaining assumptions or gaps
- ...

## Recommendation
- continue description work | generate app

## Suggested next skill or skill sequence
1. ...
2. ...
```

## Assessment rules

### 1. Prefer explicit gaps over silent optimism
If a critical area is underspecified, say so directly.
Do not mark the description ready just because generation is technically possible.

### 2. Weight missing production concerns appropriately
Missing auth/security or observability details may block readiness even if core behavior is mostly defined.

### 3. Allow limited assumptions only when localized
`ready-with-assumptions` is valid only when the remaining assumptions are:
- few
- explicit
- low-risk
- unlikely to distort the app's core behavior

### 4. Consider manual evaluation intent
If the user mainly wants a rough generated app for early evaluation, readiness may tolerate more assumptions than a production-grade generation step, but those assumptions must still be surfaced.

### 5. Recommend generation proactively when justified
If the description is sufficiently mature, this skill may recommend moving on to generation even when the user has not yet explicitly asked.

## Handoff rules

Route onward as follows:
- if `not-ready`, route to the most relevant missing description skills:
  - `app-description-behavior-specification`
  - `app-description-test-specification`
  - `app-description-auth-security`
  - `app-description-observability`
- if `ready-with-assumptions`, route to:
  - explicit assumption confirmation if needed
  - then `app-generate-app` if generation is requested or accepted
- if `ready`, route to:
  - `app-generate-app` when realization is requested or accepted

## Example readiness questions

Ask only when necessary:
- "Is repeated submission expected to succeed idempotently or fail?"
- "Are there caller roles or tenant boundaries not yet described?"
- "What operational evidence is required when this flow fails or times out?"
- "Are the current acceptance and regression cases enough to define expected behavior?"

## Anti-patterns

Avoid:
- treating vague behavior as ready because the model can improvise
- ignoring production concerns because they are not code yet
- hiding important assumptions inside a `ready` result
- blocking generation over trivial missing details that do not materially affect correctness
- recommending generation without listing the basis for readiness

## Final review checklist

Before finishing, verify:
- the result uses one of the three allowed states
- behavior completeness was assessed explicitly
- test completeness was assessed explicitly
- auth/security completeness was assessed explicitly
- observability completeness was assessed explicitly
- remaining assumptions or gaps are listed clearly
- the recommendation is explicit
- the next skill or sequence is named clearly

## Response style

When answering:
- state the readiness result first
- be direct about blocking gaps
- distinguish critical gaps from acceptable assumptions
- keep the recommendation actionable
- if generation is appropriate, say so plainly
