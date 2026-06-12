# TASK-UASNT-99-002: Re-verify User Admin navigation tree mini-project

## Objective

Re-run the mini-project verification after the appended User branch descendant implementation and test tasks. Mark the mini-project complete only if no material gaps remain; otherwise append bounded follow-up tasks and another terminal verification task.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/conversation-capture.md
- specs/user-admin-surface-navigation-tree/pending-tasks.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- Tests and validation notes produced by prior tasks

## Skills

- app-description-readiness-assessment
- app-description-change-impact
- akka-web-ui-testing
- akka-http-endpoint-testing

## Expected outputs

- Updated `specs/user-admin-surface-navigation-tree/navigation-tree-verification.md` or a follow-up verification addendum.
- Updated `pending-tasks.md` with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain.

## Required checks

- `git diff --check`
- Run or review required checks from implementation tasks; rerun focused Maven/npm checks when needed.

## Done criteria

- Verification compares completed work against README done state, backlog, app-description, tests, and conversation decisions.
- Every required dashboard, User branch, Organization branch, system-message, auth/forbidden, trace/correlation, audit, and frontend secret-boundary expectation is either complete or has a newly appended bounded task.
- Verification output and queue update are committed.
