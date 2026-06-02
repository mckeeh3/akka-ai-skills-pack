# Sprint 01: Doctrine and App-Description Ownership

## Objective

Define the canonical workstream expertise model and make app-description artifacts responsible for capturing which skills, reference documents, capabilities, tools, surfaces, authority rules, traces, and tests make each functional agent a workstream expert.

## Scope

Likely source files:

- `docs/agent-workstream-application-architecture.md`
- new or updated `docs/workstream-expertise-model.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md` only if surface/action references need clarification
- `skills/app-description-readiness-assessment/SKILL.md`
- `templates/ai-first-saas-starter/app-description/app-description/12-workstreams/functional-agents.md`

## Deliverables

- Canonical definition of a workstream expert bundle.
- Clear ownership for `12-workstreams/workstream-expertise/**` or equivalent app-description artifacts.
- Functional-agent modeling guidance requires prompt intent, skills, reference docs, manifests, tool boundaries, capabilities, traces, and tests.
- Readiness guidance fails functional-agent readiness when expertise artifacts are absent or explicitly deferred.
- Sprint review records any unresolved model questions before runtime guidance changes.

## Checks

- `git diff --check`
- Text search proving updated docs/skills mention workstream expertise, skills, reference documents, manifests, capabilities, traces, and tests.
