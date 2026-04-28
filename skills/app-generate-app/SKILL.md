---
name: app-generate-app
description: Realize the current app description as generated code, tests, and runnable outputs while preserving description primacy, making regeneration scope explicit, and reporting what was generated, executed, and left uncertain.
---

# App Generate App

Use this skill when the user explicitly asks to realize the current application description as generated outputs, or when the harness has assessed the description as ready and generation has been accepted.

This skill exists in a **description-first operating model**.
It treats code, tests, and runnable assets as projections from the authoritative app description.
It does not redefine the app.

## Goal

Consume the current app description and generate application outputs that are suitable for implementation evaluation, test execution, and app execution.

The skill should:
- preserve description primacy
- generate code and tests from the current description
- choose full regeneration or localized regeneration deliberately
- make assumptions and generation scope explicit
- support downstream running, testing, and manual evaluation
- report clearly what changed and what remains uncertain

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-readiness-assessment/SKILL.md`
- any currently relevant app-description layer artifacts identified by the harness
- `../../docs/web-ui-style-guide.md` and `app-description/55-ui/style-guide.md` when a browser frontend is in scope

If the user asks to generate and run, read the current build and execution entry points for the target project before acting.

## Use this skill when

The input sounds like:
- "generate the app"
- "generate the code"
- "regenerate from the current description"
- "ok, now generate the code and run the app"
- "realize the current description"

Use it only after the harness has either:
- determined the description is `ready`, or
- determined it is `ready-with-assumptions` and those assumptions are accepted or tolerable for the requested evaluation step

## Core operating rule

Generation is a realization step, not the source-of-truth step.

If generation reveals a semantic gap, the fix belongs in the app description, not in hand-edited generated code.

## Generation responsibilities

When generating, this skill must:
- identify the current description baseline
- identify whether generation is full or localized
- identify which outputs are in scope
- realize outputs from the description
- keep generated outputs consistent with the current description
- avoid treating generated code as authoritative
- report failures or ambiguities back as description-level issues where appropriate

## Regeneration scope rules

### Full regeneration
Prefer when:
- the app is early-stage or mostly disposable
- the description changed broadly across many layers
- prior outputs are unreliable or obsolete
- a clean realization is cheaper and safer than targeted patching

### Localized regeneration
Prefer when:
- the description change is well-localized
- the affected projections are clear
- preserving stable unaffected outputs reduces churn, cost, or review noise

Localized regeneration is an optimization only.
It must never override description correctness.

## Output categories

As applicable, generation may include:
- source code
- generated tests
- configuration or deployment assets
- runtime startup commands or scripts
- Akka-hosted web UI assets and TypeScript frontend modules when the app description includes `55-ui`, applying the selected `55-ui/style-guide.md`
- documentation or evaluation notes

The exact realization set depends on the current repository and user request. When a browser frontend is in scope, route realization through `akka-web-ui-apps` and its focused companion skills rather than treating the UI as static content only. Do not invent a visual theme during generation; if `55-ui/style-guide.md` or the specs style guide is missing/unselected, stop web UI generation and add or ask the pending style-selection question described in `../../docs/web-ui-style-guide.md`.

## Standard generation output shape

Use this response shape when summarizing generation:

```md
# App Generation Summary

## Generation basis
- description state:
- readiness state:
- assumptions used:

## Regeneration scope
- full | localized
- affected output areas:

## Generated or updated outputs
- ...

## Executed steps
- generation:
- tests:
- app run:

## Results
- passed:
- failed:
- not run:

## Remaining uncertainties
- ...

## Recommended next step
- ...
```

## Handoff behavior

After generation, route onward as needed:
- to `app-description-change-summary` if the user asks what changed
- to `app-description-readiness-summary` if the user asks why generation was considered acceptable
- back to description-maintenance skills if generation exposed missing semantics or unacceptable assumptions

## Clarification policy

Ask only the smallest questions needed to avoid an obviously wrong realization step.

Examples:
- "Do you want full regeneration or should I localize regeneration to the changed description area if possible?"
- "Should I stop after generation, or also run tests and start the app if available?"
- "Is this generation mainly for early evaluation, or do you want the strictest production-oriented realization possible from the current description?"

## Anti-patterns

Avoid:
- generating code directly from a vague prompt without honoring readiness assessment
- silently fixing generation issues by inventing semantics not present in the description
- treating prior generated code as authoritative over the current description
- performing manual-style code edits as if they were the correct response to semantic gaps
- hiding assumptions used during `ready-with-assumptions` generation

## Final review checklist

Before finishing, verify:
- the description basis for generation is explicit
- readiness state is explicit
- assumptions are explicit when used
- regeneration scope is explicit
- outputs in scope are listed clearly
- executed steps and results are reported clearly
- semantic gaps discovered during generation are surfaced as description issues, not buried in code changes

## Response style

When answering:
- state the generation basis first
- distinguish generation from description maintenance
- make regeneration scope explicit
- clearly separate generated outputs from executed validation steps
- keep the summary suitable for prompt/response review
