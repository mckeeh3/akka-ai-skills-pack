---
name: app-description-change-impact
description: Determine which internal app-description layers, traceability artifacts, readiness state, and generated output areas are affected by a requested description change, and decide whether regeneration can stay localized or should broaden.
---

# App Description Change Impact

Use this skill when a description change has been proposed or applied and the harness needs to determine what else must move.

This skill formalizes the change-impact pass described in the maintenance flow.
It helps the harness keep the internal app-description system consistent and decide whether future realization can use localized regeneration safely.

## Goal

Analyze a requested or completed description change and produce an impact result that:
- identifies impacted authoritative layers
- identifies impacted traceability artifacts
- identifies whether readiness must be reassessed
- identifies likely affected generated output areas
- identifies whether existing specs, backlogs, task briefs, or pending tasks need reconciliation
- recommends localized or broader regeneration scope
- prevents stale description links, stale planning artifacts, or stale generated outputs from surviving a semantic change

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../app-descriptions/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../app-description-auth-security/SKILL.md`
- `../app-description-observability/SKILL.md`
- `../app-description-readiness-assessment/SKILL.md`

Prefer these example references when present:
- `../../docs/examples/purchase-request-app-description/app-description/60-generation/regeneration-map.md`
- `../../docs/examples/purchase-request-app-description/app-description/70-traceability/capability-to-behavior-map.md`
- `../../docs/examples/purchase-request-app-description/app-description/70-traceability/behavior-to-tests-map.md`
- `../../docs/examples/purchase-request-app-description/app-description/70-traceability/change-impact-map.md`

## Use this skill when

The task sounds like:
- "what else does this change affect?"
- "update change impact after this revision"
- "can regeneration stay localized?"
- "which layers must be updated because of this behavior change?"
- "what generated outputs are affected by this description change?"

Use it after or alongside changes to:
- capabilities
- behavior
- tests
- auth/security
- observability
- readiness-driving assumptions

## Core operating rule

Do not treat a change as local just because the original edit was small.

Impact is determined by semantic dependency, not by file-diff size.
A one-line behavior change may still require:
- new or changed tests
- new security rules
- new observability expectations
- readiness reassessment
- broader regeneration than expected

## What this skill must determine

For each change, determine as applicable:
- which capability artifacts are impacted
- which behavior artifacts are impacted
- which test artifacts are impacted
- which auth/security artifacts are impacted
- which observability artifacts are impacted
- which traceability maps must change
- whether `00-system/readiness-status.md` must be updated
- which generation surfaces are likely affected
- which specs/backlogs/task briefs/pending tasks are likely affected when they already exist
- whether regeneration can remain localized or should broaden

## Standard impact output shape

Use this response shape:

```md
# App Description Change Impact

## Change basis
- ...

## Impacted authoritative layers
- capabilities:
- behavior:
- tests:
- auth/security:
- observability:

## Impacted derived layers
- traceability:
- readiness:
- generation maps:
- review summaries:
- specs/backlogs/pending tasks:

## Likely affected generated outputs
- ...

## Regeneration recommendation
- localized | broad | full
- rationale:

## Required next updates
1. ...
2. ...
```

## Impact analysis rules

### 1. Start from semantics, not files
Ask what the change means for the app, then identify which artifacts must reflect that meaning.

### 2. Always inspect verification impact
Any important change in behavior, security, or observability should trigger a test-impact check.

### 3. Treat security and observability as dependency surfaces
Behavior changes may force security and observability updates even when the user did not explicitly ask for them.

### 4. Reassess readiness when meaning changed materially
If the change alters core behavior, failure semantics, production constraints, or test coverage expectations, readiness should usually be revisited.

### 5. Prefer localized regeneration only when the dependency chain is clear
If the harness cannot confidently bound the affected outputs, recommend broad or full regeneration instead.

### 6. Update traceability maps explicitly
When capability-to-behavior, behavior-to-tests, or change-impact maps are now stale, mark them for update.

## Regeneration recommendation guide

### Recommend `localized` when
- the changed semantic area is narrow
- affected authoritative layers are clearly bounded
- affected output surfaces are known
- no broad architectural or policy assumption shifted

### Recommend `broad` when
- multiple authoritative layers changed in connected ways
- the impact is larger than one bounded feature area
- readiness or production concerns shifted meaningfully

### Recommend `full` when
- the change affects foundational assumptions
- traceability is incomplete or stale
- the harness cannot safely preserve unaffected outputs
- a clean regeneration is lower risk than partial preservation

## Handoff rules

Route onward as needed:
- to `app-description-test-specification` when verification impact exists
- to `app-description-auth-security` when access, identity, boundary, or data-protection implications exist
- to `app-description-observability` when logging, metrics, audit, trace, or diagnosability implications exist
- to `app-description-readiness-assessment` when readiness likely changed
- to `akka-change-request-to-spec-update` when existing specs/backlogs/pending tasks must be reconciled after a bounded change
- to `akka-revised-prd-reconciliation` when the change basis is a revised/replacement PRD
- to `app-generate-app` only after the impacted description layers are updated and readiness is acceptable

## Clarification policy

Ask only the smallest questions needed to avoid a wrong impact boundary.

Examples:
- "Is this rule change limited to one capability, or does it apply across the whole app?"
- "Does this change alter only user-visible behavior, or also access control and audit expectations?"
- "Should repeated-request handling change everywhere this action appears, or only in this one flow?"

## Anti-patterns

Avoid:
- assuming no test impact because the user mentioned only behavior
- assuming no security impact because the request did not mention auth explicitly
- recommending localized regeneration without a clear dependency chain
- treating review summaries as authoritative instead of derived
- leaving traceability maps stale after a material semantic change

## Final review checklist

Before finishing, verify:
- impacted authoritative layers are named explicitly
- impacted derived layers are named explicitly
- readiness impact is called out when relevant
- likely generated outputs are named at a useful level
- the regeneration recommendation is explicit and justified
- the next required updates are actionable

## Response style

When answering:
- summarize the change basis first
- separate authoritative vs derived impact clearly
- make regeneration scope recommendations explicit
- keep the result usable as a downstream maintenance and realization handoff
