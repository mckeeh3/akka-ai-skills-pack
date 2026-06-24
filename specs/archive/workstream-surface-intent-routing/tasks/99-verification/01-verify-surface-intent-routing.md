# Task: Verify Workstream Surface Intent Routing completion

## Objective

Verify the current task group and overall mini-project done state. Append bounded follow-up tasks and a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/conversation-capture.md`
- `specs/workstream-surface-intent-routing/pending-tasks.md`
- all sprint files
- backlog file
- completed task notes
- touched backend/frontend/app-description/seed/test files

## Skills

- akka-runtime-feature-verification
- akka-web-ui-testing
- akka-agent-work-trace

## Expected outputs

- Verification notes under `specs/workstream-surface-intent-routing/` if needed.
- Queue updates marking verification done only when the README done state is achieved.
- New bounded follow-up tasks plus a new terminal verification task if gaps remain.
- Commit for verification notes and queue updates.

## Required checks

- `git diff --check`
- targeted backend routing tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build` if production frontend output changed
- `mvn test` if shared backend workstream/agent behavior changed materially

## Done criteria

- `create organization "Org 1"` routes to a prefilled Organization Create surface through the real local backend/API/UI path and does not mutate before submit.
- All five core workstreams have catalog-backed deterministic routing at the stated scope.
- Agent familiarity material aligns with the no-direct-command non-goal.
- Verification records evidence, residual risks, and either closes the mini-project or appends precise follow-up work.
- Changes and queue update are committed.
