# TASK-ADICM-00-001: Create app-description intent-compiler migration queue

## Purpose

Create the mini-project planning scaffold and pending-task queue for reconstructing root `app-description/` around the current intent compiler model.

## Required reads

- `AGENTS.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- `.agents/skills/docs/intent-compiler.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- current conversation context

## Expected outputs

- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/conversation-capture.md`
- `specs/app-description-intent-compiler-migration/pending-tasks.md`
- `specs/app-description-intent-compiler-migration/sprints/*.md`
- `specs/app-description-intent-compiler-migration/backlog/*.md`
- `specs/app-description-intent-compiler-migration/tasks/**/*.md`

## Required checks

- `git diff --check`

## Done criteria

- The migration approach is captured.
- Future tasks are bounded and ordered.
- The terminal verification task can append follow-up work if gaps remain.
- Changes are committed with the queue update.

## Vertical workstream contract

- Workstream / functional agent: docs-only cross-cutting planning for core starter app-description migration
- Attention category or non-attention reason: non-runtime planning scaffold
- Role-specific dashboard / surface: none; planning only
- Surface graph node/action edge: none
- Governed-tool id and exposure: none
- Capability id: app-description/current-intent migration scope
- AuthContext / roles / tenant scope: preserve root starter security doctrine in future tasks; no runtime auth change
- Akka substrate: docs/specs only
- API / frontend / realtime path: none
- Audit/work trace requirements: planning provenance in conversation capture
- Local validation path: `git diff --check`
