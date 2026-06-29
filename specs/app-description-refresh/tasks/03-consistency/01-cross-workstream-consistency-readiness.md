# TASK-ADR-03-001: Cross-workstream consistency and readiness pass

## Summary

Review the refreshed shared and workstream app-description artifacts for consistency with the current skills-pack graph contract and record readiness/alignment outcomes.

## Scope

May edit:

- `app-description/**` for consistency fixes only;
- `specs/app-description-refresh/**` for findings, queue updates, and follow-up tasks;
- `specs/runtime-validation/**` only to add/update scenario definitions or scenario-gap notes.

Do not implement runtime source changes.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/migration-sequence.md`
- `specs/app-description-refresh/workstreams/*.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/app-description-readiness-assessment/SKILL.md`
- refreshed `app-description/**`

## Skills

- `app-description-change-impact`
- `app-description-readiness-assessment`
- `app-description-readiness-summary`
- `app-descriptions`

## Expected outputs

- Cross-workstream consistency notes in `specs/app-description-refresh/consistency-readiness-review.md`.
- Any small consistency corrections needed in `app-description/**`.
- Follow-up tasks/questions for material gaps.
- Queue update.

## Consistency checklist

- Shared global definitions are not duplicated as divergent workstream definitions.
- Workstream bindings link to global actors/roles/workers/agents/tools/surfaces/policies/traces where appropriate.
- Human surface actions, confirmed human chat plans, AI agent tools, workflows, timers, consumers, APIs, MCP, and internal calls are actor adapters to governed tools.
- Governed-tool ids map to capability ids.
- AuthContext, tenant/organization language, roles/capabilities, denials, and frontend secret boundaries are consistent.
- Lifecycle and source-alignment files match the actual refresh result.
- Runtime-validation scenarios or explicit scenario gaps exist for each workstream.

## Required checks

- `git diff --check`
- `rg -n "stale-description-changed|source-alignment|runtime-validation|actor adapter|governed tool|capability" app-description/domains/core-starter/workstreams`

## Done criteria

- Refresh consistency is documented.
- All material gaps are fixed, queued, or blocked with questions.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description readiness assessment and consistency review.
- Workstream / functional agent: all five foundation workstreams.
- Governed-tool id and exposure: review only; no runtime exposure implemented.
- Capability id: all refreshed foundation capabilities.
- AuthContext / roles / tenant scope: consistency review.
- Akka substrate: app-description/specs only.
- Audit/work trace requirements: consistency review of trace obligations.
- Local validation path: `git diff --check` plus graph vocabulary proof.
