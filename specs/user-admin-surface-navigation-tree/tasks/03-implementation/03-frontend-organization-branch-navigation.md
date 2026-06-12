# TASK-UASNT-03-003: Implement Organization Directory branch frontend navigation

## Objective

Implement or revise frontend Organization Admin surfaces so authorized dashboard users can open Organization Directory and all organization-branch descendants expose a working Show organizations/Back to organizations action.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- frontend/src/workstream/surfaces/**
- frontend/src/workstream/types/surfaces.ts
- frontend/src/workstream-organization-admin-vertical.contract.test.mjs

## Skills

- akka-web-ui-apps
- akka-web-ui-state-rendering
- akka-web-ui-forms-validation
- akka-web-ui-accessibility-responsive
- akka-web-ui-testing

## Expected outputs

- Frontend Organization Directory/detail/create/rename/suspend/reactivate surfaces revised or implemented.
- Frontend tests for organization branch traversal, branch-return actions, and unauthorized omission/denial.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Authorized SaaS Owner/App Admin dashboard -> Organization Directory works.
- Organization branch descendant -> Show organizations/Back to organizations works.
- Tenant Admin/Customer Admin contexts do not receive unauthorized organization branch access and direct attempts render safe denials.
- Organization language remains browser-facing; Tenant isolation remains backend/audit-facing.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: Organization administration branch for SaaS Owner/App Admin.
- Role-specific dashboard / surface: `surface-user-admin-dashboard`, `surface-user-admin-organization-directory`, organization descendants.
- Surface graph node/action edge: dashboard -> organization directory; organization descendants -> organization directory.
- Governed-tool id and exposure: `manage-organizations` browser-tool/surface actions.
- Capability id: `saas_owner.organization.list/read/create/rename/suspend/reactivate` or current equivalent.
- AuthContext / roles / tenant scope: selected SaaS Owner/App Admin context; Tenant/Customer denials.
- Akka substrate: frontend workstream surfaces using existing HTTP/workstream API.
- API / frontend / realtime path: React/Vite workstream shell and API client path.
- Audit/work trace requirements: trace ids rendered; consequential actions audited by backend.
- Local validation path: frontend tests/typecheck.
