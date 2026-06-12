# TASK-UASNT-03-002: Implement dashboard and User Directory branch frontend navigation

## Objective

Implement or revise frontend User Admin surfaces so the dashboard opens the User Directory branch and all user-branch descendants expose a working Show users/Back to users action.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
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

- Frontend User Admin dashboard/list/detail/task surface revisions.
- Missing user-branch descendant surfaces implemented at the stated scope or explicitly blocked.
- Frontend tests for user branch traversal and back navigation.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Dashboard -> User Directory works through backend-provided action metadata.
- User branch descendant -> Show users/Back to users works and preserves safe context/filter metadata.
- Loading, empty, error, forbidden, stale/reconnect, and responsive/accessibility expectations are covered at focused scope.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: user access administration attention and task routing.
- Role-specific dashboard / surface: `surface-user-admin-dashboard`, `surface-user-admin-users`, user branch descendants.
- Surface graph node/action edge: dashboard -> user directory; user descendants -> user directory.
- Governed-tool id and exposure: browser-tool/surface-action adapters from backend payloads.
- Capability id: `user_admin.list_members` and user branch capabilities.
- AuthContext / roles / tenant scope: selected SaaS Owner/Tenant/Customer scope; backend denial rendered safely.
- Akka substrate: frontend workstream surfaces using existing HTTP/workstream API.
- API / frontend / realtime path: React/Vite workstream shell and API client path.
- Audit/work trace requirements: trace ids rendered; no frontend-only authority.
- Local validation path: frontend tests/typecheck.
