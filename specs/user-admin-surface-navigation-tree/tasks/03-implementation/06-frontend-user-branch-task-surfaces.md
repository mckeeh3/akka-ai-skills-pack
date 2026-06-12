# TASK-UASNT-03-006: Implement frontend User branch task/confirmation surfaces

## Objective

Render the backend-provided User branch task/confirmation descendants in the canonical workstream surface UI and expose branch-return controls back to User Directory.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- specs/user-admin-surface-navigation-tree/tasks/03-implementation/06-frontend-user-branch-task-surfaces.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- frontend/src/workstream/surfaces/**
- frontend/src/workstream/types/surfaces.ts
- frontend/src/workstream-user-admin-vertical.contract.test.mjs
- frontend/src/workstream-surfaces.contract.test.mjs

## Skills

- akka-web-ui-apps
- akka-web-ui-state-rendering
- akka-web-ui-forms-validation
- akka-web-ui-accessibility-responsive
- akka-web-ui-testing

## Expected outputs

- Frontend rendering support for the User branch descendants implemented by `TASK-UASNT-03-005`.
- Branch-return controls using backend-authored `action-user-admin-show-users` metadata.
- Contract tests for labels, trace/correlation rendering, forbidden/blocked copy, and frontend secret boundaries.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Each required User branch task/confirmation surface renders a purpose-specific state instead of falling back to broad JSON or inline list/detail mutation behavior.
- Branch returns are keyboard-accessible, use backend-provided action metadata, and do not infer authority in frontend code.
- Browser payload rendering avoids raw tokens, provider secrets, raw JWT/session data, and hidden cross-scope facts.
