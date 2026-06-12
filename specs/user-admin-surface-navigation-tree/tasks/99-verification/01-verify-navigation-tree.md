# TASK-UASNT-99-001: Verify User Admin navigation tree mini-project

## Objective

Verify the current task group and overall mini-project done state. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/conversation-capture.md
- specs/user-admin-surface-navigation-tree/pending-tasks.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- Tests and validation notes produced by prior tasks

## Skills

- app-description-readiness-assessment
- app-description-change-impact
- akka-web-ui-testing
- akka-http-endpoint-testing

## Expected outputs

- `specs/user-admin-surface-navigation-tree/navigation-tree-verification.md`
- Updated `pending-tasks.md` with done status only if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain.

## Required checks

- `git diff --check`
- Run or review required checks from implementation tasks; rerun focused Maven/npm checks when needed to resolve uncertainty.

## Done criteria

- Verification explicitly compares completed work against README done state, backlog, app-description, tests, and conversation decisions.
- No material mini-project gaps remain, or follow-up tasks and a new terminal verification task are appended.
- Verification output and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: verification/review only.
- Role-specific dashboard / surface: verifies dashboard trunk, user branch, organization branch.
- Surface graph node/action edge: verifies dashboard -> directories -> descendants -> directories.
- Governed-tool id and exposure: verifies browser-tool/surface-action coverage.
- Capability id: verifies user-admin and organization-admin capability mappings.
- AuthContext / roles / tenant scope: verifies role/scope behavior and denials.
- Akka substrate: review plus focused checks.
- API / frontend / realtime path: verifies backend/API/frontend evidence.
- Audit/work trace requirements: verifies trace/correlation/audit expectations.
- Local validation path: documented verification and focused checks.
