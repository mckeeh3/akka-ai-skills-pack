---
name: app-description-readiness-summary
description: Summarize why the current app description is or is not ready for generation, including blocking gaps, acceptable assumptions, and the recommended next step, for prompt/response review.
---

# App Description Readiness Summary

Use this skill when the human wants a concise explanation of whether the current app description is ready for generation and why.

This skill is a review-oriented companion to `app-description-readiness-assessment`.
It explains the readiness result in prompt/response form.

## Lifecycle classification

- Phase: interview.
- Kind: review/summary.
- Family: app-description.
- Living-graph contract: readiness summaries explain whether the app-description current-intent graph is sufficient across workers, execution harnesses, actor adapters, governed tools, capabilities, tests, security, observability, UI, and realization links.
- Build/compile handoff: if generation is recommended, state that the next step should follow `../docs/app-description-to-code-compile-contract.md` rather than treating the summary as implementation authority.

## Goal

Produce a concise readiness summary that explains:
- whether the app description is ready, ready-with-assumptions, or not ready
- which factors most influenced that result
- what the blocking gaps or acceptable assumptions are, including missing required global/domain/workstream operating-model semantics for generated AI-first SaaS apps
- whether the harness recommends continuing description work or proceeding to generation

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../app-description-readiness-assessment/SKILL.md`
- `../ai-first-saas/SKILL.md` when delegated work, agents, decisions, governance, supervision, audit, or outcomes are in scope
- the latest readiness-assessment result if available
- any updated operating-model, behavior, test, auth/security, observability, and UI summaries that informed the assessment

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
- AI-first operating-model completeness when delegated work, authority, policies, decisions, traces, outcomes, or supervision surfaces are in scope
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
If the state is `ready-with-assumptions`, identify the acceptable assumptions explicitly and state the narrowed scope.
For AI-first apps, do not summarize missing authority, approval, policy, evidence, trace, outcome, supervision, auth/security, API/UI wiring, agent/provider configuration, audit/work-trace, tests, or local validation semantics as harmless assumptions if generation would have to invent them or if a named runtime feature depends on them.

### 4. Keep the next step actionable
The user should know whether to keep refining behavior, tests, security, or observability, or whether to move to generation.

### 5. Be willing to recommend generation
If the description is mature enough, say so clearly.

## Handoff rules

Route onward as needed:
- to `app-generate-app` if the state is `ready` or accepted `ready-with-assumptions` for a named narrowed scope with no runtime-critical assumptions and the user wants realization
- to `ai-first-saas` or focused AI-first companion skills when the missing area is operating-model interpretation
- to `app-description-behavior-specification`, `app-description-test-specification`, `app-description-auth-security`, `app-description-observability`, or `app-description-ui` if the summary reveals the next missing area
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
- blockers or assumptions are explicit, including AI-first operating-model gaps when applicable
- the recommendation is explicit
- the next step is actionable and named clearly

## Response style

When answering:
- be brief and direct
- state the readiness state first
- explain the decisive factors
- make the recommendation unambiguous
- keep the summary suitable for natural prompt/response review
