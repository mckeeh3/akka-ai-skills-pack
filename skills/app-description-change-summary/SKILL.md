---
name: app-description-change-summary
description: Summarize what changed in the app description after a user revision request, including behavior, tests, security, observability, readiness, and generation impact, for prompt/response review without file editing.
---

# App Description Change Summary

Use this skill when the human asks what changed after a revision request or when the harness needs to present a concise review of description updates.

This skill supports the review model where humans do not edit internal artifacts directly.
Review happens through prompt/response summaries generated from the harness's internal analysis.

## Goal

Produce a clear summary of what changed in the authoritative app description, including:
- behavioral changes
- test-specification changes
- auth/security changes
- observability changes
- readiness impact
- generation impact

The summary should help the human understand the consequence of their requested revision without exposing internal editing workflows as the primary interaction model.

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- any relevant updated description-layer artifacts identified by the harness
- any recent readiness or generation summaries if they exist

## Use this skill when

The input sounds like:
- "what changed?"
- "what changed based on my revision requests?"
- "summarize the updated description"
- "what behavior did you update?"
- "what is the impact of this revision?"

## Core operating rule

Summarize **semantic change**, not merely document churn.

The important question is not which internal artifacts changed.
The important question is what changed in the app's meaning, verification expectations, production constraints, readiness, and generation impact.

## What this skill must summarize

As applicable, include:
- changed capabilities or scope
- changed behavior and rules
- changed invariants or forbidden behavior
- changed tests and verification expectations
- changed auth/security rules
- changed observability requirements
- changed readiness posture
- changed generation implications or affected output areas
- remaining open questions or assumptions

## Standard change-summary shape

Use this response shape:

```md
# App Description Change Summary

## Requested revision
- ...

## Behavior changes
- ...

## Test changes
- ...

## Auth/security changes
- ...

## Observability changes
- ...

## Readiness impact
- ...

## Generation impact
- ...

## Remaining open questions or assumptions
- ...
```

## Summary rules

### 1. Lead with meaning
Start with what changed in app behavior or policy, not with file-level activity.

### 2. Separate layers clearly
Do not merge behavior, tests, security, and observability into one vague paragraph.
Show each affected layer distinctly.

### 3. Include unchanged critical areas only when useful
If a critical area did not change, do not pad the summary.
Mention unchanged areas only when they matter for review clarity.

### 4. Distinguish confirmed change from remaining ambiguity
If the revision left open questions, say so explicitly.

### 5. Surface generation impact in human terms
Explain whether the revision affects likely generated outputs, regeneration locality, or readiness to generate.

## Handoff rules

Route onward as needed:
- to `app-description-readiness-summary` if the user also wants to know whether the app is now ready to generate
- to `app-description-readiness-assessment` if readiness has not yet been evaluated after the change
- back to description-maintenance skills if the user continues revising behavior, tests, security, or observability

## Anti-patterns

Avoid:
- listing only internal document names that changed
- collapsing the summary into generic statements like "updated the specification"
- ignoring production-readiness changes because the user asked a broad review question
- hiding open questions that materially affect readiness or generation
- confusing generated output changes with source-of-truth changes

## Final review checklist

Before finishing, verify:
- the requested revision is identified
- semantic behavior changes are summarized clearly
- test, security, and observability impacts are separated clearly
- readiness impact is addressed when relevant
- generation impact is addressed when relevant
- open questions or assumptions are surfaced clearly

## Response style

When answering:
- be concise but semantically specific
- emphasize what changed in the app's meaning
- organize by layer
- avoid file-editing language unless it is specifically useful
- keep the summary suitable for natural prompt/response review
