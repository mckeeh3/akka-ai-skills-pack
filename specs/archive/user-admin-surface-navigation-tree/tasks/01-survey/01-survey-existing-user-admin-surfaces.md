# TASK-UASNT-01-001: Survey existing User Admin and Organization Admin surfaces

## Objective

Inventory existing User Admin and Organization Admin app-description, backend, frontend, fixture, and test surfaces before implementation. Classify each surface/artifact as usable as-is, revise, remove/deprecate, or missing/new relative to the dashboard-trunk/directory-branch design.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/conversation-capture.md
- specs/user-admin-surface-navigation-tree/backlog/01-user-admin-navigation-tree-build-backlog.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- frontend/src/workstream/surfaces/**
- frontend/src/workstream/types/surfaces.ts
- frontend/src/workstream-user-admin-vertical.contract.test.mjs
- frontend/src/workstream-organization-admin-vertical.contract.test.mjs
- src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
- src/main/java/ai/first/application/coreapp/useradmin/**

## Skills

- app-description-surface-modeling
- akka-web-ui-state-rendering
- akka-web-ui-testing

## Expected outputs

- `specs/user-admin-surface-navigation-tree/existing-surface-inventory.md`
- Updated `pending-tasks.md` only if survey discovers task-blocking gaps or task ordering needs repair.

## Required checks

- `git diff --check`
- `rg`/survey evidence recorded in the inventory for app-description, frontend, backend/workstream, and tests.

## Done criteria

- Every expected dashboard, user-branch, organization-branch, and system-message surface is classified.
- Existing artifacts are mapped to surface ids/contracts where possible.
- Missing surfaces and obsolete/conflicting implementations are explicitly listed.
- Next implementation/spec task can proceed without guessing what exists.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: planning/survey only; no runtime mutation.
- Role-specific dashboard / surface: surveys `surface-user-admin-dashboard`, user branch, and organization branch.
- Surface graph node/action edge: inventory only.
- Governed-tool id and exposure: none, docs-only survey.
- Capability id: none, docs-only survey.
- AuthContext / roles / tenant scope: identify but do not change SaaS Owner/Tenant Admin/Customer Admin scope behavior.
- Akka substrate: docs/planning only.
- API / frontend / realtime path: inventory only.
- Audit/work trace requirements: inventory trace/audit expectations, no runtime emission.
- Local validation path: `git diff --check` and search evidence.
