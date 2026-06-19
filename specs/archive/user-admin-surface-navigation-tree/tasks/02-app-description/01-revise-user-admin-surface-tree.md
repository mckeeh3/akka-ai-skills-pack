# TASK-UASNT-02-001: Revise app-description User Admin surface tree

## Objective

Update the app-description User Admin surface contract to make the dashboard-trunk/directory-branch model explicit, including dashboard branch actions, directory roots, descendant surfaces, branch-return actions, payload fields, auth, traces, states, and tests.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/conversation-capture.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- specs/user-admin-surface-navigation-tree/backlog/01-user-admin-navigation-tree-build-backlog.md
- skills-pack/docs/structured-surface-contracts.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- app-description/domains/core-starter/realization/traceability.md

## Skills

- app-description-surface-modeling
- app-description-change-impact
- app-description-ui

## Expected outputs

- Updated `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`.
- Updated traceability/realization docs if needed.
- Optional update to mini-project notes if app-description changes affect task order.

## Required checks

- `git diff --check`
- Focused `rg` proof that app-description contains dashboard trunk, user directory branch, organization directory branch, Show users/Show organizations return actions, and authorization expectations.

## Done criteria

- Surface-description sufficiency review passes for the revised navigation tree.
- Implementation tasks can inherit required surface ids, action ids, auth, states, trace links, and test expectations.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: app-description capture only.
- Role-specific dashboard / surface: `surface-user-admin-dashboard` as trunk; `surface-user-admin-users` and `surface-user-admin-organization-directory` as branch roots.
- Surface graph node/action edge: dashboard -> directories; directories -> descendants; descendants -> branch root.
- Governed-tool id and exposure: browser-tool/surface-action mappings described, not implemented.
- Capability id: `user_admin.list_members`, `saas_owner.organization.list`, and branch descendant capabilities as described.
- AuthContext / roles / tenant scope: SaaS Owner/App Admin organization branch; Tenant/Customer scoped user branch; backend denial expectations.
- Akka substrate: app-description/docs only.
- API / frontend / realtime path: described for later implementation.
- Audit/work trace requirements: described for allowed, denied, no-op, stale, and consequential actions.
- Local validation path: `git diff --check` and focused search proof.
