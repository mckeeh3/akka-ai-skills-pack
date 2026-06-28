# Task AABP-05-001: Verify Agent Admin behavior-profile realization closure

## Goal

Run terminal verification for this task group and determine whether the mini-project done state is achieved. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `specs/agent-admin-behavior-profile-realization/README.md`
- `specs/agent-admin-behavior-profile-realization/conversation-capture.md`
- `specs/agent-admin-behavior-profile-realization/pending-tasks.md`
- `specs/agent-admin-behavior-profile-realization/backlog/01-agent-admin-behavior-profile-build-backlog.md`
- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- Relevant changed backend/frontend/tests.

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-agent-testing`
- `akka-agent-work-trace`

## Verification obligations

- Compare completed work against README done state, sprint goals, backlog, task briefs, app-description tests, unresolved blockers, and source-alignment state.
- Validate SaaS-admin-only authorization, denial safety, proposal-first lifecycle, activation separation, risk/authority-expansion handling, restore proposal, skill/reference lifecycle, behavior-profile versions, runtime loading, trace visibility, frontend surfaces, and secret boundaries at the implemented scope.
- Record readiness level achieved.
- If gaps remain, append specific bounded follow-up tasks and append a new terminal verification task after them.
- If no material gaps remain, update verification notes and source-alignment to close the mini-project at the achieved readiness level.

## Expected outputs

- `specs/agent-admin-behavior-profile-realization/verification-notes.md`
- Updated `pending-tasks.md` status and notes.
- Updated app-description source-alignment if closure or residuals are established.

## Done criteria

- All required checks run and results are recorded.
- Mini-project is either closed with evidence or left open with bounded follow-up tasks and a new terminal verification task.
- Changes are committed.

## Required checks

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Commit message

`Verify Agent Admin behavior profile realization`
