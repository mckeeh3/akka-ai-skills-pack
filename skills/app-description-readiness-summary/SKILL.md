---
name: app-description-readiness-summary
description: Summarize why the current app description is or is not ready for generation, including blocking gaps, acceptable assumptions, and the recommended next step, for prompt/response review.
---

# App Description Readiness Summary

Use this skill when the human wants a concise explanation of whether the current app description is ready for generation and why.

This skill is a review-oriented companion to `app-description-readiness-assessment`.
It explains the readiness result in prompt/response form.

## Goal

Produce a concise readiness summary that explains:
- whether the app description is ready, ready-with-assumptions, or not ready
- which factors most influenced that result
- what the blocking gaps or acceptable assumptions are
- whether the harness recommends continuing description work or proceeding to generation

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-readiness-assessment/SKILL.md`
- the latest readiness-assessment result if available
- any updated behavior, test, auth/security, and observability summaries that informed the assessment

## Use this skill when

The input sounds like:
- "is it ready?"
- "why do you think the description is ready?"
- "what is still missing before generation?"
- "summarize readiness"
- "why not generate yet?"

## Core operating rule

Explain readiness in terms of **semantic sufficiency**, not in terms of whether code could be guessed.

The summary should help the user understand the practical decision:
- continue refining the description, or
- proceed to generation now

## What this skill must summarize

As applicable, include:
- current readiness state
- strongest evidence supporting that state
- major remaining gaps
- acceptable assumptions, if any
- the most important next step
- whether generation is recommended now

## Standard readiness-summary shape

Use this response shape:

```md
# App Description Readiness Summary

## Current state
- not-ready | ready-with-assumptions | ready

## Why
- ...

## Key gaps or assumptions
- ...

## Recommendation
- continue description work | generate app

## Suggested next step
- ...
```

## Summary rules

### 1. Start with the state
State `not-ready`, `ready-with-assumptions`, or `ready` immediately.

### 2. Explain the decision, not the procedure
Focus on the missing or sufficient semantics, not on internal processing steps.

### 3. Distinguish blockers from assumptions
If the state is `not-ready`, identify blocking gaps.
If the state is `ready-with-assumptions`, identify the acceptable assumptions explicitly.

### 4. Keep the next step actionable
The user should know whether to keep refining behavior, tests, security, or observability, or whether to move to generation.

### 5. Be willing to recommend generation
If the description is mature enough, say so clearly.

## Handoff rules

Route onward as needed:
- to `app-generate-app` if the state is `ready` or accepted `ready-with-assumptions` and the user wants realization
- to `app-description-behavior-specification`, `app-description-test-specification`, `app-description-auth-security`, or `app-description-observability` if the summary reveals the next missing area
- to `app-description-change-summary` if the user asks what changed since the last revision

## Anti-patterns

Avoid:
- burying the readiness state deep in the response
- explaining readiness as a vague feeling instead of a reasoned conclusion
- mixing blocking gaps and acceptable assumptions together without distinction
- recommending more description work without naming the missing area
- withholding a generation recommendation when the description is clearly mature enough

## Final review checklist

Before finishing, verify:
- the readiness state is stated first
- the explanation focuses on semantic sufficiency
- blockers or assumptions are explicit
- the recommendation is explicit
- the next step is actionable and named clearly

## Response style

When answering:
- be brief and direct
- state the readiness state first
- explain the decisive factors
- make the recommendation unambiguous
- keep the summary suitable for natural prompt/response review
