# TASK-UASNT-03-007: Add fullstack tests for User branch dedicated descendants

## Objective

Add focused backend/frontend/fullstack evidence that the dedicated User branch descendants work through the real runtime path and satisfy the navigation-tree contract.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java
- frontend/src/workstream-user-admin-vertical.contract.test.mjs
- frontend/src/workstream-surfaces.contract.test.mjs
- relevant backend/frontend files changed by `TASK-UASNT-03-005` and `TASK-UASNT-03-006`

## Skills

- akka-http-endpoint-testing
- akka-web-ui-testing
- akka-basic-user-admin

## Expected outputs

- Focused backend tests for dashboard/User Directory -> each dedicated User branch descendant -> User Directory return.
- Focused frontend contract tests for rendering and action metadata of those descendants.
- Evidence for forbidden/stale/system-message behavior, trace/correlation, audit expectations, and no frontend secret leakage.

## Required checks

- `git diff --check`
- `mvn -q -Dtest=WorkstreamServiceTest test`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Tests prove every required User branch descendant listed in the app-description has a runtime/backend/frontend path at starter scope.
- Tests prove branch-return behavior uses `action-user-admin-show-users` and returns `surface-user-admin-users` with correlation/trace evidence.
- Unauthorized, hidden, stale, and blocked states are safely denied or rendered as system-message/blocked states without fake success.
