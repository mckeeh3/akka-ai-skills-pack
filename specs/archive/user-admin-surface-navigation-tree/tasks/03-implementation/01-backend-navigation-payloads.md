# TASK-UASNT-03-001: Add backend workstream navigation metadata and branch actions

## Objective

Update backend/workstream surface payload production and action handling so the dashboard, directories, and descendant surfaces expose explicit backend-authorized navigation metadata/actions for the tree.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- specs/user-admin-surface-navigation-tree/backlog/01-user-admin-navigation-tree-build-backlog.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
- src/main/java/ai/first/application/coreapp/useradmin/**
- src/test/java/ai/first/application/coreapp/useradmin/**
- src/test/java/ai/first/application/coreapp/workstream/**

## Skills

- akka-basic-user-admin
- akka-http-endpoint-component-client
- akka-http-endpoint-testing

## Expected outputs

- Backend/workstream DTO or payload changes for branch navigation metadata.
- Dashboard action for Organization Directory when authorized.
- Descendant branch-return actions to User Directory or Organization Directory.
- Focused backend tests.

## Required checks

- `git diff --check`
- Focused `mvn test` for changed User Admin/workstream tests.

## Done criteria

- Backend returns authorized dashboard -> directory actions and descendant -> directory return actions.
- Unauthorized Organization Directory access is omitted or denied safely server-side.
- Tests cover SaaS Owner allowed, Tenant/Customer denied/omitted, and safe branch-return payloads.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: navigation/action routing; dashboard attention objects remain actionable.
- Role-specific dashboard / surface: `surface-user-admin-dashboard`, `surface-user-admin-users`, `surface-user-admin-organization-directory`, descendants.
- Surface graph node/action edge: dashboard -> users/organizations; descendants -> users/organizations.
- Governed-tool id and exposure: human-backed browser-tool/surface actions via workstream API.
- Capability id: `user_admin.list_members`, `saas_owner.organization.list`, descendant read/action capabilities.
- AuthContext / roles / tenant scope: SaaS Owner/App Admin, Tenant Admin, Customer Admin, Auditor; selected tenant/customer scope and denials.
- Akka substrate: backend services/views/endpoints as currently implemented.
- API / frontend / realtime path: workstream surface/action API payloads.
- Audit/work trace requirements: consequential actions audited; navigation/read denials traceable as currently supported.
- Local validation path: focused backend tests.
