# TASK-UASNT-03-004: Add fullstack navigation tree tests

## Objective

Add or strengthen backend/frontend/contract tests that prove the User Admin navigation tree works through real workstream API/UI paths at the stated local scope.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- relevant backend and frontend tests changed by prior tasks

## Skills

- akka-http-endpoint-testing
- akka-web-ui-testing
- akka-basic-user-admin

## Expected outputs

- Focused backend tests for surface/action payloads and authorization.
- Focused frontend/contract tests for dashboard -> directory -> descendant -> directory traversal.
- Optional validation notes if any runtime smoke remains blocked by local prerequisites.

## Required checks

- `git diff --check`
- Focused `mvn test` for changed User Admin/workstream tests.
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Tests prove both User Directory and Organization Directory branches.
- Tests cover unauthorized Organization Directory omission/denial, stale/deep-link safe denial, trace/correlation rendering, and no frontend secret leakage at focused scope.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: validation for surface graph navigation.
- Role-specific dashboard / surface: dashboard, user branch, organization branch.
- Surface graph node/action edge: dashboard -> directories -> descendants -> directories.
- Governed-tool id and exposure: browser-tool/surface-action test coverage.
- Capability id: user-admin and organization-admin read/navigation capabilities.
- AuthContext / roles / tenant scope: SaaS Owner/App Admin, Tenant Admin, Customer Admin, Auditor where fixtures support.
- Akka substrate: backend tests and frontend workstream tests.
- API / frontend / realtime path: workstream API and React frontend tests.
- Audit/work trace requirements: test trace/correlation links and denial behavior.
- Local validation path: focused Maven and npm tests.
