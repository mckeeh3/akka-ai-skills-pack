# TASK-ADR-00-001: Create app-description refresh mini-project

## Summary

Create the planning scaffold for refreshing root `app-description/` to the current installed skills-pack graph contract, including umbrella docs, independent workstream migration plans, backlog, task briefs, and pending queue.

## Scope

- Create `specs/app-description-refresh/**`.
- Do not modify `app-description/**` or runtime source.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/AGENTS.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- current conversation context

## Done criteria

- Mini-project intent and done state are captured.
- Independent workstream migration plans exist.
- Pending queue includes bounded sequential tasks and a terminal verification task.
- `git diff --check` passes.
- Scaffold is committed.

## Vertical workstream contract

- Lifecycle / readiness target: planning-only, `decomposed-to-tasks`.
- Workstream / functional agent: cross-cutting app-description refresh; no single runtime workstream implementation.
- Governed-tool id and exposure: none; planning-only.
- Capability id: foundation app-description/source-alignment scope.
- AuthContext / roles / tenant scope: preserved as planning constraints, not implemented.
- Akka substrate: docs/specs only.
- Audit/work trace requirements: no runtime trace; queue requires future description trace obligations.
- Local validation path: `git diff --check`.
