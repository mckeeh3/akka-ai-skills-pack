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
- identifies impacted authoritative layers, including `12-workstreams/functional-agents.md`, `12-workstreams/surfaces-index.md`, `12-workstreams/surface-contracts/**`, `12-workstreams/workstream-expertise/`, `55-ui/`, and the required `15-operating-model/` for generated AI-first SaaS apps
- identifies impacted traceability artifacts
- identifies whether readiness must be reassessed
- identifies likely affected generated output areas
- treats role-specific dashboard attention, human surface graph nodes/edges, internal workstream agent graph delegation, workstream expertise, and governed-tool exposure mappings as first-class impact drivers
- identifies whether existing specs, backlogs, task briefs, or pending tasks need reconciliation
- recommends localized or broader regeneration scope
- prevents stale description links, stale planning artifacts, or stale generated outputs from surviving a semantic change

## Required reading

Read these first if present:
- `../../../AGENTS.md`
- `../README.md`
- `../docs/description-first-application-doctrine.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for tracing capability contract changes across linked layers and exposure surfaces
- `../docs/workstream-expertise-model.md` for tracing per-functional-agent expert bundle changes across skills, references, manifests, boundaries, traces, and tests
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../app-descriptions/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../app-description-auth-security/SKILL.md`
- `../app-description-observability/SKILL.md`
- `../app-description-ui/SKILL.md`
- `../app-description-readiness-assessment/SKILL.md`
- `../ai-first-saas/SKILL.md` when the change involves delegated work, agents, decisions, governance, supervision, audit, or outcomes

Prefer these current generated-SaaS references when present:
- the target project `app-description/README.md` plus `../docs/core-ai-first-saas-foundation.md`
- target project `app-description/60-generation/regeneration-map.md`
- target project `app-description/70-traceability/functional-agent-to-capability-map.md`
- target project `app-description/70-traceability/surface-to-capability-map.md`
- target project `app-description/70-traceability/capability-to-behavior-map.md`
- target project `app-description/70-traceability/behavior-to-tests-map.md`
- target project `app-description/70-traceability/change-impact-map.md`

Use current target-project app-description files and starter templates for traceability mechanics; do not depend on removed historical domain examples.

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
- UI/supervision surfaces
- AI-first operating-model semantics such as delegated work, authority, policies, decisions, traces, outcomes, or readiness-driving assumptions
- workstream expertise semantics such as prompt intent, governed skill documents, reference documents, expertise manifests, loader access, tool boundaries, trace obligations, governance owners, default-content upgrade behavior, or expertise tests

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
- which capability artifacts are impacted, including changes to governed-tool ids, actors/callers, AuthContext, schemas, side effects, idempotency, policy/approval, audit/trace, selected exposure surfaces, or browser-tool/agent-tool/internal-tool mappings
- which workstream graph artifacts are impacted: role-specific dashboard surfaces, attention categories, human surface graph nodes/edges, system-message surfaces, cross-workstream surface requests, internal workstream agent graph nodes/delegations/results/escalations, or notification/projection effects
- which `12-workstreams/workstream-expertise/` artifacts are impacted: bundle scope, prompt intent, skill/reference ids, compact manifest entries, capability map, `ToolPermissionBoundary`, authority profile, loader denials, governed-tool explanations, trace obligations, governance owner, default-content upgrade behavior, or tests
- which `15-operating-model/` artifacts are impacted for generated AI-first SaaS: goals, delegated work, retained human authority, agent/team responsibilities, policies, approval gates, decisions, exceptions, evidence, traces, learning, or outcomes
- which behavior artifacts are impacted
- which test artifacts are impacted
- which auth/security artifacts are impacted, especially authority boundaries and permission enforcement
- which observability artifacts are impacted, especially audit/work/decision traces, policy invocations, tool/data-access events, and outcome metrics
- which UI artifacts are impacted, especially `55-ui` workstream shell, functional-agent rail, composer, structured surface rendering, human surface graph realization, capability-backed browser-tool actions, frontend API contracts, supervision, decision-card, governance, digest, goal-to-execution, and audit/trace surfaces
- which traceability maps must change, including functional-agent-to-dashboard, surface-to-capability/governed-tool, functional-agent-to-expertise, expertise-to-capability/surface, expertise-to-observability, and expertise-to-tests relationships when present
- whether `00-system/readiness-status.md` must be updated
- which generation surfaces are likely affected, including default prompt/skill/reference resources, manifest fixtures, governed-document import code, loader/tool-boundary implementation, frontend governance surfaces, and tests when workstream expertise changed
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
- workstream-expertise:
- operating-model:
- behavior:
- tests:
- auth/security:
- observability:
- UI:

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

### 2. Preserve AI-first operating-model context
When delegated operations are in scope, do not let a change bypass `15-operating-model/`.
Detect whether it changes goals, plans, delegated authority, retained human authority, agent/team responsibility, policies, approval gates, decision evidence, exception handling, audit traces, learning loops, outcomes, or supervision surfaces.

### 3. Always inspect verification impact
Any important change in behavior, security, observability, UI supervision, or AI-first operating semantics should trigger a test-impact check.

### 4. Treat security, observability, and UI as dependency surfaces
Behavior or operating-model changes may force security, observability, and UI updates even when the user did not explicitly ask for them.

### 5. Reassess readiness when meaning changed materially
If the change alters core behavior, failure semantics, role-specific dashboard attention, human surface graph edges, internal workstream agent graph delegation, governed-tools, browser-tool/agent-tool/internal-tool exposure, delegated authority, workstream expert bundles, skill/reference manifests, tool boundaries, policies, approvals, decisions, traces, outcomes, production constraints, UI supervision surfaces, or test coverage expectations, readiness should usually be revisited.

### 6. Prefer localized regeneration only when the dependency chain is clear
If the harness cannot confidently bound the affected outputs, recommend broad or full regeneration instead.

### 7. Update traceability maps explicitly
When capability-to-behavior, operating-model-to-behavior, behavior-to-tests, or change-impact maps are now stale, mark them for update.

## Regeneration recommendation guide

### Recommend `localized` when
- the changed semantic area is narrow
- affected authoritative layers are clearly bounded
- affected output surfaces are known
- no broad architectural, authority, policy, decision, trace, outcome, or UI-supervision assumption shifted

### Recommend `broad` when
- multiple authoritative layers changed in connected ways
- the impact is larger than one bounded feature area
- readiness or production concerns shifted meaningfully
- functional-agent expertise, manifest, reference, tool-boundary, or trace requirements changed

### Recommend `full` when
- the change affects foundational assumptions
- traceability is incomplete or stale
- the harness cannot safely preserve unaffected outputs
- a clean regeneration is lower risk than partial preservation

## Handoff rules

Route onward as needed:
- to `app-description-functional-agent-modeling` when the change creates or revises a functional agent's workstream expert bundle, governed skills, reference documents, manifest entries, loader behavior, tool boundaries, traces, governance owner, or expertise tests
- to `app-description-capability-modeling` when the change creates or revises a capability contract, operation/query, caller, AuthContext, schema, side effect, idempotency rule, approval gate, audit/trace obligation, or exposure surface
- to `app-description-test-specification` when verification impact exists
- to `app-description-auth-security` when access, identity, boundary, or data-protection implications exist
- to `app-description-observability` when logging, metrics, audit, trace, outcome, or diagnosability implications exist
- to `app-description-ui` when `55-ui` workstream shell, rail/composer, structured surface rendering, supervision, decision, governance, digest, goal-to-execution, or audit UI implications exist
- to focused AI-first companion skills when operating-model semantics changed and need decomposition before app-description updates
- to `app-description-readiness-assessment` when readiness likely changed
- to `akka-change-request-to-spec-update` when existing specs/backlogs/pending tasks must be reconciled after a bounded change, especially when expertise changes require new or revised tasks for governed documents, manifests, loaders, boundaries, UI/governance surfaces, generation/default-content assets, or tests
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
- treating a workstream expertise change as prompt-only and skipping capability, governance, auth/security, observability, UI, generation, traceability, or test propagation
- recommending localized regeneration without a clear dependency chain
- treating review summaries as authoritative instead of derived
- leaving traceability maps stale after a material semantic change

## Final review checklist

Before finishing, verify:
- impacted authoritative layers are named explicitly, including `10-capabilities/`, `12-workstreams/workstream-expertise/`, and `15-operating-model/` for generated AI-first SaaS apps
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
