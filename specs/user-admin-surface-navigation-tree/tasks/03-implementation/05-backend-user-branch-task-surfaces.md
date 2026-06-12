# TASK-UASNT-03-005: Implement backend User branch task/confirmation navigation surfaces

## Objective

Implement backend/workstream runtime surfaces and action routing for the app-description-required User branch task/confirmation descendants that are currently described but absent from the real workstream path.

## Required reads

- AGENTS.md
- specs/user-admin-surface-navigation-tree/README.md
- specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- specs/user-admin-surface-navigation-tree/existing-surface-inventory.md
- app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
- src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
- src/main/java/ai/first/application/coreapp/useradmin/**
- src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java

## Skills

- akka-basic-user-admin
- akka-http-endpoint-component-client
- akka-http-endpoint-testing

## Expected outputs

- Backend/workstream surfaces and action results for:
  - `surface-user-admin-invitation-create`
  - `surface-user-admin-invitation-resend-confirmation`
  - `surface-user-admin-invitation-revoke-confirmation`
  - `surface-user-admin-membership-status-confirmation`
  - `surface-user-admin-support-access-grant`
  - `surface-user-admin-support-access-revoke-confirmation`
  - `surface-user-admin-identity-exception-review`
- Every new User branch descendant includes `branchRootSurfaceId=surface-user-admin-users`, `branchReturnActionId=action-user-admin-show-users`, safe trace/correlation metadata, and backend-authorized action routing.
- Focused backend tests for opening each surface and returning to User Directory.

## Required checks

- `git diff --check`
- `mvn -q -Dtest=WorkstreamServiceTest test`

## Done criteria

- Real backend/workstream API path can open every app-description-required User branch descendant at starter scope.
- Consequential submissions remain fail-closed, idempotent/audited where already implemented, or explicitly return safe system-message/blocked states without fake success.
- Missing/forbidden/stale targets return safe system-message behavior without leaking hidden users, raw provider ids, invitation tokens, raw JWTs, or secrets.
