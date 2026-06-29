# TASK-ADR-99-001: Terminal app-description refresh verification

## Summary

Verify whether the app-description refresh mini-project done state has been achieved. If material gaps remain, append bounded follow-up tasks and a new terminal verification task instead of declaring completion.

## Scope

Review only:

- `specs/app-description-refresh/**`
- refreshed `app-description/**`
- relevant `specs/runtime-validation/**` scenario definitions or gaps
- active specs references touched by this mini-project

Do not implement runtime source changes.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/conversation-capture.md`
- `specs/app-description-refresh/migration-sequence.md`
- `specs/app-description-refresh/pending-tasks.md`
- `specs/app-description-refresh/backlog/01-app-description-refresh-build-backlog.md`
- `specs/app-description-refresh/tasks/**/*.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/docs/pending-task-queue.md`
- `.agents/skills/app-description-readiness-assessment/SKILL.md`

## Skills

- `app-description-readiness-assessment`
- `app-description-readiness-summary`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- `specs/app-description-refresh/terminal-verification.md`
- Queue status updates.
- If incomplete: appended bounded follow-up tasks and a new terminal verification task.

## Required checks

- `git diff --check`
- Graph coverage proof across app-description workstreams.
- Queue proof showing no pending non-verification gaps remain, or explicit appended follow-up tasks.

Suggested proof commands:

```bash
find app-description/domains/core-starter/workstreams -maxdepth 3 -type f | sort
rg -n "worker|actor adapter|governed tool|capability|source-alignment|runtime-validation|trace" app-description/domains/core-starter/workstreams
rg -n "status: pending|status: blocked" specs/app-description-refresh/pending-tasks.md
```

## Done criteria

- Terminal verification compares completed work against README done state, workstream plans, backlog, task briefs, and any blocked questions.
- Mini-project is declared complete only if no material refresh gaps remain.
- If incomplete, follow-up tasks are appended before the terminal verification task is marked done.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: terminal verification, non-runtime.
- Workstream / functional agent: all five foundation workstreams.
- Governed-tool id and exposure: verification only.
- Capability id: all refreshed foundation capabilities.
- AuthContext / roles / tenant scope: verification only.
- Akka substrate: app-description/specs only.
- Audit/work trace requirements: verify trace obligations exist; no runtime trace emitted.
- Local validation path: `git diff --check` plus graph/queue proof.
